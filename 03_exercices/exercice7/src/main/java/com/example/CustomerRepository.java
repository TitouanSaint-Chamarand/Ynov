package com.example;

public interface CustomerRepository {
    ClientProfile getProfileByEmail(String email);
}
