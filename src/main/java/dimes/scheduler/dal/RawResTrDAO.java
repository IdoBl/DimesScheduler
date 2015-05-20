package dimes.scheduler.dal;

import dimes.scheduler.dal.entities.TrNode;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 17/02/2015.
 */
public interface RawResTrDAO {

    /**
     * Getting
     *
     * @param year
     * @param week
     * @return
     */
    /*
    Set<Long> getNegMeasSeq(int year, int week);
    */

    long getMinMeasurementSeq(int year, int week);

    long getMaxMeasurementSeq(int year, int week);

    List<TrNode> getMeasurementsBySeq(int year , int week , long minMeasurementSeq , long maxMeasurementSeq);
}
