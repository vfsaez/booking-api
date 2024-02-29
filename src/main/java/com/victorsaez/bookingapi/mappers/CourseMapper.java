package com.victorsaez.bookingapi.mappers;

import com.victorsaez.bookingapi.dto.CourseDTO;
import com.victorsaez.bookingapi.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    CourseDTO courseToCourseDTO(Course course);
    Course courseDTOtoCourse(CourseDTO courseDto);
}