package com.retailer.rewards;

import com.retailer.rewards.model.CustomerRewardsResponse;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import com.retailer.rewards.service.RewardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardsService rewardsService;

    // ─── Points Calculation Tests ──────────────────────────────────────────────

    @Test
    @DisplayName("Amount under $50 earns 0 points")
    void testUnder50() {
        assertEquals(0, rewardsService.calculatePoints(30.00));
        assertEquals(0, rewardsService.calculatePoints(49.99));
    }

    @Test
    @DisplayName("Exactly $50 earns 0 points")
    void testExactly50() {
        assertEquals(0, rewardsService.calculatePoints(50.00));
    }

    @Test
    @DisplayName("Amount between $50 and $100 earns 1 point per dollar over $50")
    void testBetween50And100() {
        assertEquals(25, rewardsService.calculatePoints(75.00));  // 1x25 = 25
        assertEquals(45, rewardsService.calculatePoints(95.00));  // 1x45 = 45
    }

    @Test
    @DisplayName("Exactly $100 earns 50 points")
    void testExactly100() {
        assertEquals(50, rewardsService.calculatePoints(100.00)); // 1x50 = 50
    }

    @Test
    @DisplayName("$120 earns 90 points as per example in requirements")
    void testRequirementsExample() {
        // $120 = 2x$20 + 1x$50 = 40 + 50 = 90 pts
        assertEquals(90, rewardsService.calculatePoints(120.00));
    }

    @Test
    @DisplayName("Amount over $100 earns both tiers correctly")
    void testOver100() {
        assertEquals(250, rewardsService.calculatePoints(200.00)); // 2x100 + 1x50 = 250
        assertEquals(150, rewardsService.calculatePoints(150.00)); // 2x50  + 1x50 = 150
        assertEquals(110, rewardsService.calculatePoints(130.00)); // 2x30  + 1x50 = 110
    }

    // ─── Integration-style Tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Customer rewards grouped correctly by month")
    void testCustomerRewardsGroupedByMonth() {
        List<Transaction> mockTxns = List.of(
            Transaction.builder().customerId("C001").customerName("Alice")
                    .amount(120.00).transactionDate(LocalDate.of(2024, 1, 5)).build(),
            Transaction.builder().customerId("C001").customerName("Alice")
                    .amount(75.00).transactionDate(LocalDate.of(2024, 2, 10)).build()
        );

        when(transactionRepository.findByTransactionDateBetween(any(), any()))
                .thenReturn(mockTxns);

        List<CustomerRewardsResponse> responses = rewardsService.getAllCustomerRewards();

        assertEquals(1, responses.size());
        CustomerRewardsResponse response = responses.get(0);

        assertEquals("C001", response.getCustomerId());
        assertEquals(90L, response.getPointsPerMonth().get("2024-01")); // 2x20+1x50=90
        assertEquals(25L, response.getPointsPerMonth().get("2024-02")); // 1x25=25
        assertEquals(115L, response.getTotalPoints());
    }

    @Test
    @DisplayName("Multiple customers return separate reward summaries")
    void testMultipleCustomers() {
        List<Transaction> mockTxns = List.of(
            Transaction.builder().customerId("C001").customerName("Alice")
                    .amount(120.00).transactionDate(LocalDate.of(2024, 1, 5)).build(),
            Transaction.builder().customerId("C002").customerName("Bob")
                    .amount(200.00).transactionDate(LocalDate.of(2024, 1, 8)).build()
        );

        when(transactionRepository.findByTransactionDateBetween(any(), any()))
                .thenReturn(mockTxns);

        List<CustomerRewardsResponse> responses = rewardsService.getAllCustomerRewards();

        assertEquals(2, responses.size());
        assertEquals("C001", responses.get(0).getCustomerId());
        assertEquals(90L, responses.get(0).getTotalPoints());   // $120 -> 90 pts
        assertEquals("C002", responses.get(1).getCustomerId());
        assertEquals(250L, responses.get(1).getTotalPoints());  // $200 -> 250 pts
    }

    @Test
    @DisplayName("Transaction under $50 contributes 0 points to total")
    void testTransactionUnder50ContributesZero() {
        List<Transaction> mockTxns = List.of(
            Transaction.builder().customerId("C003").customerName("Carol")
                    .amount(30.00).transactionDate(LocalDate.of(2024, 1, 8)).build()
        );

        when(transactionRepository.findByTransactionDateBetween(any(), any()))
                .thenReturn(mockTxns);

        List<CustomerRewardsResponse> responses = rewardsService.getAllCustomerRewards();
        assertEquals(0L, responses.get(0).getTotalPoints());
    }
}
