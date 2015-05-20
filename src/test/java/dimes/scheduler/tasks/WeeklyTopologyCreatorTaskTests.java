/**
 * 
 */
package dimes.scheduler.tasks;

import com.google.common.net.InetAddresses;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.InetAddress;

/**
 * @author idob
 *
 */

@ContextConfiguration(locations="classpath:/tests-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class WeeklyTopologyCreatorTaskTests {

	@Autowired
	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.netdimes.scheduler.tasks.WeeklyTopologyCreatorTask#createWeeklyTopology(int, int, int)}.
	 */
	@Test
	public final void testCreateWeeklyTopologyIntIntInt() {
		String ip = "192.168.0.1";
		InetAddress addr = InetAddresses.forString(ip);
		System.out.println("InetAddress = " + addr);
		System.out.println("InetAddress hashcode = " + addr.hashCode());
		int address = InetAddresses.coerceToInteger(addr);
		System.out.println("int = " + address);
		System.out.println("Back to str = " + InetAddresses.fromInteger(address));
		System.out.println("Convert to long = " + (address +  0xFFFFFFFFL));
	}

}
