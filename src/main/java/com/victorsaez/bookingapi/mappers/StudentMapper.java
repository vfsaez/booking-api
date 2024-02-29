package com.victorsaez.bookingapi.mappers;

import com.victorsaez.bookingapi.dto.StudentDTO;
import com.victorsaez.bookingapi.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    StudentDTO studentToStudentDTO(Student student);
    Student studentDTOtoStudent(StudentDTO studentDto);
}