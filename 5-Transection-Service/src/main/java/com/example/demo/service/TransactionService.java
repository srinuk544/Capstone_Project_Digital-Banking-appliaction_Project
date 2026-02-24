package com.example.demo.service;

import com.example.demo.client.AccountClient;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.*;
import com.example.demo.repo.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountClient accountClient;

    @Transactional
    public Transaction deposit(TransactionRequest request) {

        AccountResponse account =
                accountClient.getAccount(request.accountId);

        BigDecimal balance = account.getBalance();

        BigDecimal newBalance = balance.add(request.amount);

        accountClient.updateBalance(request.accountId, newBalance);

        Transaction tx = new Transaction();
        tx.setAccountId(request.accountId);
        tx.setAmount(request.amount);
        tx.setType(TransactionType.DEPOSIT);
        tx.setBalanceAfter(newBalance);

        return repository.save(tx);
    }

    @Transactional
    public Transaction withdraw(TransactionRequest request) {

        AccountResponse account =
                accountClient.getAccount(request.accountId);

        BigDecimal balance = account.getBalance();

        if (balance.compareTo(request.amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = balance.subtract(request.amount);

        accountClient.updateBalance(request.accountId, newBalance);

        Transaction tx = new Transaction();
        tx.setAccountId(request.accountId);
        tx.setAmount(request.amount);
        tx.setType(TransactionType.WITHDRAW);
        tx.setBalanceAfter(newBalance);

        return repository.save(tx);
    }

    public List<Transaction> history(Long accountId) {
        return repository.findByAccountId(accountId);
    }
}
