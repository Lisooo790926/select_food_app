package com.project.selectfood.services;

import com.project.selectfood.data.FindingResult;

import java.util.Map;

public interface SelectfoodService {

    FindingResult findPlaceByAddress(String address);

    FindingResult searchNearbyPlaces(Map<String, String> attributes);

    FindingResult selectFoodByRandom(Map<String, String> attributes);
}
