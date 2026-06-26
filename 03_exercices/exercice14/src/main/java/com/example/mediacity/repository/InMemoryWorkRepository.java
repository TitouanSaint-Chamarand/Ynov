package com.example.mediacity.repository;

import com.example.mediacity.model.Work;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryWorkRepository implements WorkRepository {

    private final Map<String, Work> works = new HashMap<>();

    @Override
    public Optional<Work> findById(String id) {
        return Optional.ofNullable(works.get(id));
    }

    @Override
    public Work save(Work work) {
        works.put(work.id(), work);
        return work;
    }
}
