package dimes.scheduler.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.net.InetAddresses;
import org.joda.time.LocalDateTime;
import org.netdimes.orm.dimes_results_old.dao.RawResTrAltDAO;
import org.netdimes.orm.dimes_results_old.dao.RawResTrDAO;
import org.netdimes.orm.dimes_topology.dao.IPNodesDAO;
import org.netdimes.orm.dimes_topology.dao.UnknownNodesDAO;
import org.netdimes.orm.dimes_topology.dao.WeeklyUnknownNodesDAO;
import org.netdimes.orm.dimes_topology.entities.AbstractTopologyEntity;
import org.netdimes.orm.dimes_topology.entities.IPNodeEntity;
import org.netdimes.orm.util.MinMaxMeasurementSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Creating IP level Topology from old raw_res_traceroute and raw_res_main tables.<br>
 * The IP will be based on HopAddressStr (from raw_res_traceroute tables) conversion to int.
 * 
 * @version 1.0
 * @author idob
 */

/*
 * Points:
 * 	1. Chunk size read from DB (1M of MainSequenceNum???)
 * 	2. In case of tail of "unknown" - Does it enter the DB? should i check this option? - According the Boaz a tail of "unknowns" is omitted from the rsults.
 * 	3. unknown nodes - Updated table with most of data (IPs and gaps). Thin weekly table (dates).
 *  4. Weekly empty tables will be generated by spring (dal-context.xml: schemaUpdate = true) - columns of the tables defined in @column(columnDefinition)
 *  
 * Algorithm (See implementation in code):
 * 1. Create MultiMap for saving  edges. Key is an AbstraceNodeEntity (edge source). Value is a Set of AbstraceNodeEntity (edges connected to key)  
 * 2. Read from weekly raw TR table chunk of rows (Columns : MainSequenceNum, Sequence, hopAddressStr, hopNameStr, hasAlternative). From Main: StartTime (GMT) of measurement.
 * 3. Loop over MainSequenceNum (measurements)
 * 		4.a. Loop over Sequence of measurement (hops)
 * 			4.a.1 Take the HopAddressStr and check if it is "unknown" or dotted IP.
 * 					4.a.1.I "unknown": 
 * 						check if it is the last hop OR if there are no known nodes before the end of the measurement. 
 * 							Yes -> Ignore this hop and the next ones. Continue to next measurement.
 * 							No -> check for existence in HashSet:
 * 									Yes -> Update dateOfValidation
 * 									No -> create the object, populate and add to HashSet of Unknown Nodes.
 * 					
 * 					4.a.1.II dotted IP: 				
 * 						Check for node existent in HashMap.
 * 									Yes ->  update date of validation.
 * 									No -> 	create the object, populate and add to HashMap of known Nodes.
 * 											Object population:
 * 												dateOfDiscovery = dateOfValidation 
 * 												ip = hopAddressStr convert to int (TODO - create converter)
 * 												asNum, asPrefix = to check Jonathan Gazith's tree.
 * 			4.a.2 Check "hasAlternative"
 * 					True -> Read the alternative and process it (3.a.1). If there is another alt (alt to alt) process it too.
 * 					Ignore the next hops and continue to next measurement 
 * 			 	
 */

//@Service("ipTopologyCreatorService")
class IPTopologyCreatorServiceImpl implements EntityCreatorService {

	private static final Logger logger = LoggerFactory.getLogger(IPTopologyCreatorServiceImpl.class);
	
	// Chunk size of processed measurement sequence:
	private static final long MEASUREMENT_SEQUENCE_CHUNK_SIZE = 1000000L;
	
	// DAOs for old dimes_results schema:
/*	@Autowired
	private RawResMainDAO rawResMainDAO;
*/	@Autowired
	private RawResTrDAO rawResTrDAO;
	@Autowired
	private RawResTrAltDAO rawResTrAltDAO;
	
	// DAOs for new topology tables:
	@Autowired
	private IPNodesDAO ipNodesDAO;
	@Autowired
	private UnknownNodesDAO unknownNodesDAO;
	@Autowired
	private WeeklyUnknownNodesDAO weeklyUnknownNodesDAO;
		
	public void createWeeklyEntity(int year, int week) {
		
		logger.info("Creating IP Node Topology. Year : {} , Week : {}", year, week);
		
		//*************************************************************************************************************
		// Create MultiMap for saving nodes and edges:

		SetMultimap<AbstractTopologyEntity, AbstractTopologyEntity> edgesMap = HashMultimap.create();
		
		//*************************************************************************************************************
		// Checking min and max of MainSequenceNum of the weekly raw data table - range for running the service - and initiating the different counters
		
		MinMaxMeasurementSequence minMaxMeasurementSequence = rawResTrDAO.geMinMaxMeasurementSequence();
		
		long weeklyMinMeasurementSequence = minMaxMeasurementSequence.getMinMeasurementSequence();
		long weeklyMaxMeasurementSequence = minMaxMeasurementSequence.getMaxMeasurementSequence();
		
		logger.info("Week measurementSequence : {} -> {}", weeklyMinMeasurementSequence, weeklyMaxMeasurementSequence);
		
		// init iteration variables
		
		long iterationMinValue = weeklyMinMeasurementSequence;
		long iterationMaxValue = weeklyMaxMeasurementSequence - weeklyMinMeasurementSequence < MEASUREMENT_SEQUENCE_CHUNK_SIZE ? weeklyMaxMeasurementSequence : weeklyMinMeasurementSequence + MEASUREMENT_SEQUENCE_CHUNK_SIZE;
		
		//**************************************************************************************************************
		// Running the topology creator process on the min - max measurementSequence
		
		while (iterationMinValue < weeklyMaxMeasurementSequence) {
			
			logger.info("Iteration index : {} -> {}", iterationMinValue, iterationMaxValue);
			
			// Get measurements from DAO for iteration values
			List<Object[]> measurementsList = rawResTrDAO.getTopologyDataFromWeeklyTbl(year, week, iterationMinValue, iterationMaxValue);
			int counter = 0;
			
			// Running over the iteration measurements:
			while (!measurementsList.isEmpty()) {
				
				// Collecting all the rows which belong to single measurement (all rows with the same measurementSeq) into one list - to be processed as connected edges 
				Object[] firstMeasurementNode =  measurementsList.remove(counter);
				// Getting the measurementSeq - first value in each returned row
				long measurementSeq = (long)firstMeasurementNode[0];
				// Create list which represent single measurement - all hops with the same sequenceNum
				List<Object[]> singleMeasurement = new ArrayList<Object[]>(); 
				singleMeasurement.add(firstMeasurementNode);
				// Adding the relevant hops / rows to the list
				while(measurementSeq == (long)measurementsList.get(++counter)[0]) {
					singleMeasurement.add(measurementsList.remove(counter));
				}
				
				// Create topology from single measurement
				createTopology(singleMeasurement);
				// Updating counter for next single measurement
				counter++;
				
			}
			
			logger.info("Finished iteration. index : {} -> {}", iterationMinValue, iterationMaxValue);
			
			// Updating the iteration counters
			iterationMinValue = iterationMaxValue;	
			iterationMaxValue = weeklyMaxMeasurementSequence - iterationMaxValue < MEASUREMENT_SEQUENCE_CHUNK_SIZE ? weeklyMaxMeasurementSequence : iterationMaxValue + MEASUREMENT_SEQUENCE_CHUNK_SIZE;
			
		}
		
		//**************************************************************************************************************
		// After finishing creating the topology - persist it into DB
		
		persistTopology(edgesMap);
	 		
		logger.info("Finished creating IP Node Topology. Year : {} , Week : {}", year, week);
		
	}

	/*
	 * This method creates the nodes - known and unknown - and add them to the topology multimap which represents the edges of the ip topology.
	 * It also responsible for saving the new unknown nodes (not the weekly data) in the base unknown_nodes table. 
	 */
	private void createTopology(List<Object[]> singleMeasurement) {
		
		Iterator<Object[]> measurementIterator = singleMeasurement.iterator();
		
		// First node in the chain -will be treated differently in case it is unknown (no former known nodes)
		AbstractTopologyEntity edgeSourceEntity = createTopologyEntity(measurementIterator.next());
		
		while (measurementIterator.hasNext()) {
			
			AbstractTopologyEntity edgeTargetEntity = createTopologyEntity(measurementIterator.next());
			
			
			edgeSo
		}
		
	}

	// Converting raw row data into relevant node entity 
	private AbstractTopologyEntity createTopologyEntity(Object[] nodeData) {
		
		String hopeAddressStr = (String) nodeData[2];
		String hopeNameStr = (String) nodeData[3];
		long hasAlternative = (Long) nodeData[4];
		LocalDateTime startTime = new LocalDateTime(((Timestamp) nodeData[5]).getTime());
		
		// Case of known node
		if(InetAddresses.isInetAddress(hopeAddressStr)) {
			
			IPNodeEntity knownEntity = new IPNodeEntity();
			
			knownEntity.setDateOfDiscovery(startTime);
			knownEntity.setDateOfValidation(startTime);
			knownEntity.setName(hopeNameStr);
			
			// Getting ip as signed int - java data type
			int ip = InetAddresses.coerceToInteger(InetAddresses.forString(hopeAddressStr));
			// Converting to long to support the unsigned int in DB - in case of negative value (due to java signed int) adding byte shifting
			knownEntity.setIp(ip > 0 ? ip : ip + 0xFFFFFFFFL);
			
			
		}
		// Case of unknown node:
		else {
			
		}
		
		
	}

	/*
	 * This methods persist the topology into the different DB tables
	 */
	private void persistTopology(
			SetMultimap<AbstractTopologyEntity, AbstractTopologyEntity> edgesMap) {
		// TODO Auto-generated method stub
		
	}


}
