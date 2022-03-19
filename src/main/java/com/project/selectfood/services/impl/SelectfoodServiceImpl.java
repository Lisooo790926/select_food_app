package com.project.selectfood.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private FindingResult sendAPIRequest(final String url) {

        HttpGet getRequest = new HttpGet(url);

        try {
            HttpClient client = HttpClientBuilder.create().build();
            final HttpResponse response = client.execute(getRequest);

            final String jsonString = EntityUtils.toString(response.getEntity());
//            log.info("response is [{}]", jsonString);

            return objectMapper.readValue(jsonString, FindingResult.class);
        } catch (Exception e) {
            log.error("failed in getting response from api with error ", e);
        }

        return new FindingResult();
    }

    @Override
    public FindingResult selectFoodByRandom(Map<String, String> attributes) {

        FindingResult result = new FindingResult();

        final String address = attributes.getOrDefault(ADDRESS, Strings.EMPTY);
        FindingResult curPlace = findPlaceByAddress(address);

        final List<FindingPlace> candidates = curPlace.getCandidates();
        if (isUnAvailableCandidates(candidates)) return result;

        Location location = candidates.get(0).getGeometry().get(LOCATION);
        attributes.put(LOCATION, location.getLat() + COMA + location.getLng());

        return searchNearbyPlaces(attributes);
    }

    private boolean isUnAvailableCandidates(final List<FindingPlace> candidates) {
        return CollectionUtils.isEmpty(candidates)
                || Objects.isNull(candidates.get(0))
                || Objects.isNull(candidates.get(0).getGeometry())
                || Objects.isNull(candidates.get(0).getGeometry().get(LOCATION));
    }

}
