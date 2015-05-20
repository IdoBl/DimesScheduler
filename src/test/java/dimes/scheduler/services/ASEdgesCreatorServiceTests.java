package dimes.scheduler.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by user on 17/05/2015.
 */
@ContextConfiguration(locations="classpath:/scheduler-config.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ASEdgesCreatorServiceTests {

    @Autowired
    private EntityCreatorService asEdgesCreatorService;

    @Test
    public void testCreateWeeklyEntity() {

        asEdgesCreatorService.createWeeklyEntity(2015,3);
    }
}
