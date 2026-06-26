package com.example.mediacity.repository;

import com.example.mediacity.model.Work;

import java.util.Optional;

public interface WorkRepository {

    Optional<Work> findById(String id);

    Work save(Work work);
}
