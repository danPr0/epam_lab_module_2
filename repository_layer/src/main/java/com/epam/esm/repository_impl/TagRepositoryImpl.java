package com.epam.esm.repository_impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.util_repository.DbFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of DAO Interface {@link com.epam.esm.repository.TagRepository}.
 *
 * @author Danylo Proshyn
 */

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagRepositoryImpl(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertEntity(Tag tag) {

        jdbcTemplate.update("insert into tags values (?, ?)", tag.getId(), tag.getName());
    }

    @Override
    public Optional<Tag> getEntity(Long id) {

        Optional<Tag> result;
        try {
            result = Optional.ofNullable(
                    jdbcTemplate.queryForObject("select * from tags where id = ?", new TagRowMapper(), id));
        } catch (DataAccessException e) {
            result = Optional.empty();
        }

        return result;
    }

    @Override
    public int deleteEntity(Long id) {

        return jdbcTemplate.update("delete from tags where id = ?", id);
    }

    @Override
    public List<Tag> getAllByGiftCertificate(Long giftCertificateId) {

        List<Long> tagIds =
                jdbcTemplate.queryForList("select * from gift_certificates_tags where gc_id = ?", giftCertificateId)
                        .stream().map(m -> (Long) m.get(DbFields.GIFT_CERTIFICATES_TAGS_TAG_ID)).toList();

        return tagIds.stream().map(id -> getEntity(id).get()).toList();
    }

    private static class TagRowMapper implements RowMapper<Tag> {

        @Override
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {

            return Tag.builder().id(rs.getLong(DbFields.TAG_ID)).name(rs.getString(DbFields.TAG_NAME)).build();
        }
    }
}
