package com.project.selectfood.repository;

import com.project.selectfood.models.FindingHistory;
import com.project.selectfood.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FindingHistoryRepo extends JpaRepository<FindingHistory, Long> {

    List<FindingHistory> findAllByUser(final User user);
}
