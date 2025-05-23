package com.example.auction.domain.test.repository;

import com.example.auction.domain.auctionbid.dto.BidRedisDto;
import com.example.auction.domain.test.TestRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionBidJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<BidRedisDto> bids) {
        String sql = "INSERT INTO auction_bid (bid_price, is_success, product_id, user_id) VALUES (?, ?, ?, ?)";
        // BatchPreparedStatementSetter 는 인덱스 기반
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                BidRedisDto bid = bids.get(i);
                ps.setLong(1, bid.getBidPrice());
                ps.setBoolean(2, false);
                ps.setLong(3, bid.getProductId());
                ps.setLong(4, bid.getUserId());
            }

            public int getBatchSize() {
                return bids.size();
            }
        });
    }
}
