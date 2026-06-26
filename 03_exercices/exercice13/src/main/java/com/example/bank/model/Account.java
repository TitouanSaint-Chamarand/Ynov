package com.example.bank.model;

import java.math.BigDecimal;

public record Account(String number, String holder, BigDecimal balance) {
}
