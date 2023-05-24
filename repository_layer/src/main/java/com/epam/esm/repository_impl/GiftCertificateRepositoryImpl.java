package com.epam.esm.repository_impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.util_repository.DbFields.*;

/**
 * Implementation of DAO Interface {@link com.epam.esm.repository.GiftCertificateRepository}.
 *
 * @author Danylo Proshyn
 */

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private final JdbcTemplate               jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Autowired
    public GiftCertificateRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParamJdbcTemplate) {

        this.jdbcTemplate           = jdbcTemplate;
        this.namedParamJdbcTemplate = namedParamJdbcTemplate;
    }

    @Override
    public void insertEntity(GiftCertificate gc) {

        jdbcTemplate.update("insert into gift_certificates values (?, ?, ?, ?, ?, ?, ?)", gc.getId(), gc.getName(),
                gc.getDescription(), gc.getPrice(), gc.getDuration(), gc.getCreateDate(), gc.getLastUpdateDate());
    }

    @Override
    public Optional<GiftCertificate> getEntity(Long id) {

        Optional<GiftCertificate> result;
        try {
            result = Optional.ofNullable(jdbcTemplate.queryForObject("select * from gift_certificates where id = ?",
                    new GiftCertificateRowMapper(), id));
        } catch (DataAccessException e) {
            result = Optional.empty();
        }

        return result;
    }

    @Override
    public int updateEntity(GiftCertificate gc) {

        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(gc);

        return namedParamJdbcTemplate.update("update gift_certificates set name = :name, description = :description," +
                        "price = :price, duration = :duration, create_date = :createDate, last_update_date = :lastUpdateDate where id = :id",
                namedParameters);
    }

    @Override
    public int deleteEntity(Long id) {

        return jdbcTemplate.update("delete from gift_certificates where id = ?", id);
    }

    @Override
    public List<GiftCertificate> getAll(
            Optional<String> tagName, Optional<String> namePart, Optional<String> descriptionPart,
            Optional<String> nameOrder, Optional<String> createDateOrder) {

        String conditionClause = "where current_date() < timestampadd(day, duration, create_date)";
        if (tagName.isPresent()) {
            conditionClause += String.format(" and id in (select gc_id from gift_certificates_tags where tag_id in " +
                    "(select id from tags where name = '%s'))", tagName.get());
        }
        if (namePart.isPresent()) {
            conditionClause += String.format(" and lower(name) like '%s'", '%' + namePart.get().toLowerCase() + '%');
        }
        if (descriptionPart.isPresent()) {
            conditionClause +=
                    String.format(" and lower(description) like '%s'", '%' + descriptionPart.get().toLowerCase() + '%');
        }

        String orderClause = "";
        if (nameOrder.isPresent()) {
            orderClause = " order by name " + nameOrder.get();
            if (createDateOrder.isPresent()) {
                orderClause += ", create_date " + createDateOrder.get();
            }
        } else if (createDateOrder.isPresent()) {
            orderClause = " order by create_date " + createDateOrder.get();
        }

        return jdbcTemplate.query("select * from gift_certificates " + conditionClause + orderClause,
                new GiftCertificateRowMapper());
    }

    @Override
    public void addTagToEntity(Long gcId, Long tagId) {

        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", gcId, tagId);
    }

    @Override
    public int deleteAllTagsForEntity(Long gcId) {

        return jdbcTemplate.update("delete from gift_certificates_tags where gc_id = ?", gcId);
    }

    private static class GiftCertificateRowMapper implements RowMapper<GiftCertificate> {

        @Override
        public GiftCertificate mapRow(ResultSet rs, int rowNum) throws SQLException {

            return GiftCertificate.builder().id(rs.getLong(GIFT_CERTIFICATE_ID))
                    .name(rs.getString(GIFT_CERTIFICATE_NAME)).description(rs.getString(GIFT_CERTIFICATE_DESCRIPTION))
                    .price(rs.getDouble(GIFT_CERTIFICATE_PRICE)).duration(rs.getInt(GIFT_CERTIFICATE_DURATION))
                    .createDate(rs.getObject(GIFT_CERTIFICATE_CREATE_DATE, LocalDateTime.class))
                    .lastUpdateDate(rs.getObject(GIFT_CERTIFICATE_LAST_UPDATE_DATE, LocalDateTime.class)).build();
        }
    }
}
