package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.CourseDTO;
import com.victorsaez.bookingapi.dto.StudentDTO;
import com.victorsaez.bookingapi.entities.Course;
import com.victorsaez.bookingapi.entities.Student;
import com.victorsaez.bookingapi.exceptions.AgeRequirementsException;
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
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        //mock student id 1L
        Student student = new Student();
        student.setId(1L);
        student.setName("Charles");
        student.setFamilyName("Demo");
        student.setEmail("efake@gmfake.com");
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, 1, 1);
        student.setDateOfBirth(calendar.getTime());
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(student));
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

    @Test
    public void shouldReturnAllStudentsByProfessorId() {
        Student student = new Student();
        student.setId(1L);
        // set other fields as necessary
        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(false);
        List<Student> studentList = Collections.singletonList(student);
        Page<Student> studentPage = new PageImpl<>(studentList);

        when(studentRepository.findAllByProfessorId(anyLong(),any(Pageable.class))).thenReturn(studentPage);

        Page<StudentDTO> students = studentService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, students.getTotalElements());
        assertEquals(1L, students.getContent().get(0).getId());
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsInvalidOnInsert() throws AgeRequirementsException {
        StudentDTO studentDto = new StudentDTO();
        studentDto.setEmail("invalidEmail");
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, 1, 1);
        studentDto.setDateOfBirth(calendar.getTime());

        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(false);

        assertThrows(ConstraintViolationException.class, () -> studentService.insert(studentDto, mockUserDetails));
    }

    @Test
    public void shouldThrowExceptionWhenAgerRequirementIsInvalidOnPatch() throws AgeRequirementsException {
        StudentDTO studentDto = new StudentDTO();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 1, 1);
        studentDto.setDateOfBirth(calendar.getTime());

        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(false);

        assertThrows(AgeRequirementsException.class, () -> studentService.patch(1L, studentDto, mockUserDetails));
    }
}