package com.example.bank.service;

import com.example.bank.exception.AccountAlreadyExistsException;
import com.example.bank.exception.AccountNotFoundException;
import com.example.bank.exception.InsufficientFundsException;
import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService service;

    private static final String ACCOUNT_NUMBER = "FR001";
    private static final String HOLDER = "Alice Martin";
    private static final Account EXISTING_ACCOUNT = new Account(ACCOUNT_NUMBER, HOLDER, BigDecimal.ZERO);

    // --- Creation de compte ---

    @Test
    void shouldCreateAccount_whenNumberIsUnique() {
        // Arrange
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());
        when(accountRepository.save(ACCOUNT_NUMBER, HOLDER, BigDecimal.ZERO))
                .thenReturn(EXISTING_ACCOUNT);

        // Act
        Account result = service.create(ACCOUNT_NUMBER, HOLDER);

        // Assert
        assertEquals(ACCOUNT_NUMBER, result.number());
        assertEquals(HOLDER, result.holder());
        assertEquals(BigDecimal.ZERO, result.balance());
        verify(accountRepository).save(ACCOUNT_NUMBER, HOLDER, BigDecimal.ZERO);
    }

    @Test
    void shouldThrowConflict_whenAccountNumberAlreadyExists() {
        // Arrange
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(EXISTING_ACCOUNT));

        // Act + Assert
        assertThrows(AccountAlreadyExistsException.class, () -> service.create(ACCOUNT_NUMBER, HOLDER));
        verify(accountRepository, never()).save(eq(ACCOUNT_NUMBER), eq(HOLDER), eq(BigDecimal.ZERO));
    }

    // --- Consultation ---

    @Test
    void shouldReturnAccount_whenAccountExists() {
        // Arrange
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(EXISTING_ACCOUNT));

        // Act
        Account result = service.getByNumber(ACCOUNT_NUMBER);

        // Assert
        assertEquals(EXISTING_ACCOUNT, result);
    }

    @Test
    void shouldThrowNotFound_whenAccountDoesNotExist() {
        // Arrange
        when(accountRepository.findByNumber("UNKNOWN")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(AccountNotFoundException.class, () -> service.getByNumber("UNKNOWN"));
    }

    @Test
    void shouldReturnAllAccounts() {
        // Arrange
        Account account2 = new Account("FR002", "Bob Dupont", BigDecimal.TEN);
        when(accountRepository.findAll()).thenReturn(List.of(EXISTING_ACCOUNT, account2));

        // Act
        List<Account> result = service.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(EXISTING_ACCOUNT, result.get(0));
        assertEquals(account2, result.get(1));
    }

    // --- Depot ---

    @Test
    void shouldDeposit_whenAmountIsPositive() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        Account updated = new Account(ACCOUNT_NUMBER, HOLDER, amount);

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(EXISTING_ACCOUNT));
        when(accountRepository.updateBalance(ACCOUNT_NUMBER, amount)).thenReturn(updated);

        // Act
        Account result = service.deposit(ACCOUNT_NUMBER, amount);

        // Assert
        assertEquals(amount, result.balance());
        verify(accountRepository).updateBalance(ACCOUNT_NUMBER, amount);
    }

    @Test
    void shouldThrowBadRequest_whenDepositAmountIsNull() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.deposit(ACCOUNT_NUMBER, null));
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowBadRequest_whenDepositAmountIsNegative() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.deposit(ACCOUNT_NUMBER, new BigDecimal("-10")));
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    // --- Retrait ---

    @Test
    void shouldWithdraw_whenFundsAreSufficient() {
        // Arrange
        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal amount = new BigDecimal("50.00");
        BigDecimal remaining = new BigDecimal("150.00");
        Account fundedAccount = new Account(ACCOUNT_NUMBER, HOLDER, initialBalance);
        Account updated = new Account(ACCOUNT_NUMBER, HOLDER, remaining);

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(fundedAccount));
        when(accountRepository.updateBalance(ACCOUNT_NUMBER, remaining)).thenReturn(updated);

        // Act
        Account result = service.withdraw(ACCOUNT_NUMBER, amount);

        // Assert
        assertEquals(remaining, result.balance());
        verify(accountRepository).updateBalance(ACCOUNT_NUMBER, remaining);
    }

    @Test
    void shouldThrowBadRequest_whenWithdrawAmountIsNull() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(ACCOUNT_NUMBER, null));
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowBadRequest_whenWithdrawAmountIsNegative() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(ACCOUNT_NUMBER, new BigDecimal("-5")));
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowConflict_whenWithdrawWithInsufficientFunds() {
        // Arrange
        Account poorAccount = new Account(ACCOUNT_NUMBER, HOLDER, new BigDecimal("10.00"));
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(poorAccount));

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> service.withdraw(ACCOUNT_NUMBER, new BigDecimal("50.00"))
        );
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    // --- Virement ---

    @Test
    void shouldTransfer_whenFundsAreSufficient() {
        // Arrange
        String toNumber = "FR002";
        BigDecimal amount = new BigDecimal("75.00");
        Account fromAccount = new Account(ACCOUNT_NUMBER, HOLDER, new BigDecimal("200.00"));
        Account toAccount = new Account(toNumber, "Bob Dupont", new BigDecimal("50.00"));

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByNumber(toNumber)).thenReturn(Optional.of(toAccount));

        // Act
        service.transfer(ACCOUNT_NUMBER, toNumber, amount);

        // Assert
        verify(accountRepository).updateBalance(ACCOUNT_NUMBER, new BigDecimal("125.00"));
        verify(accountRepository).updateBalance(toNumber, new BigDecimal("125.00"));
    }

    @Test
    void shouldThrowBadRequest_whenTransferAmountIsNull() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.transfer(ACCOUNT_NUMBER, "FR002", null));
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowBadRequest_whenTransferAmountIsNegative() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> service.transfer(ACCOUNT_NUMBER, "FR002", new BigDecimal("-1")));
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowConflict_whenTransferWithInsufficientFunds() {
        // Arrange
        String toNumber = "FR002";
        Account fromAccount = new Account(ACCOUNT_NUMBER, HOLDER, new BigDecimal("30.00"));
        Account toAccount = new Account(toNumber, "Bob Dupont", BigDecimal.ZERO);

        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByNumber(toNumber)).thenReturn(Optional.of(toAccount));

        // Act + Assert
        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(ACCOUNT_NUMBER, toNumber, new BigDecimal("100.00"))
        );
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
        verify(accountRepository, never()).updateBalance(eq(toNumber), eq(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowNotFound_whenTransferToUnknownAccount() {
        // Arrange
        Account fromAccount = new Account(ACCOUNT_NUMBER, HOLDER, new BigDecimal("100.00"));
        when(accountRepository.findByNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByNumber("UNKNOWN")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                AccountNotFoundException.class,
                () -> service.transfer(ACCOUNT_NUMBER, "UNKNOWN", new BigDecimal("10.00"))
        );
        verify(accountRepository, never()).updateBalance(eq(ACCOUNT_NUMBER), eq(BigDecimal.ZERO));
    }

    @Test
    void shouldThrowNotFound_whenTransferFromUnknownAccount() {
        // Arrange
        when(accountRepository.findByNumber("UNKNOWN")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                AccountNotFoundException.class,
                () -> service.transfer("UNKNOWN", "FR002", new BigDecimal("10.00"))
        );
        verify(accountRepository, never()).updateBalance(eq("FR002"), eq(BigDecimal.ZERO));
    }
}
