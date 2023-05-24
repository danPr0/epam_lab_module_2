package service_test;

import com.epam.esm.dto.TagDTO;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service_impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest extends Mockito {

    @InjectMocks
    private TagServiceImpl tagService;

    @Mock
    private TagRepository tagRepository;

    private Tag tag1;
    private Tag tag2;

    private TagDTO tag1DTO;
    private TagDTO tag2DTO;

    {
        tag1 = new Tag(1L, "1");
        tag2 = new Tag(2L, "2");

        tag1DTO = new TagDTO(1L, "1");
        tag2DTO = new TagDTO(2L, "2");
    }

    @Test
    public void testAddTag() {

        doNothing().when(tagRepository).insertEntity(tag1);

        assertTrue(tagService.addTag(tag1DTO));
        verify(tagRepository).insertEntity(tag1);

        doThrow(new DataAccessException("") {}).when(tagRepository).insertEntity(tag2);

        assertFalse(tagService.addTag(tag2DTO));
        verify(tagRepository).insertEntity(tag2);
    }

    @Test
    public void testGetTag() {

        when(tagRepository.getEntity(tag1.getId())).thenReturn(Optional.of(tag1));

        assertEquals(Optional.of(tag1DTO), tagService.getTag(tag1DTO.getId()));
        verify(tagRepository).getEntity(tag1.getId());

        when(tagRepository.getEntity(tag2.getId())).thenReturn(Optional.empty());

        assertTrue(tagService.getTag(tag2DTO.getId()).isEmpty());
        verify(tagRepository).getEntity(tag2.getId());
    }

    @Test
    public void testDeleteTag() {

        when(tagRepository.deleteEntity(tag1.getId())).thenReturn(1);

        assertTrue(tagService.deleteTag(tag1DTO.getId()));
        verify(tagRepository).deleteEntity(tag1.getId());

        when(tagRepository.deleteEntity(tag2.getId())).thenReturn(0);

        assertFalse(tagService.deleteTag(tag2DTO.getId()));
        verify(tagRepository).deleteEntity(tag2.getId());
    }
}
