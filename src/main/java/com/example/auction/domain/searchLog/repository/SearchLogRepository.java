package com.example.auction.domain.searchLog.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.auction.domain.searchLog.dto.response.SearchLogResponseDto;
import com.example.auction.domain.searchLog.entity.SearchLog;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

	@Query("SELECT new com.example.auction.domain.searchLog.dto.response.SearchLogResponseDto(s.keyword)" +
		"FROM SearchLog s " +
		"GROUP BY s.keyword " +
		"ORDER BY COUNT(s) DESC")
	List<SearchLogResponseDto> findPopularKeywords(Pageable pageable);

	List<SearchLog> findByKeyword(String keyword);

	@Query("select s from SearchLog s where s.createAt < :minusRange ")
	List<SearchLog> findKeywords(@Param("minusRange") LocalDateTime minusRange);
}
