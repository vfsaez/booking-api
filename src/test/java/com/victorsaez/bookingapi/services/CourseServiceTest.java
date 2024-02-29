package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.CourseDTO;
import com.victorsaez.bookingapi.entities.Course;
import com.victorsaez.bookingapi.repositories.CourseRepository;
import com.victorsaez.bookingapi.repositories.ResultRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ResultRepository resultRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnAllCourses() {
        Course course = new Course();
        course.setId(1L);
        // set other fields as necessary
        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(true);
        List<Course> courseList = Collections.singletonList(course);
        Page<Course> coursePage = new PageImpl<>(courseList);

        when(courseRepository.findAll(any(Pageable.class))).thenReturn(coursePage);

        Page<CourseDTO> courses = courseService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, courses.getTotalElements());
        assertEquals(1L, courses.getContent().get(0).getId());
    }

    @Test
    public void shouldReturnAllCoursesByProfessorId() {
        Course course = new Course();
        course.setId(1L);
        // set other fields as necessary
        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");
        Mockito.when(mockUserDetails.getId()).thenReturn(1L);
        Mockito.when(mockUserDetails.isAdmin()).thenReturn(false);
        List<Course> courseList = Collections.singletonList(course);
        Page<Course> coursePage = new PageImpl<>(courseList);

        when(courseRepository.findAllByProfessorId(anyLong(),any(Pageable.class))).thenReturn(coursePage);

        Page<CourseDTO> courses = courseService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, courses.getTotalElements());
        assertEquals(1L, courses.getContent().get(0).getId());
    }
}