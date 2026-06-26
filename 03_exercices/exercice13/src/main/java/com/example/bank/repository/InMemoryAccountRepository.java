package com.example.bank.repository;

import com.example.bank.model.Account;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Account save(String number, String holder, BigDecimal balance) {
        Account account = new Account(number, holder, balance);
        accounts.put(number, account);
        return account;
    }

    @Override
    public Optional<Account> findByNumber(String number) {
        return Optional.ofNullable(accounts.get(number));
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accounts.values())
                .stream()
                .sorted(Comparator.comparing(Account::number))
                .toList();
    }

    @Override
    public Account updateBalance(String number, BigDecimal balance) {
        Account existing = accounts.get(number);
        Account updated = new Account(existing.number(), existing.holder(), balance);
        accounts.put(number, updated);
        return updated;
    }

    @Override
    public void deleteAll() {
        accounts.clear();
    }
}
