package com.project.selectfood.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long code;

    @NotEmpty(message = "Restaurant name")
    private String name;

    @Column(unique = true)
    private LocalDateTime date;
}
