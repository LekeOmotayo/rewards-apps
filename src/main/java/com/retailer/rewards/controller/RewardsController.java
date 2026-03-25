package com.retailer.rewards.controller;

import com.retailer.rewards.model.CustomerRewardsResponse;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import com.retailer.rewards.service.RewardsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    private final RewardsService rewardsService;
    private final TransactionRepository transactionRepository;

    public RewardsController(RewardsService rewardsService,
                             TransactionRepository transactionRepository) {
        this.rewardsService = rewardsService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * GET /api/rewards
     * Returns reward points for ALL customers over the last 3 months.
     */
    @GetMapping
    public ResponseEntity<List<CustomerRewardsResponse>> getAllRewards() {
        return ResponseEntity.ok(rewardsService.getAllCustomerRewards());
    }

    /**
     * GET /api/rewards/{customerId}
     * Returns reward points for a SPECIFIC customer over the last 3 months.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerRewards(@PathVariable String customerId) {
        try {
            return ResponseEntity.ok(rewardsService.getCustomerRewards(customerId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/rewards/range?startDate=2024-01-01&endDate=2024-03-31
     * Returns reward points for all customers within a custom date range.
     */
    @GetMapping("/range")
    public ResponseEntity<List<CustomerRewardsResponse>> getRewardsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(rewardsService.getRewardsByDateRange(startDate, endDate));
    }

    /**
     * GET /api/rewards/transactions
     * Returns all raw transactions (for debugging/verification).
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    /**
     * POST /api/rewards/calculate?amount=120
     * Calculates points for a single purchase amount (for testing the formula).
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculatePointsForAmount(@RequestParam double amount) {
        long points = rewardsService.calculatePoints(amount);
        return ResponseEntity.ok(
                java.util.Map.of(
                        "amount", amount,
                        "points", points,
                        "breakdown", buildBreakdown(amount)
                )
        );
    }

    private String buildBreakdown(double amount) {
        if (amount > 100) {
            long over100 = (long)(amount - 100);
            return String.format("2x$%d + 1x$50 = %d points", over100,
                    rewardsService.calculatePoints(amount));
        } else if (amount > 50) {
            long between50and100 = (long)(amount - 50);
            return String.format("1x$%d = %d points", between50and100,
                    rewardsService.calculatePoints(amount));
        }
        return "0 points (purchase under $50)";
    }
}
