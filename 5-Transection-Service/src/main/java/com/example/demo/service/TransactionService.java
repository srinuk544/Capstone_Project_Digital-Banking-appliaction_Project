package com.example.demo.service;

import com.example.demo.client.AccountClient;
import com.example.demo.dto.AccountResponse;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.*;
import com.example.demo.repo.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository repository;

	@Autowired
	private AccountClient accountClient;

	private static final String ACCOUNT_SERVICE = "accountService";

	@Transactional
	@CircuitBreaker(name = ACCOUNT_SERVICE, fallbackMethod = "depositFallback")
	public Transaction deposit(TransactionRequest request) {

		AccountResponse account = accountClient.getAccount(request.accountId);

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
	@CircuitBreaker(name = ACCOUNT_SERVICE, fallbackMethod = "withdrawFallback")
	public Transaction withdraw(TransactionRequest request) {

		AccountResponse account = accountClient.getAccount(request.accountId);

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

	public List<Transaction> getAllTransactions() {
		return repository.findAll();
	}

	// ------------------ FALLBACK METHODS ------------------

	public Transaction depositFallback(TransactionRequest request, Exception ex) {

		throw new RuntimeException("Account Service is down. Please try again later.");
	}

	public Transaction withdrawFallback(TransactionRequest request, Exception ex) {

		throw new RuntimeException("Account Service is down. Withdrawal failed.");
	}
}