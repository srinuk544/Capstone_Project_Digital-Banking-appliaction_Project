package com.example.demo.service;

import com.example.demo.client.AccountClient;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.*;
import com.example.demo.repo.TransactionRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository repository;
    private final AccountClient accountClient;

    private static final String ACCOUNT_SERVICE = "accountService";

    public TransactionService(TransactionRepository repository,
                              AccountClient accountClient) {
        this.repository = repository;
        this.accountClient = accountClient;
    }

    @Transactional
    @CircuitBreaker(name = ACCOUNT_SERVICE, fallbackMethod = "depositFallback")
    public Transaction deposit(TransactionRequest request) {

        log.info("Deposit request received for account {}", request.accountId);

        AccountResponse account = accountClient.getAccount(request.accountId);

        BigDecimal balance = account.getBalance();
        BigDecimal newBalance = balance.add(request.amount);

        log.info("Updating account balance for account {}", request.accountId);

        accountClient.updateBalance(request.accountId, newBalance);

        Transaction tx = new Transaction();
        tx.setAccountId(request.accountId);
        tx.setAmount(request.amount);
        tx.setType(TransactionType.DEPOSIT);
        tx.setBalanceAfter(newBalance);

        Transaction savedTx = repository.save(tx);

        log.info("Deposit successful for account {}", request.accountId);

        return savedTx;
    }

    @Transactional
    @CircuitBreaker(name = ACCOUNT_SERVICE, fallbackMethod = "withdrawFallback")
    public Transaction withdraw(TransactionRequest request) {

        log.info("Withdraw request received for account {}", request.accountId);

        AccountResponse account = accountClient.getAccount(request.accountId);

        BigDecimal balance = account.getBalance();

        if (balance.compareTo(request.amount) < 0) {

            log.warn("Insufficient balance for account {}", request.accountId);

            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = balance.subtract(request.amount);

        log.info("Updating account balance after withdrawal for account {}", request.accountId);

        accountClient.updateBalance(request.accountId, newBalance);

        Transaction tx = new Transaction();
        tx.setAccountId(request.accountId);
        tx.setAmount(request.amount);
        tx.setType(TransactionType.WITHDRAW);
        tx.setBalanceAfter(newBalance);

        Transaction savedTx = repository.save(tx);

        log.info("Withdrawal successful for account {}", request.accountId);

        return savedTx;
    }

    public List<Transaction> history(Long accountId) {

        log.info("Fetching transaction history for account {}", accountId);

        return repository.findByAccountId(accountId);
    }

    public List<Transaction> getAllTransactions() {

        log.info("Fetching all transactions");

        return repository.findAll();
    }

    // - FALLBACK ---

    public Transaction depositFallback(TransactionRequest request, Exception ex) {

        log.error("Deposit failed because Account Service is down. AccountId: {}", request.accountId);

        throw new RuntimeException("Account Service is down. Please try again later.");
    }

    public Transaction withdrawFallback(TransactionRequest request, Exception ex) {

        log.error("Withdraw failed because Account Service is down. AccountId: {}", request.accountId);

        throw new RuntimeException("Account Service is down. Withdrawal failed.");
    }
}