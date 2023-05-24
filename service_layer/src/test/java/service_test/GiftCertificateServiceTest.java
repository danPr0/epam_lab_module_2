package service_test;

import com.epam.esm.dto.GiftCertificateDTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service_impl.GiftCertificateServiceImpl;
import com.epam.esm.util_service.DTOUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GiftCertificateServiceTest extends Mockito {

    @InjectMocks
    private GiftCertificateServiceImpl gcService;

    @Mock
    private GiftCertificateRepository gcRepository;
    @Mock
    private TagRepository             tagRepository;
    @Mock
    private DataSourceTransactionManager transactionManager;

    public GiftCertificate gc1;
    public GiftCertificate gc2;

    public GiftCertificateDTO gc1DTO;
    public GiftCertificateDTO gc2DTO;

    private Tag tag1;
    private Tag tag2;

    {
        tag1 = new Tag(1L, "1");
        tag2 = new Tag(2L, "2");

        gc1 = new GiftCertificate(1L, "1", "1", 1, 1, LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.MAX.truncatedTo(ChronoUnit.MILLIS));
        gc2 = new GiftCertificate(2L, "2", "2", 2, 2, LocalDateTime.now().plusMinutes(2).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.MAX.truncatedTo(ChronoUnit.MILLIS));

        gc1DTO = DTOUtil.convertToDTO(gc1, List.of(tag1));
        gc2DTO = DTOUtil.convertToDTO(gc2, List.of(tag2));
    }

    @Test
    public void testAddGiftCertificate() {

        doNothing().when(gcRepository).insertEntity(any());
        doNothing().when(transactionManager).commit(any());

        assertTrue(gcService.addGiftCertificate(gc1DTO));
        verify(gcRepository).insertEntity(any());
        verify(transactionManager).commit(any());

        doThrow(new DataAccessException("") {}).when(gcRepository).insertEntity(any());
        doNothing().when(transactionManager).rollback(any());

        assertFalse(gcService.addGiftCertificate(gc2DTO));
        verify(gcRepository, times(2)).insertEntity(any());
        verify(transactionManager).rollback(any());
    }

    @Test
    public void testGetGiftCertificate() {

        when(gcRepository.getEntity(gc1.getId())).thenReturn(Optional.of(gc1));
        when(tagRepository.getAllByGiftCertificate(gc1.getId())).thenReturn(List.of(tag1));

        assertEquals(Optional.of(gc1DTO), gcService.getGiftCertificate(gc1DTO.getId()));
        verify(gcRepository).getEntity(gc1.getId());

        when(gcRepository.getEntity(gc2.getId())).thenReturn(Optional.empty());

        assertTrue(gcService.getGiftCertificate(gc2DTO.getId()).isEmpty());
        verify(gcRepository).getEntity(gc2.getId());
    }

    @Test
    public void testUpdateGiftCertificate() {

        when(gcRepository.getEntity(gc1.getId())).thenReturn(Optional.of(gc1));
        doNothing().when(transactionManager).commit(any());

        assertTrue(gcService.updateGiftCertificate(gc1DTO));
        verify(gcRepository).getEntity(gc1.getId());
        verify(transactionManager).commit(any());

        when(gcRepository.getEntity(gc2.getId())).thenReturn(Optional.empty());
        doNothing().when(transactionManager).rollback(any());

        assertFalse(gcService.updateGiftCertificate(gc2DTO));
        verify(gcRepository).getEntity(gc2.getId());
        verify(transactionManager).rollback(any());
    }

    @Test
    public void deleteGiftCertificate() {

        when(gcRepository.deleteEntity(gc1.getId())).thenReturn(1);

        assertTrue(gcService.deleteGiftCertificate(gc1DTO.getId()));
        verify(gcRepository).deleteEntity(gc1.getId());

        when(gcRepository.deleteEntity(gc2.getId())).thenReturn(0);

        assertFalse(gcService.deleteGiftCertificate(gc2DTO.getId()));
        verify(gcRepository).deleteEntity(gc2.getId());
    }

    @Test
    public void testGetAll() {

        when(gcRepository.getAll(Optional.of(tag1.getName()), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty())).thenReturn(List.of(gc1));
        when(tagRepository.getAllByGiftCertificate(gc1.getId())).thenReturn(List.of(tag1));

        assertEquals(List.of(gc1DTO),
                gcService.getAll(Optional.of(tag1.getName()), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty()));
        verify(gcRepository).getAll(Optional.of(tag1.getName()), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty());
    }
}
