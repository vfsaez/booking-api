package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Page<Result> findAllByProfessorId(Long professorId, Pageable pageable);

}
