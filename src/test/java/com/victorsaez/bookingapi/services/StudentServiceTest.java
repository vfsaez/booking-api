package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.StudentDTO;
import com.victorsaez.bookingapi.entities.Student;
import com.victorsaez.bookingapi.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnAllStudents() {
        Student student = new Student();
        student.setId(1L);
        List<Student> studentList = Collections.singletonList(student);
        Page<Student> studentPage = new PageImpl<>(studentList);

        when(studentRepository.findAll(any(Pageable.class))).thenReturn(studentPage);

        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        when(mockUserDetails.isAdmin()).thenReturn(true);
        Page<StudentDTO> students = studentService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, students.getTotalElements());
        assertEquals(1L, students.getContent().get(0).getId());
    }
}