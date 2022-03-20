package com.project.selectfood.services;

import com.project.selectfood.data.AdditionalItem;
import com.project.selectfood.data.FindingHistory;
import com.project.selectfood.data.FindingPlace;
import com.project.selectfood.data.FindingResult;

import java.util.List;
import java.util.Map;

public interface SelectfoodService {

    FindingResult findPlaceByAddress(String address);

    FindingResult searchNearbyPlaces(Map<String, String> attributes);

    FindingResult selectFoodsByAddress(Map<String, String> attributes);

    List<FindingPlace> filterResult(final Map<String, String> attributes, final FindingResult result);

    FindingPlace getMaxRandomResult(final Map<String, String> attributes, final List<FindingPlace> update);

    List<AdditionalItem> getAllItems();

    AdditionalItem save(final AdditionalItem item);

    void removeAdditionItem(final Long code);

    List<FindingHistory> getAllHistories();

    FindingHistory save(final FindingPlace history);

    void removeFindingHistory(final Long code);
}
