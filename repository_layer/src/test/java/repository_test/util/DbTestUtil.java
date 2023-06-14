package repository_test.util;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Utility for preparing database tables before tests.
 *
 * @author Danylo Proshyn
 */

public class DbTestUtil {

    public static GiftCertificate gc1;
    public static GiftCertificate gc11;
    public static GiftCertificate gc2;
    public static GiftCertificate gc22;
    public static GiftCertificate gc3Spare;

    public static Tag tag1;
    public static Tag tag2;
    public static Tag tag11;
    public static Tag tag22;
    public static Tag tag3Spare;

    public static void createTables(JdbcTemplate jdbcTemplate) {

        jdbcTemplate.execute("""
                create table gift_certificates
                (
                    id               bigint
                        primary key,
                    name             varchar(255) not null,
                    description      varchar(255) not null,
                    price            double       not null,
                    duration         int          not null,
                    created_date      datetime     not null,
                    last_modified_date datetime     not null
                )""");
        jdbcTemplate.execute("""
                create table tags
                (
                    id   bigint auto_increment
                        primary key,
                    name varchar(255) not null
                );
                """);
        jdbcTemplate.execute("""
                create table gift_certificates_tags
                (
                    gc_id  bigint not null,
                    tag_id bigint not null,
                    primary key (tag_id, gc_id),
                    constraint gift_certificates_tags_gift_certificate_id_fk
                        foreign key (gc_id) references gift_certificates (id)
                            on delete cascade,
                    constraint gift_certificates_tags_tag_id_fk
                        foreign key (tag_id) references tags (id)
                            on delete cascade
                );
                """);
    }

    public static void fillTables(JdbcTemplate jdbcTemplate) {

        createEntities();

        jdbcTemplate.update("insert into tags values (?, ?)", tag1.getId(), tag1.getName());
        jdbcTemplate.update("insert into tags values (?, ?)", tag2.getId(), tag2.getName());
        jdbcTemplate.update("insert into tags values (?, ?)", tag11.getId(), tag11.getName());
        jdbcTemplate.update("insert into tags values (?, ?)", tag22.getId(), tag22.getName());

        jdbcTemplate.update("insert into gift_certificates values (?, ?, ?, ?, ?, ?, ?)", gc1.getId(), gc1.getName(),
                gc1.getDescription(), gc1.getPrice(), gc1.getDuration(), gc1.getCreateDate(), gc1.getLastUpdateDate());
        jdbcTemplate.update("insert into gift_certificates values (?, ?, ?, ?, ?, ?, ?)", gc2.getId(), gc2.getName(),
                gc2.getDescription(), gc2.getPrice(), gc2.getDuration(), gc2.getCreateDate(), gc2.getLastUpdateDate());
        jdbcTemplate.update("insert into gift_certificates values (?, ?, ?, ?, ?, ?, ?)", gc11.getId(), gc11.getName(),
                gc11.getDescription(), gc11.getPrice(), gc11.getDuration(), gc11.getCreateDate(),
                gc11.getLastUpdateDate());
        jdbcTemplate.update("insert into gift_certificates values (?, ?, ?, ?, ?, ?, ?)", gc22.getId(), gc22.getName(),
                gc22.getDescription(), gc22.getPrice(), gc22.getDuration(), gc22.getCreateDate(),
                gc22.getLastUpdateDate());

        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 1, 1);
        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 1, 11);
        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 11, 1);
        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 11, 11);
        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 2, 2);
        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 2, 22);
        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 22, 2);
        jdbcTemplate.update("insert into gift_certificates_tags values (?, ?)", 22, 22);
    }

    public static void dropTables(JdbcTemplate jdbcTemplate) {

        jdbcTemplate.execute("drop table gift_certificates_tags");
        jdbcTemplate.execute("drop table gift_certificates");
        jdbcTemplate.execute("drop table tags");
    }

    private static void createEntities() {

        gc1      = new GiftCertificate(1L, "11abc11", "111abc111", 1111, 11111,
                LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.MAX.truncatedTo(ChronoUnit.MILLIS));
        gc11     = new GiftCertificate(11L, "11abc11", "111ABC111", 11111, 111111,
                LocalDateTime.now().plusMinutes(2).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.MIN.truncatedTo(ChronoUnit.MILLIS));
        gc2      = new GiftCertificate(2L, "22ABC22", "222abc222", 2222, 22222,
                LocalDateTime.now().plusMinutes(11).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.MIN.truncatedTo(ChronoUnit.MILLIS));
        gc22     = new GiftCertificate(22L, "22ABC22", "222ABC222", 22222, 222222,
                LocalDateTime.now().plusMinutes(22).truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.MAX.truncatedTo(ChronoUnit.MILLIS));
        gc3Spare = new GiftCertificate(3L, "33", "333", 3333, 33333,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        tag1      = new Tag(1L, "11");
        tag2      = new Tag(2L, "22");
        tag11     = new Tag(11L, "111");
        tag22     = new Tag(22L, "222");
        tag3Spare = new Tag(3L, "33");
    }
}
