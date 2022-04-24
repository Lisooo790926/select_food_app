package com.project.selectfood.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindingResult {

    private List<FindingPlace> candidates;
    private List<FindingPlace> results;
    private String status;

}
