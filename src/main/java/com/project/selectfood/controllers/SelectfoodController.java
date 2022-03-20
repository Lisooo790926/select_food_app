package com.project.selectfood.controllers;

import com.project.selectfood.data.AdditionalItem;
import com.project.selectfood.data.FindingPlace;
import com.project.selectfood.data.FindingResult;
import com.project.selectfood.data.Response;
import com.project.selectfood.services.impl.SelectfoodServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    public ResponseEntity<Response> selectFoodByRandom(@RequestBody final Map<String, String> attributes) {

        log.info("Coming map is [{}]", attributes);
        final FindingResult result = selectfoodService.selectFoodsByAddress(attributes);
        final List<FindingPlace> findingPlaces = selectfoodService.filterResult(attributes, result);
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("find the place result")
                        .data(Map.of("placeResult", selectfoodService.getMaxRandomResult(attributes, findingPlaces)))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/save/additional-item")
    public ResponseEntity<Response> saveAdditionalItem(@RequestBody AdditionalItem additionalItem) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("save the additional item")
                        .data(Map.of("saveItem", selectfoodService.save(additionalItem)))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/delete/additional-item/{code}")
    public ResponseEntity<Response> removeAdditionalItem(@PathVariable String code) {
        selectfoodService.removeAdditionItem(Long.valueOf(code));
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("remove the additional item")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/all/additional-item")
    public ResponseEntity<Response> getAdditionalItems() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("all additional items")
                        .data(Map.of("additionalItems", selectfoodService.getAllItems()))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/all/finding-history")
    public ResponseEntity<Response> getFindingHistories() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("all finding histories")
                        .data(Map.of("histories", selectfoodService.getAllHistories()))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/delete/finding-history/{code}")
    public ResponseEntity<Response> removeFindingHistory(@PathVariable String code) {
        selectfoodService.removeFindingHistory(Long.valueOf(code));
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("remove the finding history")
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
