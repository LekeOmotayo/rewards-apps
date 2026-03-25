package com.retailer.rewards.config;

import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds the H2 in-memory database with sample transaction data
 * covering a 3-month period (January - March 2024).
 *
 * Dataset covers 4 customers with varied spending patterns:
 *  - C001 Alice Johnson  : High spender, consistent across months
 *  - C002 Bob Smith      : Moderate spender, one big purchase
 *  - C003 Carol White    : Low spender, mostly under $100
 *  - C004 David Brown    : Mixed spender, some months inactive
 */
@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(TransactionRepository repo) {
        return args -> {
            repo.saveAll(List.of(

                // ── ALICE JOHNSON (C001) ──────────────────────────────
                // January
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(120.00).transactionDate(LocalDate.of(2024, 1, 5)).build(),
                // 2x20 + 1x50 = 90 pts
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(75.00).transactionDate(LocalDate.of(2024, 1, 15)).build(),
                // 1x25 = 25 pts
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(200.00).transactionDate(LocalDate.of(2024, 1, 28)).build(),
                // 2x100 + 1x50 = 250 pts
                // Jan total: 90 + 25 + 250 = 365 pts

                // February
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(130.00).transactionDate(LocalDate.of(2024, 2, 3)).build(),
                // 2x30 + 1x50 = 110 pts
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(55.00).transactionDate(LocalDate.of(2024, 2, 14)).build(),
                // 1x5 = 5 pts
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(95.00).transactionDate(LocalDate.of(2024, 2, 22)).build(),
                // 1x45 = 45 pts
                // Feb total: 110 + 5 + 45 = 160 pts

                // March
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(150.00).transactionDate(LocalDate.of(2024, 3, 7)).build(),
                // 2x50 + 1x50 = 150 pts
                Transaction.builder().customerId("C001").customerName("Alice Johnson")
                        .amount(110.00).transactionDate(LocalDate.of(2024, 3, 19)).build(),
                // 2x10 + 1x50 = 70 pts
                // Mar total: 150 + 70 = 220 pts

                // Alice GRAND TOTAL: 365 + 160 + 220 = 745 pts

                // ── BOB SMITH (C002) ──────────────────────────────────
                // January
                Transaction.builder().customerId("C002").customerName("Bob Smith")
                        .amount(49.99).transactionDate(LocalDate.of(2024, 1, 10)).build(),
                // 0 pts (under $50)
                Transaction.builder().customerId("C002").customerName("Bob Smith")
                        .amount(85.00).transactionDate(LocalDate.of(2024, 1, 20)).build(),
                // 1x35 = 35 pts
                // Jan total: 35 pts

                // February
                Transaction.builder().customerId("C002").customerName("Bob Smith")
                        .amount(300.00).transactionDate(LocalDate.of(2024, 2, 8)).build(),
                // 2x200 + 1x50 = 450 pts
                Transaction.builder().customerId("C002").customerName("Bob Smith")
                        .amount(60.00).transactionDate(LocalDate.of(2024, 2, 25)).build(),
                // 1x10 = 10 pts
                // Feb total: 460 pts

                // March
                Transaction.builder().customerId("C002").customerName("Bob Smith")
                        .amount(100.00).transactionDate(LocalDate.of(2024, 3, 12)).build(),
                // 1x50 = 50 pts (exactly $100 → no 2x tier)
                Transaction.builder().customerId("C002").customerName("Bob Smith")
                        .amount(45.00).transactionDate(LocalDate.of(2024, 3, 28)).build(),
                // 0 pts
                // Mar total: 50 pts

                // Bob GRAND TOTAL: 35 + 460 + 50 = 545 pts

                // ── CAROL WHITE (C003) ────────────────────────────────
                // January
                Transaction.builder().customerId("C003").customerName("Carol White")
                        .amount(30.00).transactionDate(LocalDate.of(2024, 1, 8)).build(),
                // 0 pts
                // Jan total: 0 pts

                // February
                Transaction.builder().customerId("C003").customerName("Carol White")
                        .amount(65.00).transactionDate(LocalDate.of(2024, 2, 11)).build(),
                // 1x15 = 15 pts
                Transaction.builder().customerId("C003").customerName("Carol White")
                        .amount(72.50).transactionDate(LocalDate.of(2024, 2, 20)).build(),
                // 1x22 = 22 pts
                // Feb total: 37 pts

                // March
                Transaction.builder().customerId("C003").customerName("Carol White")
                        .amount(105.00).transactionDate(LocalDate.of(2024, 3, 3)).build(),
                // 2x5 + 1x50 = 60 pts
                Transaction.builder().customerId("C003").customerName("Carol White")
                        .amount(88.00).transactionDate(LocalDate.of(2024, 3, 16)).build(),
                // 1x38 = 38 pts
                Transaction.builder().customerId("C003").customerName("Carol White")
                        .amount(40.00).transactionDate(LocalDate.of(2024, 3, 29)).build(),
                // 0 pts
                // Mar total: 98 pts

                // Carol GRAND TOTAL: 0 + 37 + 98 = 135 pts

                // ── DAVID BROWN (C004) ────────────────────────────────
                // January
                Transaction.builder().customerId("C004").customerName("David Brown")
                        .amount(250.00).transactionDate(LocalDate.of(2024, 1, 18)).build(),
                // 2x150 + 1x50 = 350 pts
                // Jan total: 350 pts

                // February — no transactions (David inactive in Feb)

                // March
                Transaction.builder().customerId("C004").customerName("David Brown")
                        .amount(115.00).transactionDate(LocalDate.of(2024, 3, 9)).build(),
                // 2x15 + 1x50 = 80 pts
                Transaction.builder().customerId("C004").customerName("David Brown")
                        .amount(175.00).transactionDate(LocalDate.of(2024, 3, 22)).build()
                // 2x75 + 1x50 = 200 pts
                // Mar total: 280 pts

                // David GRAND TOTAL: 350 + 0 + 280 = 630 pts
            ));

            System.out.println("✅ Sample dataset loaded: 4 customers, 3 months of transactions.");
        };
    }
}
