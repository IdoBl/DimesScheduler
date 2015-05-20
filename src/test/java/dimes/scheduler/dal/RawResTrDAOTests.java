package dimes.scheduler.dal;

import dimes.scheduler.dal.entities.TrNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dimes.scheduler.dal.RawResTrDAO;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for RawResTrDAO implementation
 *
 * Created by Ido Blutman on 17/02/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:scheduler-config.xml")
public class RawResTrDAOTests {

    @Autowired
    private RawResTrDAO rawResTrDAO;

    @Test
    public void testGetMinMeasurementSeq() {
        long minMeasurementSeq = rawResTrDAO.getMinMeasurementSeq( 2015 , 3 );
        assertEquals( 312026380 , minMeasurementSeq );
    }

    @Test
    public void testGetMaxMeasurementSeq() {
        long maxMeasurementSeq = rawResTrDAO.getMaxMeasurementSeq(2015, 3);
        assertEquals( 329138505, maxMeasurementSeq );

    }

    @Test
    public void testGetMeasurementsBySeq() {
        List<TrNode> nodes = rawResTrDAO.getMeasurementsBySeq( 2015 , 3 , 312026380 , 312036380);
        assertEquals(41761 , nodes.size());
    }

}
