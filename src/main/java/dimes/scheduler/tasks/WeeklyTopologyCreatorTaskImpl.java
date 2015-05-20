/**
 * 
 */
package dimes.scheduler.tasks;

import org.joda.time.LocalDateTime;
import org.netdimes.scheduler.services.EntityCreatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * An implemention of {@link org.netdimes.scheduler.tasks.WeeklyTopologyCreatorTask}
 * 
 * @author idob
 * @see {@link org.netdimes.scheduler.tasks.WeeklyTopologyCreatorTask}
 */

//@Service("weeklyTopologyCreatorTask")
class WeeklyTopologyCreatorTaskImpl implements WeeklyTopologyCreatorTask {

	private static final Logger logger = LoggerFactory.getLogger(WeeklyTopologyCreatorTaskImpl.class);
	@Autowired
	private EntityCreatorService ipTopologyCreatorService;
	
	/* (non-Javadoc)
	 * @see org.netdimes.scheduler.tasks.WeeklyTopologyCreatorTask#createWeeklyTopology()
	 */
	@Scheduled(cron="0 0 6 ? * MON")
	public void createWeeklyTopology() {
		
		LocalDateTime localDateTime = new LocalDateTime().minusDays(1);
		int weekyear = localDateTime.getWeekyear();
		int WeekOfWeekyear = localDateTime.getWeekOfWeekyear();
		
		logger.info("Creating new topology from scheduled method");
		
		createWeeklyTopology(weekyear, WeekOfWeekyear);
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.netdimes.scheduler.tasks.WeeklyTopologyCreatorTask#createWeeklyTopology(int, int, int)
	 */
	public void createWeeklyTopology(int year, int startWeek, int endWeek) {
		
		logger.info("Creating weekly topology. Year : {} , Start Week : {} , End Week : {}", new int[] {year, startWeek, endWeek});
		
		for (int topologyWeek = startWeek;  topologyWeek <= endWeek;  topologyWeek++) {
		
			createWeeklyTopology(year, topologyWeek);
			
		}
		
		logger.info("Done Creating weekly topology. Year : {} , Start Week : {} , End Week : {}", new int[] {year, startWeek, endWeek});
	}

	/* (non-Javadoc)
	 * @see org.netdimes.scheduler.tasks.WeeklyTopologyCreatorTask#createWeeklyTopology(int, int)
	 */
	public void createWeeklyTopology(int year, int week) {
		
		logger.info("Creating weekly topology. Year : {} , Week : {}", year, week);
		
		ipTopologyCreatorService.createWeeklyTopology(year, week);
		
		// TODO - add all the other topology services (as, routers, metropology, PoPs)
		
		logger.info("Done creating weekly topology. Year : {} , Week : {}", year, week);
		
	}
	
	
	/**
	 * The main method exists in case that the topology creator needs to run for specific week/s in an initiated way.<br>
	 * Regularly, the topology creator should be activated from the SchedulerStarter for creating last week topology.  
	 * 
	 * 
	 * @param args includes year, startWeek and endWeek for creating the topology.
	 */
	public static void main(String[] args) {

//		WeeklyTopologyCreatorTaskImpl task = new WeeklyTopologyCreatorTaskImpl();
//		task.createWeeklyTopology();
		ApplicationContext applicationContext = new GenericXmlApplicationContext("classpath:/META-INF/spring/app-context.xml");
		
		int year = Integer.parseInt(args[0]);
		int startWeek = Integer.parseInt(args[1]);
		int endWeek = Integer.parseInt(args[2]);
		
		WeeklyTopologyCreatorTask task = (WeeklyTopologyCreatorTask) applicationContext.getBean("weeklyTopologyCreatorTask");
		task.createWeeklyTopology(year, startWeek, endWeek);
		
	}
}
