package dimes.scheduler.dal;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 17/05/2015.
 */
public interface ASEdgesDAO {

    public void createWeeklyTable(int year, int week);
    public int insertASLinks(int year, int week, Map<Integer , List<Integer>> linksMap);
}
