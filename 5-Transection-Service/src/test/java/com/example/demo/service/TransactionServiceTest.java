package com.example.demo.service;

import com.example.demo.client.AccountClient;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionType;
import com.example.demo.repo.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private TransactionService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Deposit Test
    @Test
    void testDeposit() {

        AccountResponse account = new AccountResponse();
        account.setBalance(BigDecimal.valueOf(1000));

        when(accountClient.getAccount(1L))
                .thenReturn(account);

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest request = new TransactionRequest();
        request.accountId = 1L;
        request.amount = BigDecimal.valueOf(500);

        Transaction tx = service.deposit(request);

        assertNotNull(tx);
        assertEquals(BigDecimal.valueOf(1500), tx.getBalanceAfter());
        assertEquals(TransactionType.DEPOSIT, tx.getType());
    }

    // ✅ Withdraw Test - Success
    @Test
    void testWithdraw_Success() {

        AccountResponse account = new AccountResponse();
        account.setBalance(BigDecimal.valueOf(1000));

        when(accountClient.getAccount(1L))
                .thenReturn(account);

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionRequest request = new TransactionRequest();
        request.accountId = 1L;
        request.amount = BigDecimal.valueOf(200);

        Transaction tx = service.withdraw(request);

        assertNotNull(tx);
        assertEquals(BigDecimal.valueOf(800), tx.getBalanceAfter());
        assertEquals(TransactionType.WITHDRAW, tx.getType());
    }

    // ❌ Withdraw Test - Insufficient Balance
    @Test
    void testWithdraw_InsufficientBalance() {

        AccountResponse account = new AccountResponse();
        account.setBalance(BigDecimal.valueOf(100));

        when(accountClient.getAccount(1L))
                .thenReturn(account);

        TransactionRequest request = new TransactionRequest();
        request.accountId = 1L;
        request.amount = BigDecimal.valueOf(200);

        assertThrows(RuntimeException.class,
                () -> service.withdraw(request));
    }

    // ✅ History Test
    @Test
    void testHistory() {

        Transaction tx1 = new Transaction();
        tx1.setAccountId(1L);

        when(repository.findByAccountId(1L))
                .thenReturn(List.of(tx1));

        List<Transaction> result = service.history(1L);

        assertEquals(1, result.size());
    }
}