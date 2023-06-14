package repository_test;

import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository_impl.TagRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import repository_test.config.DataSourceConfig;
import repository_test.util.DbTestUtil;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used for testing DAO repository {@link com.epam.esm.repository_impl.TagRepositoryImpl}.
 *
 * @author Danylo Proshyn
 */

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DataSourceConfig.class, TagRepositoryImpl.class})
@ActiveProfiles("test")
public class TagRepositoryTest {

    private final TagRepository tagRepository;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagRepositoryTest(TagRepository tagRepository, JdbcTemplate jdbcTemplate) {

        this.tagRepository = tagRepository;

        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    public void createAndFillTable() {

        DbTestUtil.createTables(jdbcTemplate);
        DbTestUtil.fillTables(jdbcTemplate);
    }

    @AfterEach
    public void dropTable() {

        DbTestUtil.dropTables(jdbcTemplate);
    }

    public int countRows() {

        //noinspection DataFlowIssue
        return jdbcTemplate.queryForObject("select count(*) from tags", Integer.class);
    }

    @Test
    public void testGet() {

        Assertions.assertEquals(DbTestUtil.tag1, tagRepository.getEntity(DbTestUtil.tag1.getId()).get());

        assertTrue(tagRepository.getEntity(DbTestUtil.tag3Spare.getId()).isEmpty());
    }

    @Test
    public void testInsert() {

        tagRepository.insertEntity(DbTestUtil.tag3Spare);

        assertEquals(5, countRows());
        Assertions.assertEquals(DbTestUtil.tag3Spare, tagRepository.getEntity(DbTestUtil.tag3Spare.getId()).get());

        assertThrows(DuplicateKeyException.class, () -> tagRepository.insertEntity(DbTestUtil.tag3Spare));
        assertEquals(5, countRows());
    }

    @Test
    public void testDelete() {

        assertEquals(1, tagRepository.deleteEntity(DbTestUtil.tag2.getId()));
        assertEquals(3, countRows());

        assertTrue(tagRepository.getEntity(DbTestUtil.tag2.getId()).isEmpty());
        assertEquals(0, tagRepository.deleteEntity(DbTestUtil.tag2.getId()));
    }

    @Test
    public void testGetAllByGiftCertificate() {

        List<Tag> tags = tagRepository.getAllByGiftCertificate(DbTestUtil.gc1.getId());

        assertArrayEquals(new Long[]{DbTestUtil.tag1.getId(), DbTestUtil.tag11.getId()},
                tags.stream().map(Tag::getId).sorted().toArray());

        assertEquals(0, tagRepository.getAllByGiftCertificate(DbTestUtil.gc3Spare.getId()).size());
    }
}
