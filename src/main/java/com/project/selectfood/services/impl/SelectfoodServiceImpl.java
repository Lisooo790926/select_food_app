package com.project.selectfood.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.selectfood.constants.SelectFoodConstant;
import com.project.selectfood.data.*;
import com.project.selectfood.repository.AdditionItemsRepo;
import com.project.selectfood.repository.FindingHistoryRepo;
import com.project.selectfood.services.SelectfoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelectfoodServiceImpl implements SelectfoodService {

    private static final String COMA = ",";

    private final ObjectMapper objectMapper;
    private final AdditionItemsRepo additionItemsRepo;
    private final FindingHistoryRepo findingHistoryRepo;

    @Value("${google.host.schema}")
    private String hostSchema;

    @Value("${google.place.url}")
    private String placeApiUrl;

    @Value("${google.findfromtext.url}")
    private String findFromTextUrl;

    @Value("${google.searchnearby.url}")
    private String searchNearbyUrl;

    @Value("${google.api.key}")
    private String apikey;

    @Override
    public FindingResult findPlaceByAddress(String address) {

        final URIBuilder builder = new URIBuilder();
        builder.setPath(findFromTextUrl)
                .addParameter(SelectFoodConstant.FIELDS, "formatted_address%2Cname%2Crating%2Copening_hours%2Cgeometry")
                .addParameter(SelectFoodConstant.INPUT, address)
                .addParameter(SelectFoodConstant.INPUTTYPE, "textquery")
                .addParameter(SelectFoodConstant.OPENNOW, "true")
                .addParameter(SelectFoodConstant.LANGUAGE, "zh-TW");

        final String url = getUrlByBuilder(builder);
        log.info("Finding address [{}] in google api [{}]", address, url);
        return sendAPIRequest(url);
    }

    @Override
    public FindingResult searchNearbyPlaces(Map<String, String> attributes) {

        final String location = attributes.getOrDefault(SelectFoodConstant.LOCATION, Strings.EMPTY);
        final String radius = attributes.getOrDefault(SelectFoodConstant.RADIUS, SelectFoodConstant.DEFAULT_RADIUS);
        final String priceLevel = attributes.getOrDefault(SelectFoodConstant.MAXPRICE, Strings.EMPTY);

        final URIBuilder builder = new URIBuilder();
        builder.setPath(searchNearbyUrl)
                .addParameter(SelectFoodConstant.LOCATION, location)
                .addParameter(SelectFoodConstant.RADIUS, radius)
                .addParameter(SelectFoodConstant.TYPES, "restaurant")
                .addParameter(SelectFoodConstant.OPENNOW, "true")
                .addParameter(SelectFoodConstant.LANGUAGE, "zh-TW")
                .addParameter(SelectFoodConstant.MAXPRICE, priceLevel)
                .addParameter(SelectFoodConstant.KEY, apikey);

        final String url = getUrlByBuilder(builder);
        log.info("Search nearby places by location [{}] and radius [{}] in google api", location, radius);
        return sendAPIRequest(url);
    }

    @Override
    public FindingResult selectFoodsByAddress(Map<String, String> attributes) {

        FindingResult result = new FindingResult();

        final String address = attributes.getOrDefault(SelectFoodConstant.ADDRESS, Strings.EMPTY);
        FindingResult curPlace = findPlaceByAddress(address);

        final List<FindingPlace> candidates = curPlace.getCandidates();
        if (isUnAvailableCandidates(candidates)) return result;

        Location location = candidates.get(0).getGeometry().get(SelectFoodConstant.LOCATION);
        attributes.put(SelectFoodConstant.LOCATION, location.getLat() + COMA + location.getLng());

        return searchNearbyPlaces(attributes);
    }

    @Override
    public List<FindingPlace> filterResult(final Map<String, String> attributes, final FindingResult result) {

        if (CollectionUtils.isEmpty(result.getResults())) return Collections.emptyList();

        try {
            final String rating = attributes.get(SelectFoodConstant.RATING);
            final String user_ratings_total = attributes.get(SelectFoodConstant.USER_RATE_TOTAL);

            final List<FindingPlace> additionalItems = getAllItems().stream().map(this::populate).collect(Collectors.toList());
            final List<FindingPlace> findingPlaces = filterPlacesByLimit(result, rating, user_ratings_total);
            findingPlaces.addAll(additionalItems);

            return findingPlaces;

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
        save(result);
        return result;
    }

    @Override
    public List<AdditionalItem> getAllItems() {
        return additionItemsRepo.findAll();
    }

    @Transactional
    @Override
    public AdditionalItem save(AdditionalItem item) {
        return additionItemsRepo.save(item);
    }

    @Transactional
    @Override
    public void removeAdditionItem(Long code) {
        additionItemsRepo.deleteById(code);
    }

    @Override
    public List<FindingHistory> getAllHistories() {
        return findingHistoryRepo.findAll();
    }

    @Transactional
    @Override
    public FindingHistory save(FindingPlace place) {
        return findingHistoryRepo.save(populate(place));
    }

    @Transactional
    @Override
    public void removeFindingHistory(Long code) {
        findingHistoryRepo.deleteById(code);
    }

    private String getUrlByBuilder(final URIBuilder builder) {
        try {
            return builder.setScheme(hostSchema).setHost(placeApiUrl)
                    .build().toString();
        } catch (URISyntaxException e) {
            log.error("Syntax Exception while building url");
            throw new IllegalArgumentException("URI building exception ", e);
        }
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
                || Objects.isNull(candidates.get(0).getGeometry().get(SelectFoodConstant.LOCATION));
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

    private FindingHistory populate(final FindingPlace place) {
        FindingHistory history = new FindingHistory();
        history.setDate(LocalDateTime.now());
        history.setName(place.getName());
        return history;
    }

    private FindingPlace populate(final AdditionalItem item) {
        FindingPlace place = new FindingPlace();
        place.setName(item.getName());
        return place;
    }

}
