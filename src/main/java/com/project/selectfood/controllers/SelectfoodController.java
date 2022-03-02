package com.project.selectfood.controllers;

import com.project.selectfood.data.FindingPlace;
import com.project.selectfood.data.FindingResult;
import com.project.selectfood.services.impl.SelectfoodServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/selectfood")
public class SelectfoodController {

    private static final String RATING = "rating";
    private static final String USER_RATE_TOTAL = "user_ratings_total";
    private static final String RANDOM_TIME = "random_time";

    @Resource
    private SelectfoodServiceImpl selectfoodService;

    @PostMapping("/random")
    public FindingPlace selectFoodByRandom(@RequestBody final Map<String, String> attributes) {
        log.info("Coming map is [{}]", attributes);
        FindingResult result = selectfoodService.selectFoodByRandom(attributes);

        // filter by rating and total rating
        final String rating = attributes.getOrDefault(RATING, Strings.EMPTY);
        final String user_ratings_total = attributes.getOrDefault(USER_RATE_TOTAL, Strings.EMPTY);
        final String randomTime_str = attributes.getOrDefault(RANDOM_TIME, Strings.EMPTY);

        if (CollectionUtils.isEmpty(result.getResults())) return new FindingPlace();

        double rating_limit, user_ratings_total_limit;
        int randomTimes;
        try {
            rating_limit = Double.parseDouble(rating);
            user_ratings_total_limit = Double.parseDouble(user_ratings_total);
            randomTimes = Integer.parseInt(randomTime_str);
        } catch (NumberFormatException e) {
            log.error("The format is wrong", e);
            FindingPlace findingPlace = new FindingPlace();
            findingPlace.setErrorMessage("The format is wrong");
            return findingPlace;
        }

        final List<FindingPlace> update = result.getResults().stream()
                .filter(place -> (Objects.nonNull(place.getUser_ratings_total()) && place.getUser_ratings_total() >= user_ratings_total_limit)
                        && (Objects.nonNull(place.getRating()) && place.getRating() >= rating_limit)).collect(Collectors.toList());

        log.info("getting random result");
        return getMaxRandomResult(randomTimes, update);
    }

    private FindingPlace getMaxRandomResult(int randomTimes, final List<FindingPlace> update) {
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

    @GetMapping
    public String helloWorld() {
        return "hello world";
    }
}
