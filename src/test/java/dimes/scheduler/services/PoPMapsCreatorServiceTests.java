/**
 * 
 */
package dimes.scheduler.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit testing for org.netdimes.scheduler.services.PoPMapsCreatorServiceImpl
 * 
 * @author idob
 *
 */

@ContextConfiguration(locations="classpath:/tests-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class PoPMapsCreatorServiceTests {

	@Autowired
	private EntityCreatorService popMapsCreatorService;
	
	@Test
	public void createWeeklyEntityTest() {
		popMapsCreatorService.createWeeklyEntity(2014, 18);
	}
}
