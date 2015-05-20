/**
 * 
 */
package dimes.scheduler.tasks;

/**
 * Main interface for defining the contract of the periodical topology creator.<br>
 * Includes 2 methods:
 * <ul> 
 * 	<li>{@link #createWeeklyTopology()} runs weekly on scheduled time in order to create the last week topology</li>
 * 	<li>{@link #createWeeklyTopology(int week, int startWeek, int endWeek)} runs on demand from {@link SchedulerStarter.main} and creates bulk of weekly topology tables</li> 
 * </ul>
 * 
 * @author idob
 * @version 1.0
 *
 */

public interface WeeklyTopologyCreatorTask {


	
	/**
	 * Creating full weekly topology of the last running week
	 */
	public void createWeeklyTopology();
	
	/**
	 * Creating full weekly topology of the asked week of year
	 * 
	 * @param year The year of the topology 
	 * @param week The week of year of the topology
	 */
	public void createWeeklyTopology(int year, int week);
	
	/**
	 * Creating full weekly topology of the asked period of the year include both startWeek and endWeek
	 * 
	 * @param year The year of the topology
	 * @param startWeek First week of the period
	 * @param endWeek Last week of the period
	 */
	public void createWeeklyTopology(int year, int startWeek, int endWeek);
	
}
