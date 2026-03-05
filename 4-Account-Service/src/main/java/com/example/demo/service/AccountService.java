package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.AccountStatus;
import com.example.demo.repo.AccountRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountService {

	private static final Logger log = LoggerFactory.getLogger(AccountService.class);

	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Transactional
	public Account createAccount(Account account) {

		log.info("Creating new account for customer");

		account.setBalance(BigDecimal.ZERO);
		account.setStatus(AccountStatus.ACTIVE);

		Account savedAccount = accountRepository.save(account);

		log.info("Account created successfully with ID {}", savedAccount.getAccountId());

		return savedAccount;
	}

	public Account getAccount(Long id) {

		log.info("Fetching account with ID {}", id);

		return accountRepository.findById(id).orElseThrow(() -> {
			log.error("Account not found with ID {}", id);
			return new RuntimeException("Account not found");
		});
	}

	public List<Account> getAllAccounts() {

		log.info("Fetching all accounts");

		return accountRepository.findAll();
	}

	@Transactional
	public Account updateBalance(Long id, BigDecimal amount) {

		log.info("Updating balance for account {}", id);

		Account account = getAccount(id);

		if (account.getStatus() == AccountStatus.FROZEN) {

			log.warn("Attempt to update balance for frozen account {}", id);

			throw new RuntimeException("Account is frozen");
		}

		account.setBalance(amount);

		Account updatedAccount = accountRepository.save(account);

		log.info("Balance updated successfully for account {}", id);

		return updatedAccount;
	}

	@Transactional
	public Account freezeAccount(Long id) {

		log.warn("Freezing account {}", id);

		Account account = getAccount(id);
		account.setStatus(AccountStatus.FROZEN);

		Account frozenAccount = accountRepository.save(account);

		log.info("Account frozen successfully {}", id);

		return frozenAccount;
	}

	@Transactional
	public Account unfreezeAccount(Long id) {

		log.info("Unfreezing account {}", id);

		Account account = getAccount(id);
		account.setStatus(AccountStatus.ACTIVE);

		Account activeAccount = accountRepository.save(account);

		log.info("Account activated successfully {}", id);

		return activeAccount;
	}
}