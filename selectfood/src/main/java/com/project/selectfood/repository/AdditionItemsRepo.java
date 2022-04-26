package com.project.selectfood.repository;

import com.project.selectfood.models.AdditionalItem;
import com.project.selectfood.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdditionItemsRepo extends JpaRepository<AdditionalItem, Long> {

    List<AdditionalItem> findAllByUser(final User user);

}
