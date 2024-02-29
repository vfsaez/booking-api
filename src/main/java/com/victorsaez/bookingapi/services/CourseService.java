package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.CourseDTO;
import com.victorsaez.bookingapi.entities.Course;
import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
import com.victorsaez.bookingapi.exceptions.CourseNotFoundException;
import com.victorsaez.bookingapi.mappers.CourseMapper;
import com.victorsaez.bookingapi.repositories.CourseRepository;
import com.victorsaez.bookingapi.repositories.ResultRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    public CourseRepository repository;

    private final ResultRepository resultRepository;

    private final CourseMapper courseMapper = CourseMapper.INSTANCE;

    private static final Logger logger = LogManager.getLogger(CourseService.class);


    public CourseService(CourseRepository repository, ResultRepository resultRepository) {
        this.repository = repository;
        this.resultRepository = resultRepository;
    }

    public Page<CourseDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Page<Course> courses = customCurrentUserDetails.isAdmin() ?
                repository.findAll(pageable) :
                repository.findAllByProfessorId(customCurrentUserDetails.getId(), pageable);
        return courses.map(courseMapper::courseToCourseDTO);
    }

    public CourseDTO findById(Long id, UserDetails currentUserDetails) throws CourseNotFoundException {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        return courseMapper.courseToCourseDTO(repository.findById(id).map(course -> {
            if (customCurrentUserDetails.isAdmin() || course.getProfessor().getId().equals(((CustomUserDetails) currentUserDetails).getId())) {
                return course;
            } else {
                throw new AccessDeniedException(id, ((CustomUserDetails) currentUserDetails).getId());
            }}).orElseThrow(() -> new CourseNotFoundException(id)));
    }

    public CourseDTO insert(CourseDTO courseDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Course courseToSave = courseMapper.courseDTOtoCourse(courseDto);
        courseToSave.setProfessor(customCurrentUserDetails.getUser());
        var courseSaved = repository.save(courseToSave);
        logger.info("user {} Course id {} created", customCurrentUserDetails.getId(), courseSaved.getId());
        return courseMapper.courseToCourseDTO(courseSaved);
    }


    public CourseDTO update(CourseDTO courseDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Course existingCourse = repository.findById(courseDto.getId())
                .orElseThrow(() -> new CourseNotFoundException(courseDto.getId()));

        existingCourse.setName(courseDto.getName());

        if (!customCurrentUserDetails.isAdmin()
                && !existingCourse.getProfessor().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(courseDto.getId(), customCurrentUserDetails.getId());
        }

        Course updatedCourse = repository.save(existingCourse);
        logger.info("user {} Course id {} updated", customCurrentUserDetails.getId(), updatedCourse.getId());
        return courseMapper.courseToCourseDTO(updatedCourse);
    }

    public CourseDTO patch(Long id, CourseDTO courseDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Course existingCourse = repository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        if (courseDto.getName() != null) {
            existingCourse.setName(courseDto.getName());
        }

        if (!customCurrentUserDetails.isAdmin()
                && !existingCourse.getProfessor().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(id, customCurrentUserDetails.getId());
        }

        Course updatedCourse = repository.save(existingCourse);
        logger.info("user {} Course id {} patched", customCurrentUserDetails.getId(), updatedCourse.getId());
        return courseMapper.courseToCourseDTO(updatedCourse);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        CourseDTO courseDto = this.findById(id, currentUserDetails);
        logger.info("user {} Course id {} deleted", customCurrentUserDetails.getId(), courseDto.getId());
        repository.deleteById(id);
    }
}
