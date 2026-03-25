package com.retailer.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardsResponse {
    private String customerId;
    private String customerName;
    private Map<String, Long> pointsPerMonth;  // e.g. "2024-01" -> 150
    private Long totalPoints;
}
