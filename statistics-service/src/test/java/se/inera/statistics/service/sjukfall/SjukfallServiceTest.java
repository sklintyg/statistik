package se.inera.statistics.service.sjukfall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:process-log-impl-test.xml" })
@Transactional
public class SjukfallServiceTest extends SjukfallService {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void registering_period_returns_id() {
        String id = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-25"));
        assertNotNull(id);
    }

    @Test
    public void registering_same_period_different_users_returns_different_id() {
        String id1 = register("personnummer1", "vardgivare", date("2013-01-01"), date("2013-01-25"));
        String id2 = register("personnummer2", "vardgivare", date("2013-01-01"), date("2013-01-25"));
        assertFalse(id1.equals(id2));
    }

    @Test
    public void registering_same_period_user_user_different_vardgivare_returns_different_id() {
        String id1 = register("personnummer", "vardgivare1", date("2013-01-01"), date("2013-01-25"));
        String id2 = register("personnummer", "vardgivare2", date("2013-01-01"), date("2013-01-25"));
        assertFalse(id1.equals(id2));
    }

    @Test
    public void registering_overlapping_periods_returns_same_id() {
        String id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        String id2 = register("personnummer", "vardgivare", date("2013-01-25"), date("2013-02-15"));
        assertEquals(id1, id2);
    }

    @Test
    public void registering_close_periods_returns_same_id() {
        String id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        String id2 = register("personnummer", "vardgivare", date("2013-01-25"), date("2013-02-15"));
        assertEquals(id1, id2);
    }

    @Test
    public void registering_not_close_periods_returns_different_id() {
        String id1 = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        int expired = expire(date("2013-02-05"));
        String id2 = register("personnummer", "vardgivare", date("2013-02-10"), date("2013-02-25"));
        assertEquals(1, expired);
        assertFalse(id1.equals(id2));
    }

    @Test
    public void expire_expires_only_old() {
        String shouldExpire = register("personnummer", "vardgivare", date("2013-01-01"), date("2013-01-28"));
        String active = register("personnummer", "vardgivare2", date("2013-01-01"), date("2013-02-01"));

        int expired = expire(date("2013-02-05"));

        String newSjukfall = register("personnummer", "vardgivare", date("2013-02-10"), date("2013-02-25"));
        String stillActive = register("personnummer", "vardgivare2", date("2013-02-10"), date("2013-02-25"));

        assertEquals(1, expired);
        assertFalse(shouldExpire.equals(newSjukfall));
        assertEquals(active, stillActive);
    }

    private Date date(String stringDate) {
        try {
            return dateFormat.parse(stringDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
