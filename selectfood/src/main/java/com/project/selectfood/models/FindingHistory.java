package com.project.selectfood.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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

    private Date date;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
