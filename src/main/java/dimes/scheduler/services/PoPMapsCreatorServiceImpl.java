package dimes.scheduler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for creating 2-weeks pop maps.
 * 
 * @author idob
 *
 */

@Service("popMapsCreatorService") 
class PoPMapsCreatorServiceImpl implements EntityCreatorService {

	private static final Logger logger = LoggerFactory.getLogger(PoPMapsCreatorServiceImpl.class);
	
	private String trCleanTblName;
	private int year;
	private int firstWeek;
//	private int secondWeek;
	
	@Autowired
	private RawResMainDAO rawResMainDAO;
	@Autowired
	private RawResTrDAO rawResTrDAO;
	@Autowired
	private RawResTrCleanDAO rawResTrCleanDAO;
	/** 
	 * The main method for creating the 2-weeks periodical maps.
	 * 
	 * @see org.netdimes.scheduler.services.EntityCreatorService#createWeeklyEntity(int, int)
	 */
	@Override
	public void createWeeklyEntity(int year, int firstWeek) {
		// initialization of members
		this.year = year;
		this.firstWeek = firstWeek;
		
		logger.info("Creating PoP map. Year : {} Weeks : {} -> {}", new Integer[]{year, firstWeek, (firstWeek+1)});
		
		// Phase 1: creating clean data 
		createCleanTrData();

	}


	/*
	 * Method for creating clean TR table (with out rogue agents measurements) for the 2 weeks period in srv-dimesmysql:dimes_pop_results
	 */

	private void createCleanTrData() {
		
		// Step 1: Creating set of measurement index of rogue agents in the 2 weeks
		Set<Long> measSeqBlackSet = new HashSet<Long>();
		int iterationWeek = firstWeek;

		// Running on the relevant 2 weeks:
		do {
			// raw_res_tr: getting all MainSeqNum with negative best_time
			Set<Long> rogueTrSeqSet = rawResTrDAO.getNegMeasSeq(year, iterationWeek);
			logger.debug("Week {} : Negative MeasSeq -> {}", iterationWeek, rogueTrSeqSet.size());

			// raw_res_main: getting distinct indexes of rogue agents (based on the negative time sequence values)
			Set<Integer> rogueAgentIndexSet = rawResMainDAO.getRogueAgentIndexSet(year, iterationWeek, rogueTrSeqSet);
			logger.debug("Week {} : Rogue agents -> {}", iterationWeek, rogueAgentIndexSet.size());

			// raw_res_main: for this rogue agents getting all the measurements this week (i.e. all the sequences to be ignored in the tr table)
			Set<Long> rogueAgentSeqSet = rawResMainDAO.getAgentMeasSeq(year, iterationWeek, rogueAgentIndexSet);
			logger.debug("Week {} : MeasSeq black list -> {}", iterationWeek, rogueAgentSeqSet.size());

			//Adding to the black set
			measSeqBlackSet.addAll(rogueAgentSeqSet);
			iterationWeek++;
		} while (iterationWeek < firstWeek + 2);

//		// Step 2: Creating the test bed - the full table in dimes_pop_results
//		trCleanTblName = rawResTrCleanDAO.createCleanTrTable(year, firstWeek);
//		logger.debug("Clean table was created -> {}", trCleanTblName);
//		// Step 3: clean the new table from measurements of rogue agents
//		int deletedRows = rawResTrCleanDAO.deleteMeasurements(trCleanTblName, measSeqBlackSet);
//		logger.debug("Deleting rougue agents measurements from {}. Number of deleted rows -> {}", trCleanTblName, deletedRows);

		// Debug purposes:
		trCleanTblName = "raw_res_tr_clean_2014_18_19";

		// Step 4: Getting MainSeq and Seq of hops with alternative. No use for further hops in the trace after the first one with alt.
		List<Object[]> rawResTrCleanDAO.getTrHopsWithAlt(trCleanTblName);
	}

	// Method for creating a string presentation to be parsed in the creation of LocalDate
//	private String getDateAsStr(int weekOfYear, int dayOfWeek) {
//		String weekOfYearStr = String.valueOf(weekOfYear).length() == 1 ? "0"+weekOfYear : Integer.toString(weekOfYear);
//		return year+"-W"+weekOfYearStr+"-"+dayOfWeek;
//	}

}
