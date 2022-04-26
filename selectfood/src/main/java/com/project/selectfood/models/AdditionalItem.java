package com.project.selectfood.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long code;

    @NotEmpty(message = "Restaurant name")
    private String name;

    @Column(columnDefinition = "int default 1")
    private int count;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
