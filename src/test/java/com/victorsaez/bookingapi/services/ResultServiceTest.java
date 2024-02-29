package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.CourseDTO;
import com.victorsaez.bookingapi.dto.ResultDTO;
import com.victorsaez.bookingapi.entities.Course;
import com.victorsaez.bookingapi.entities.Result;
import com.victorsaez.bookingapi.entities.Student;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.exceptions.CourseNotFoundException;
import com.victorsaez.bookingapi.repositories.CourseRepository;
import com.victorsaez.bookingapi.repositories.ResultRepository;
import com.victorsaez.bookingapi.repositories.StudentRepository;
import com.victorsaez.bookingapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ResultServiceTest {

    @InjectMocks
    private ResultService resultService;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Student student = new Student();
        student.setId(1L);
        Mockito.when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        User user = new User();
        user.setId(1L);

        // Mock a Course
        Course course = new Course();
        course.setId(1L);
        Mockito.when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        Result bookedResult = new Result();
        bookedResult.setId(1L);
        bookedResult.setCourse(course);
        bookedResult.setStudent(student);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        bookedResult.setProfessor(user);


        Result cancelledResult = new Result();
        cancelledResult.setId(1L);
        cancelledResult.setCourse(course);
        bookedResult.setStudent(student);
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        cancelledResult.setProfessor(user);


        Mockito.when(resultRepository.findById(1L)).thenReturn(Optional.of(bookedResult));
        Mockito.when(resultRepository.findById(2L)).thenReturn(Optional.of(cancelledResult));

        Mockito.when(resultRepository.save(any())).thenReturn(bookedResult);
    }

    @Test
    public void shouldReturnAllResults() {
        Result result = new Result();
        result.setId(1L);
        List<Result> resultList = Collections.singletonList(result);
        Page<Result> resultPage = new PageImpl<>(resultList);

        when(resultRepository.findAll(any(Pageable.class))).thenReturn(resultPage);

        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        Page<ResultDTO> results = resultService.findAll(Pageable.unpaged(), customUserDetails);

        assertEquals(1, results.getTotalElements());
        assertEquals(1L, results.getContent().get(0).getId());
    }

    @Test
    public void shouldThrowExceptionWhenCreatingBlockWithNonexistentCourse() {
        ResultDTO resultDto = new ResultDTO();
        resultDto.setCourseId(1L);
        resultDto.setStudentId(1L);

        Mockito.when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        assertThrows(CourseNotFoundException.class, () -> {
            resultService.insert(resultDto, customUserDetails);
        });
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingResultWithNonexistentCourse() {
        ResultDTO resultDto = new ResultDTO();
        resultDto.setId(1L);
        resultDto.setCourseId(1L);

        Mockito.when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        assertThrows(CourseNotFoundException.class, () -> resultService.update(resultDto, customUserDetails));
    }

    @Test
    public void shouldReturnAllResultsByProfessorId() {
        Result result = new Result();
        result.setId(1L);
        // set other fields as necessary
        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(false);
        List<Result> resultList = Collections.singletonList(result);
        Page<Result> resultPage = new PageImpl<>(resultList);

        when(resultRepository.findAllByProfessorId(anyLong(),any(Pageable.class))).thenReturn(resultPage);

        Page<ResultDTO> results = resultService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, results.getTotalElements());
        assertEquals(1L, results.getContent().get(0).getId());
    }

}