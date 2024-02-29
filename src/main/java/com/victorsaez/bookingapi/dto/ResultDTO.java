package com.victorsaez.bookingapi.dto;

import com.victorsaez.bookingapi.enums.ResultGrade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDTO {

    private Long id;


    @NotNull(message = "status can't be null or empty")
    private ResultGrade grade;

    private Long studentId;
    private StudentDTO student;


    private Long courseId;
    private CourseDTO course;

    @NotNull(message = "courseId can't be null or empty")
    public Long getCourseId() {
        return course != null ? course.getId() : courseId;
    }

    @NotNull(message = "studentId can't be null or empty")
    public Long getStudentId() {
        return student != null ? student.getId() : studentId;
    }
}

