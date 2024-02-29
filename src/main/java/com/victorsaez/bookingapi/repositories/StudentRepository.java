package com.victorsaez.bookingapi.repositories;

import com.victorsaez.bookingapi.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findAllByProfessorId(Long professorId, Pageable pageable);
}
