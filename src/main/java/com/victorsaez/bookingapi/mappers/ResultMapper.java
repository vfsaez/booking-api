package com.victorsaez.bookingapi.mappers;

import com.victorsaez.bookingapi.dto.ResultDTO;
import com.victorsaez.bookingapi.entities.Result;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResultMapper {
    ResultMapper INSTANCE = Mappers.getMapper(ResultMapper.class);

    ResultDTO resultToResultDTO(Result result);
    Result resultDTOtoResult(ResultDTO resultDto);
}