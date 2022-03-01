package com.project.selectfood.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindingPlace {

    private Map<String, Location> geometry;
    private String name;
    private Integer price_level;
    private Double rating;
    private Integer user_rating_total;
    private String formatted_address;
}
