package dimes.scheduler.dal;

import static  org.junit.Assert.*;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Unit tests for RawResMainDAO implementation
 *
 * Created by Ido Blutman on 18/02/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:scheduler-config.xml")
public class RawResMainDAOTest {

    @Autowired
    private RawResMainDAO rawResMainDAO;

    @Test
    public void testGetRogueAgentList() throws Exception {
        List<Integer> rogueAgentsList = rawResMainDAO.getRogueAgentList(2014, 1);
        assertThat(rogueAgentsList.size(), Is.is(29));
    }
}
