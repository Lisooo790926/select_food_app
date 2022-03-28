package com.project.selectfood.repository;

import com.project.selectfood.data.FindingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingHistoryRepo extends JpaRepository<FindingHistory, Long> {
}
