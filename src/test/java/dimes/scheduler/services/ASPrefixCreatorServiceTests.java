/**
 * 
 */
package dimes.scheduler.services;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * 
 * @author idob
 *
 */

@ContextConfiguration(locations="classpath:/tests-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ASPrefixCreatorServiceTests {

	@Autowired
	private EntityCreatorService asPrefixCreatorService;
	
	@Test
	public void createWeeklyEntityTest() {
			LocalDate now = new LocalDate();
			
			asPrefixCreatorService.createWeeklyEntity(now.getYear(), now.getWeekOfWeekyear());		
	}

}
