package dimes.scheduler.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 *
 *
 * Created by Ido Blutman on 16/02/2015.
 */
@Repository("rawResMainDAO")
class RawResMainDAOImpl extends JdbcDaoSupport implements RawResMainDAO {

    private final Logger logger = LoggerFactory.getLogger(RawResMainDAOImpl.class);

    @Autowired
    public RawResMainDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public List<Integer> getRogueAgentList(int year, int week) {
        String schemaName = "dimes_results_"+year;
        String mainTblName = "raw_res_main_"+year+"_"+week;
        String trTblName = "raw_res_traceroute_"+year+"_"+week;
        String query = "SELECT DISTINCT AgentDef_AgentIndex FROM " + schemaName + "." + mainTblName +
                " WHERE SequenceNum IN (SELECT MainSequenceNum FROM " + schemaName + "." + trTblName +" WHERE bestTime < 0)";

        logger.debug(query);

        return
                this.getJdbcTemplate().queryForList(query, Integer.TYPE);

    }
}
