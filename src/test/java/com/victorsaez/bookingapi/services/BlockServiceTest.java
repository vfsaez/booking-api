package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomSpringUser;
import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.repositories.BlockRepository;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BlockServiceTest {

    @InjectMocks
    private BlockService blockService;

    @Mock
    private BlockRepository blockRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnAllBlocks() {
        Block block = new Block();
        block.setId(1L);
        List<Block> blockList = Collections.singletonList(block);
        Page<Block> blockPage = new PageImpl<>(blockList);

        when(blockRepository.findAll(any(Pageable.class))).thenReturn(blockPage);

        CustomSpringUser mockUserDetails = Mockito.mock(CustomSpringUser.class);
        when(mockUserDetails.isAdmin()).thenReturn(true);
        Page<BlockDTO> blocks = blockService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, blocks.getTotalElements());
        assertEquals(1L, blocks.getContent().get(0).getId());
    }
}