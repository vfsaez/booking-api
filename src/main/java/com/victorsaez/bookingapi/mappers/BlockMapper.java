package com.victorsaez.bookingapi.mappers;

import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.entities.Block;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BlockMapper {
    BlockMapper INSTANCE = Mappers.getMapper(BlockMapper.class);

    BlockDTO blockToBlockDTO(Block block);
    Block blockDTOtoBlock(BlockDTO blockDTO);
}