package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.ResultDTO;
import com.victorsaez.bookingapi.entities.Course;
import com.victorsaez.bookingapi.entities.Result;
import com.victorsaez.bookingapi.entities.Student;
import com.victorsaez.bookingapi.exceptions.*;
import com.victorsaez.bookingapi.mappers.ResultMapper;
import com.victorsaez.bookingapi.repositories.CourseRepository;
import com.victorsaez.bookingapi.repositories.ResultRepository;
import com.victorsaez.bookingapi.repositories.StudentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ResultService {
    private final ResultRepository repository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    private final ResultMapper resultMapper = ResultMapper.INSTANCE;
    private static final Logger logger = LogManager.getLogger(ResultService.class);
    public ResultService(ResultRepository repository, StudentRepository studentRepository, CourseRepository courseRepository, CourseService courseService) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    public Page<ResultDTO> findAll(Pageable pageable, UserDetails currentUserDetails) {
        CustomUserDetails customUserDetails = (CustomUserDetails) currentUserDetails;
        Page<Result> results = customUserDetails.isAdmin() ?
                repository.findAll(pageable):
                repository.findAllByProfessorId(customUserDetails.getId(), pageable);
        return results.map(resultMapper::resultToResultDTO);
    }

    public ResultDTO findById(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        return resultMapper.resultToResultDTO(repository.findById(id).map(result -> {
            if (customCurrentUserDetails.isAdmin() || result.getProfessor().getId().equals(customCurrentUserDetails.getId())) {
                return result;
            } else {
                throw new AccessDeniedException(id, customCurrentUserDetails.getId());
            }}).orElseThrow(() -> new ResultNotFoundException(id)));
    }

    public ResultDTO insert(ResultDTO resultDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Student student = studentRepository.findById(resultDto.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(resultDto.getStudentId()));
        Course course = courseRepository.findById(resultDto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(resultDto.getCourseId()));

        Result result = resultMapper.resultDTOtoResult(resultDto);
        result.setStudent(student);
        result.setCourse(course);
        result.setProfessor(customCurrentUserDetails.getUser());
        Result createdResult = repository.save(result);

        logger.info("user {} Result id {} created for course id {} and student id {}", customCurrentUserDetails.getId(), createdResult.getId(), createdResult.getCourse().getId(), createdResult.getStudent().getId());
        return resultMapper.resultToResultDTO(createdResult);
    }

    public ResultDTO patch(Long id, ResultDTO resultDto, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Result existingResult = repository.findById(id)
                .orElseThrow(() -> new ResultNotFoundException(id));

        if (resultDto.getGrade() != null) {
            existingResult.setGrade(resultDto.getGrade());
        }

        if (!customCurrentUserDetails.isAdmin()
                && !existingResult.getProfessor().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(id, customCurrentUserDetails.getId());
        }

        Result updatedResult = repository.save(existingResult);
        logger.info("user {} Result id {} patched for course id {} and student id {}", customCurrentUserDetails.getId(), updatedResult.getId(), updatedResult.getCourse().getId(), updatedResult.getStudent().getId());
        return resultMapper.resultToResultDTO(updatedResult);
    }

    public ResultDTO update(ResultDTO resultDto, UserDetails currentUserDetails) throws AccessDeniedException, CourseNotAvailableException, CourseNotFoundException, ResultNotFoundException {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        Result existingResult = repository.findById(resultDto.getId())
                .orElseThrow(() -> new ResultNotFoundException(resultDto.getId()));

        Course course = courseRepository.findById(resultDto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(resultDto.getCourseId()));

        existingResult.setGrade(resultDto.getGrade());

        if (!customCurrentUserDetails.isAdmin()
                && !existingResult.getProfessor().getId().equals(customCurrentUserDetails.getId())) {
            throw new AccessDeniedException(resultDto.getId(), customCurrentUserDetails.getId());
        }

        Result updatedResult = repository.save(existingResult);
        logger.info("user {} Result id {} updated for course id {} and student id {}", customCurrentUserDetails.getId(), updatedResult.getId(), updatedResult.getCourse().getId(), updatedResult.getStudent().getId());
        return resultMapper.resultToResultDTO(updatedResult);
    }

    public void delete(Long id, UserDetails currentUserDetails) {
        CustomUserDetails customCurrentUserDetails = (CustomUserDetails) currentUserDetails;
        ResultDTO resultDto = this.findById(id, currentUserDetails);
        logger.info("user {} Result id {} deleted for course id {} and student id {}", customCurrentUserDetails.getId(), resultDto.getId(), resultDto.getCourseId(), resultDto.getStudentId());
        repository.deleteById(id);
    }
}
