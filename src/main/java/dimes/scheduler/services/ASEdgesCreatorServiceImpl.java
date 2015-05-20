package dimes.scheduler.services;

import dimes.scheduler.dal.ASEdgesDAO;
import dimes.scheduler.dal.ASPrefixTblDAO;
import dimes.scheduler.dal.RawResTrDAO;
import dimes.scheduler.dal.entities.TrNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Class for creating AS maps
 *
 * Created by user on 12/05/2015.
 */

@Service("asEdgesCreatorService")
class ASEdgesCreatorServiceImpl implements EntityCreatorService {

    // Chunk size of processed measurement sequence:
    private static final long MEASUREMENT_SEQUENCE_CHUNK_SIZE = 1000000L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ASEdgesCreatorServiceImpl.class);

    @Autowired
    private RawResTrDAO rawResTrDAO;
    @Autowired
    private ASPrefixTblDAO asPrefixTblDAO;
    @Autowired
    private ASEdgesDAO asEdgesDAO;

    /**
     * Method for creating weekly entity
     *
     * @param year year of the entity
     * @param week week-of-year of the entity
     */
    @Override
    public void createWeeklyEntity(int year, int week) {

        // Links map - each key (source AS) has list of dest AS numbers.
        Map<Integer , List<Integer>> linksMap = new HashMap<Integer, List<Integer>>();

        // The min and max indexes of the weekly table
        long minSequenceNum = rawResTrDAO.getMinMeasurementSeq( year , week );
        long maxSequenceNum = rawResTrDAO.getMaxMeasurementSeq( year , week );

        LOGGER.info("Creating AS Edges map. Year -> {} week -> {} minSequenceNum -> {} maxSequenceNum -> {}", year, week, minSequenceNum, maxSequenceNum);

        // First, create the destination table
//        asEdgesDAO.createWeeklyTable(year, week);

        // init iteration variables
        long iterationMinValue = minSequenceNum;
        long iterationMaxValue = maxSequenceNum - minSequenceNum < MEASUREMENT_SEQUENCE_CHUNK_SIZE ? maxSequenceNum : minSequenceNum + MEASUREMENT_SEQUENCE_CHUNK_SIZE;

        // Iterating over the measurements and finding the as edges. In first phase, only the ASes correlation is being saved (for Scott's use)
        while ( iterationMinValue < maxSequenceNum ) {

            LOGGER.debug("iterationMinValue -> {} , iterationMaxValue -> {}", iterationMinValue, iterationMaxValue);

            List<TrNode> nodesList = rawResTrDAO.getMeasurementsBySeq( year , week , iterationMinValue , iterationMaxValue );

            LOGGER.debug("Number of rows : {}", nodesList.size());

            while ( nodesList.size() >= 2 ) {

                TrNode firstNode = nodesList.remove(0);
                List<TrNode> singleMeasurement = new ArrayList<TrNode>();
                singleMeasurement.add(firstNode);

                while ( nodesList.size() > 0 && firstNode.getMeasurementSeq() == nodesList.get(0).getMeasurementSeq())
                    singleMeasurement.add(nodesList.remove(0));

                LOGGER.info("singleMeasurement size -> {}", singleMeasurement.size());
                createTopology( singleMeasurement, linksMap );
                LOGGER.info("linksMap size -> ", linksMap.size());
            } // End of 2nd while

            // Updating the iteration counters
            iterationMinValue = iterationMaxValue;
            iterationMaxValue = maxSequenceNum - iterationMaxValue < MEASUREMENT_SEQUENCE_CHUNK_SIZE ? maxSequenceNum : iterationMaxValue + MEASUREMENT_SEQUENCE_CHUNK_SIZE;

        } // End of iteration while

        // persist The data
        asEdgesDAO.insertASLinks(year, week, linksMap);

    }

    private void createTopology(List<TrNode> singleMeasurement, Map<Integer, List<Integer>> linksMap) {

        Iterator<TrNode> iterator = singleMeasurement.iterator();
        TrNode firstNode = iterator.next();
        TrNode secondNode = null;

        while ( iterator.hasNext() ) {

            secondNode = iterator.next();

            int firstNodeAsNum = asPrefixTblDAO.getASNumByIP(2012, 18, firstNode.getIp());
            int secondNodeAsNum = asPrefixTblDAO.getASNumByIP(2012, 18, secondNode.getIp());

            if ( firstNodeAsNum != secondNodeAsNum ) {

                if ( linksMap.containsKey(firstNodeAsNum) ) {
                    List<Integer> linksList = linksMap.get(firstNodeAsNum);
                    if ( !linksList.contains(secondNodeAsNum) )
                        linksList.add(secondNodeAsNum);
                }
                else {
                    List<Integer> linksList = new ArrayList<Integer>();
                    linksList.add(secondNodeAsNum);
                    linksMap.put(firstNodeAsNum , linksList);
                }
            }

            firstNode = secondNode;

        }

    }

}
