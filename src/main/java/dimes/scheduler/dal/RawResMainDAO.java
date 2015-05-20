package dimes.scheduler.dal;

import java.util.List;

/**
 * Base DAO interface for actions on the raw_res_main tables
 *
 * Created by Ido Blutman on 16/02/2015.
 */
public interface RawResMainDAO {

    List<Integer> getRogueAgentList(int year, int week);

}
