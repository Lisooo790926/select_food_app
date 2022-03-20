package com.project.selectfood.controllers;

import com.project.selectfood.data.FindingPlace;
import com.project.selectfood.data.FindingResult;
import com.project.selectfood.services.impl.SelectfoodServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/selectfood")
public class SelectfoodController {

    @Resource
    private SelectfoodServiceImpl selectfoodService;

    @PostMapping("/random")
    public ResponseEntity<FindingPlace> selectFoodByRandom(@RequestBody final Map<String, String> attributes) {

        log.info("Coming map is [{}]", attributes);
        final FindingResult result = selectfoodService.selectFoodsByAddress(attributes);
        final List<FindingPlace> findingPlaces = selectfoodService.filterResult(attributes, result);
        return ResponseEntity.ok(selectfoodService.getMaxRandomResult(attributes, findingPlaces));
    }

    // TODO save additional item

    // TODO get all additional items

    // TODO save history

    // TODO add history
}
