package com.retailer.rewards.service;

import com.retailer.rewards.model.CustomerRewardsResponse;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardsService {

    private final TransactionRepository transactionRepository;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public RewardsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Calculate reward points for a single transaction amount.
     *
     * Rules:
     *  - 2 points per dollar spent over $100
     *  - 1 point per dollar spent between $50 and $100
     *
     * Example: $120 purchase = (2 x $20) + (1 x $50) = 90 points
     */
    public long calculatePoints(double amount) {
        long points = 0;

        if (amount > 100) {
            points += (long) ((amount - 100) * 2);
            points += 50; // 1 point for each dollar between $50 and $100
        } else if (amount > 50) {
            points += (long) (amount - 50);
        }

        return points;
    }

    /**
     * Get rewards for all customers over the last 3 months.
     */
    public List<CustomerRewardsResponse> getAllCustomerRewards() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3).withDayOfMonth(1);

        List<Transaction> transactions = transactionRepository
                .findByTransactionDateBetween(startDate, endDate);

        return buildRewardsResponse(transactions);
    }

    /**
     * Get rewards for a specific customer over the last 3 months.
     */
    public CustomerRewardsResponse getCustomerRewards(String customerId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3).withDayOfMonth(1);

        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);

        if (transactions.isEmpty()) {
            throw new NoSuchElementException("No transactions found for customer: " + customerId);
        }

        List<CustomerRewardsResponse> responses = buildRewardsResponse(transactions);
        return responses.get(0);
    }

    /**
     * Get rewards for all customers within a custom date range.
     */
    public List<CustomerRewardsResponse> getRewardsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository
                .findByTransactionDateBetween(startDate, endDate);
        return buildRewardsResponse(transactions);
    }

    /**
     * Build reward response objects grouped by customer and month.
     */
    private List<CustomerRewardsResponse> buildRewardsResponse(List<Transaction> transactions) {
        // Group transactions by customerId
        Map<String, List<Transaction>> byCustomer = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId));

        List<CustomerRewardsResponse> responses = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : byCustomer.entrySet()) {
            String customerId = entry.getKey();
            List<Transaction> customerTxns = entry.getValue();
            String customerName = customerTxns.get(0).getCustomerName();

            // Group by month and sum points
            Map<String, Long> pointsPerMonth = customerTxns.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getTransactionDate().format(MONTH_FORMATTER),
                            Collectors.summingLong(t -> calculatePoints(t.getAmount()))
                    ));

            // Sort months chronologically
            Map<String, Long> sortedPointsPerMonth = new TreeMap<>(pointsPerMonth);

            long totalPoints = sortedPointsPerMonth.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            responses.add(CustomerRewardsResponse.builder()
                    .customerId(customerId)
                    .customerName(customerName)
                    .pointsPerMonth(sortedPointsPerMonth)
                    .totalPoints(totalPoints)
                    .build());
        }

        // Sort by customerId
        responses.sort(Comparator.comparing(CustomerRewardsResponse::getCustomerId));
        return responses;
    }
}
