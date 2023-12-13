package com.victorsaez.bookingapi.services;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.entities.Block;
import com.victorsaez.bookingapi.entities.Client;
import com.victorsaez.bookingapi.entities.Property;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.exceptions.PropertyNotAvailableException;
import com.victorsaez.bookingapi.exceptions.PropertyNotFoundException;
import com.victorsaez.bookingapi.repositories.BlockRepository;
import com.victorsaez.bookingapi.repositories.ClientRepository;
import com.victorsaez.bookingapi.repositories.PropertyRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BlockServiceTest {

    @InjectMocks
    private BlockService blockService;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyService propertyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        Client client = new Client();
        client.setId(1L);
        Mockito.when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));

        // Mock a Property
        Property property = new Property();
        property.setId(1L);
        Mockito.when(propertyRepository.findById(anyLong())).thenReturn(Optional.of(property));

        Block blockedBlock = new Block();
        blockedBlock.setId(1L);
        blockedBlock.setStatus(BlockStatus.BLOCKED);
        blockedBlock.setProperty(property);
        Calendar cal = Calendar.getInstance();
        blockedBlock.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        blockedBlock.setEndDate(cal.getTime());


        Block cancelledBlock = new Block();
        cancelledBlock.setId(1L);
        cancelledBlock.setStatus(BlockStatus.CANCELLED);
        cancelledBlock.setProperty(property);
        cal = Calendar.getInstance();
        cancelledBlock.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        cancelledBlock.setEndDate(cal.getTime());


        Mockito.when(blockRepository.findById(1L)).thenReturn(Optional.of(blockedBlock));
        Mockito.when(blockRepository.findById(2L)).thenReturn(Optional.of(cancelledBlock));

        Mockito.when(blockRepository.save(any())).thenReturn(blockedBlock);
    }

    @Test
    public void shouldReturnAllBlocks() {
        Block block = new Block();
        block.setId(1L);
        List<Block> blockList = Collections.singletonList(block);
        Page<Block> blockPage = new PageImpl<>(blockList);

        when(blockRepository.findAll(any(Pageable.class))).thenReturn(blockPage);

        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);
        when(mockUserDetails.isAdmin()).thenReturn(true);
        Page<BlockDTO> blocks = blockService.findAll(Pageable.unpaged(), mockUserDetails);

        assertEquals(1, blocks.getTotalElements());
        assertEquals(1L, blocks.getContent().get(0).getId());
    }

    @Test
    public void shouldThrowExceptionWhenCreatingBlockWithNonexistentProperty() {
        BlockDTO dto = new BlockDTO();
        dto.setId(1L);
        dto.setPropertyId(1L);
        Calendar cal = Calendar.getInstance();
        dto.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        dto.setEndDate(cal.getTime());

        Mockito.when(propertyRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        assertThrows(PropertyNotFoundException.class, () -> {
            blockService.insert(dto, customUserDetails);
        });
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingBlockWithNonexistentProperty() {
        BlockDTO dto = new BlockDTO();
        dto.setId(1L);
        dto.setPropertyId(1L);
        Calendar cal = Calendar.getInstance();
        dto.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        dto.setEndDate(cal.getTime());

        Mockito.when(propertyRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        assertThrows(PropertyNotFoundException.class, () -> {
            blockService.update(dto, customUserDetails);
        });
    }

    @Test
    public void shouldThrowExceptionWhenCreatingBlockWithUnavailableProperty() {
        BlockDTO dto = new BlockDTO();
        dto.setId(1L);
        dto.setPropertyId(1L);
        Calendar cal = Calendar.getInstance();
        dto.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        dto.setEndDate(cal.getTime());

        Mockito.doThrow(PropertyNotAvailableException.class).when(propertyService).checkPropertyAvailabilityOnPeriod(any(), any(), any());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);
        assertThrows(PropertyNotAvailableException.class, () -> {
            blockService.insert(dto, customUserDetails);
        });
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingBlockWithUnavailableProperty() {
        BlockDTO dto = new BlockDTO();
        dto.setId(2L);
        dto.setPropertyId(1L);
        Calendar cal = Calendar.getInstance();
        dto.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        dto.setEndDate(cal.getTime());
        dto.setStatus(BlockStatus.BLOCKED);

        Mockito.doThrow(PropertyNotAvailableException.class).when(propertyService).checkPropertyAvailabilityOnPeriod(any(), any(), any());
        CustomUserDetails customUserDetails = Mockito.mock(CustomUserDetails.class);
        when(customUserDetails.getId()).thenReturn(1L);
        when(customUserDetails.isAdmin()).thenReturn(true);

        assertThrows(PropertyNotAvailableException.class, () -> {
            blockService.update(dto, customUserDetails);
        });
    }
}