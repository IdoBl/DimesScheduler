package dimes.scheduler.services;

/**
 * General interface for services which create weekly entities (e.g. AS Prefix tables from route-views / who-is, Topology tables etc.)
 * 
 * 
 * @author idob
 */
public interface EntityCreatorService {
	
	/**
	 * Method for creating weekly entity
	 * 
	 * @param year year of the entity
	 * @param week week-of-year of the entity
	 */
	public void createWeeklyEntity(int year, int week);

}
