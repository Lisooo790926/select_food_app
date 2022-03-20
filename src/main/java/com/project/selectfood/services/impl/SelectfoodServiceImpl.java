package com.project.selectfood.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.selectfood.constants.SelectFoodConstant;
import com.project.selectfood.data.FindingPlace;
import com.project.selectfood.data.FindingResult;
import com.project.selectfood.data.Location;
import com.project.selectfood.services.SelectfoodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SelectfoodServiceImpl implements SelectfoodService {

    private static final String ADDRESS = "address";
    private static final String LOCATION = "location";
    private static final String RADIUS = "radius";
    private static final String MAX_PRICE = "maxprice";
    private static final String COMA = ",";

    private static final String RADIUS_HEAD = "&radius=";
    private static final String INPUT_HAED = "&input=";
    private static final String LOCATION_HEAD = "&location=";
    private static final String KEY_HEAD = "&key=";
    private static final String MAX_PRICE_HEAD = "&maxprice=";

    private static final String FIELDS = "&fields=formatted_address%2Cname%2Crating%2Copening_hours%2Cgeometry";
    private static final String INPUTTYPE = "&inputtype=textquery";
    private static final String TYPES = "&types=restaurant";
    private static final String OPENNOW = "&opennow=true";
    private static final String LANGUAGE = "&language=zh-TW";

    @Resource
    private ObjectMapper objectMapper;

    @Value("${google.find.place.url}")
    private String findPlaceUrl;

    @Value("${google.search.nearby.url}")
    private String searchNearbyUrl;

    @Value("${google.test.api.key}")
    private String apikey;

    @Override
    public FindingResult findPlaceByAddress(String address) {
        final String url = findPlaceUrl + FIELDS + INPUT_HAED + address + INPUTTYPE + KEY_HEAD + apikey;
        log.info("Finding address [{}] in google api", address);
        return sendAPIRequest(url);
    }

    @Override
    public FindingResult searchNearbyPlaces(Map<String, String> attributes) {

        final String location = attributes.getOrDefault(LOCATION, Strings.EMPTY);
        final String radius = attributes.getOrDefault(RADIUS, Strings.EMPTY);
        final String priceLevel = attributes.getOrDefault(MAX_PRICE, Strings.EMPTY);
        final String url = searchNearbyUrl + LOCATION_HEAD + location + RADIUS_HEAD + radius + TYPES + OPENNOW + LANGUAGE + MAX_PRICE_HEAD + priceLevel + KEY_HEAD + apikey;

        log.info("Search nearby places by location [{}] and radius [{}] in google api", location, radius);
        return sendAPIRequest(url);
    }

    @Override
    public FindingResult selectFoodsByAddress(Map<String, String> attributes) {

        FindingResult result = new FindingResult();

        final String address = attributes.getOrDefault(ADDRESS, Strings.EMPTY);
        FindingResult curPlace = findPlaceByAddress(address);

        final List<FindingPlace> candidates = curPlace.getCandidates();
        if (isUnAvailableCandidates(candidates)) return result;

        Location location = candidates.get(0).getGeometry().get(LOCATION);
        attributes.put(LOCATION, location.getLat() + COMA + location.getLng());

        return searchNearbyPlaces(attributes);
    }

    @Override
    public List<FindingPlace> filterResult(final Map<String, String> attributes, final FindingResult result) {

        if (CollectionUtils.isEmpty(result.getResults())) return Collections.emptyList();

        try {
            // filter by rating and total rating
            final String rating = attributes.get(SelectFoodConstant.RATING);
            final String user_ratings_total = attributes.get(SelectFoodConstant.USER_RATE_TOTAL);

            return filterPlacesByLimit(result, rating, user_ratings_total);
        } catch (NumberFormatException e) {
            log.error("The format is wrong", e);
            return Collections.emptyList();
        }
    }

    @Override
    public FindingPlace getMaxRandomResult(final Map<String, String> attributes, final List<FindingPlace> update) {

        final String randomTime_str = attributes.get(SelectFoodConstant.RANDOM_TIME);
        int randomTimes = Strings.isNotEmpty(randomTime_str) && randomTime_str.matches("^[0-9]+$]") ?
                Integer.parseInt(randomTime_str) : SelectFoodConstant.DEFAULT_RANDOM_TIME;

        final Map<FindingPlace, Integer> randomResult = new HashMap<>();
        PrimitiveIterator.OfInt iterator = new Random().ints(0, update.size()).iterator();
        int max = 0;
        FindingPlace result = new FindingPlace();
        while (randomTimes-- > 0) {
            FindingPlace key = update.get(iterator.nextInt());
            int count = randomResult.getOrDefault(key, 0) + 1;
            randomResult.put(key, count);
            if (count > max) {
                max = count;
                result = key;
            }
        }
        return result;
    }

    private List<FindingPlace> filterPlacesByLimit(final FindingResult result, final String rating,
                                                   final String userRatingsTotal) {

        double rating_limit = Strings.isNotBlank(rating) && rating.matches("^[0-9]+.?[0-9]*$") ?
                Double.parseDouble(rating) : SelectFoodConstant.DEFAULT_RATING;
        double user_ratings_total_limit = Strings.isNotBlank(userRatingsTotal) && userRatingsTotal.matches("^[0-9]+.?[0-9]*$") ?
                Double.parseDouble(userRatingsTotal) : SelectFoodConstant.DEFAULT_USER_RATE_TOTAL;

        return result.getResults().stream()
                .filter(place -> isLargerThanLimit(place, user_ratings_total_limit, rating_limit))
                .collect(Collectors.toList());
    }

    private boolean isLargerThanLimit(final FindingPlace place, double user_ratings_total_limit, double rating_limit) {
        return (Objects.nonNull(place.getUser_ratings_total())
                && Double.compare(place.getUser_ratings_total(), user_ratings_total_limit) >= 0)
                && (Objects.nonNull(place.getRating()) && Double.compare(place.getRating(), rating_limit) >= 0);
    }

    private boolean isUnAvailableCandidates(final List<FindingPlace> candidates) {
        return CollectionUtils.isEmpty(candidates)
                || Objects.isNull(candidates.get(0))
                || Objects.isNull(candidates.get(0).getGeometry())
                || Objects.isNull(candidates.get(0).getGeometry().get(LOCATION));
    }

    private FindingResult sendAPIRequest(final String url) {

        HttpGet getRequest = new HttpGet(url);

        try {
            HttpClient client = HttpClientBuilder.create().build();
            final HttpResponse response = client.execute(getRequest);

            final String jsonString = EntityUtils.toString(response.getEntity());
            log.info("response is [{}]", jsonString);

            return objectMapper.readValue(jsonString, FindingResult.class);
        } catch (Exception e) {
            log.error("failed in getting response from api with error ", e);
        }

        return new FindingResult();
    }

}
