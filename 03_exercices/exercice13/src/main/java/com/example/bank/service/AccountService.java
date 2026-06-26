package com.example.bank.service;

import com.example.bank.exception.AccountAlreadyExistsException;
import com.example.bank.exception.AccountNotFoundException;
import com.example.bank.exception.InsufficientFundsException;
import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account create(String number, String holder) {
        if (accountRepository.findByNumber(number).isPresent()) {
            throw new AccountAlreadyExistsException(number);
        }

        return accountRepository.save(number, holder.trim(), BigDecimal.ZERO);
    }

    public Account getByNumber(String number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account deposit(String number, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = getByNumber(number);
        return accountRepository.updateBalance(number, account.balance().add(amount));
    }

    public Account withdraw(String number, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = getByNumber(number);

        if (account.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(number);
        }

        return accountRepository.updateBalance(number, account.balance().subtract(amount));
    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount) {
        validatePositiveAmount(amount);

        Account fromAccount = getByNumber(fromNumber);
        Account toAccount = getByNumber(toNumber);

        if (fromAccount.balance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromNumber);
        }

        accountRepository.updateBalance(fromNumber, fromAccount.balance().subtract(amount));
        accountRepository.updateBalance(toNumber, toAccount.balance().add(amount));
    }

    public void deleteAll() {
        accountRepository.deleteAll();
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit etre strictement positif");
        }
    }
}
