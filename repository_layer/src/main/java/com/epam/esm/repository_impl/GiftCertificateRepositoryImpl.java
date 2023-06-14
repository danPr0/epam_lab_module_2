package com.epam.esm.repository_impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.conf.ParamType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.util_repository.DbFields.*;
import static org.jooq.impl.DSL.*;

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
                        "price = :price, duration = :duration, created_date = :createDate, last_modified_date = " +
                        ":lastUpdateDate where id = :id",
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

        SelectConditionStep<Record> query = select().from(table("gift_certificates"))
                .where("current_date() < timestampadd(day, duration, created_date)");
        if (tagName.isPresent()) {
            query = query.andExists(select(field("gc_id")).from(table("gift_certificates_tags")).leftJoin(table("tags"))
                    .on(field("tag_id").eq(field("tags.id"))).where(field("gift_certificates.id").eq(field("gc_id")))
                    .and(field("tags.name").eq(tagName.get())));
        }

        if (namePart.isPresent()) {
            query = query.and(lower(field("name", String.class)).like('%' + namePart.get().toLowerCase() + '%'));
        }
        if (descriptionPart.isPresent()) {
            query = query.and(
                    lower(field("description", String.class)).like('%' + descriptionPart.get().toLowerCase() + '%'));
        }

        List<SortField<Object>> sortFieldList = new ArrayList<>();
        nameOrder.ifPresent(s -> sortFieldList.add(field("name").sort(SortOrder.valueOf(s.toUpperCase()))));
        createDateOrder.ifPresent(
                s -> sortFieldList.add(field("created_date").sort(SortOrder.valueOf(s.toUpperCase()))));
        sortFieldList.add(field("id").sort(SortOrder.ASC));

        String sql = query.orderBy(sortFieldList).getSQL(ParamType.INLINED);

        return jdbcTemplate.query(sql, new GiftCertificateRowMapper());
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
                    .createDate(rs.getObject(GIFT_CERTIFICATE_CREATED_DATE, LocalDateTime.class))
                    .lastUpdateDate(rs.getObject(GIFT_CERTIFICATE_LAST_MODIFIED_DATE, LocalDateTime.class)).build();
        }
    }
}
