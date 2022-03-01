package com.project.selectfood.controllers;

import com.project.selectfood.data.FindingPlace;
import com.project.selectfood.data.FindingResult;
import com.project.selectfood.services.impl.SelectfoodServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/selectfood")
public class SelectfoodController {

    private static final String RATING = "rating";
    private static final String USER_RATE_TOTAL = "user_ratings_total";

    @Resource
    private SelectfoodServiceImpl selectfoodService;

    @PostMapping("/random")
    public FindingResult selectFoodByRandom(final Map<String, String> attributes) {
        log.info("Coming map is [{}]", attributes);
        FindingResult result = selectfoodService.selectFoodByRandom(attributes);

        // filter by rating and total rating
        final String rating = attributes.getOrDefault(RATING, Strings.EMPTY);
        final String user_ratings_total = attributes.getOrDefault(USER_RATE_TOTAL, Strings.EMPTY);

        if (CollectionUtils.isEmpty(result.getResults())) return result;

        Double rating_limit, user_ratings_total_limit;
        try {
            rating_limit = Double.valueOf(rating);
            user_ratings_total_limit = Double.valueOf(user_ratings_total);
        } catch (NumberFormatException e) {
            log.error("The format is wrong");
            return null;
        }

        List<FindingPlace> update = result.getResults().stream().filter(place -> place.getUser_rating_total() >= user_ratings_total_limit && place.getRating() >= rating_limit).collect(Collectors.toList());
        result.setResults(update);
        return result;
    }

    @GetMapping
    public String helloWorld() {
        return "hello world";
    }
}
