package dimes.scheduler.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by user on 17/05/2015.
 */
@Repository("asEdgesDAO")
class ASEdgesDAOImpl extends JdbcDaoSupport implements ASEdgesDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ASEdgesDAOImpl.class);
    private static final String SCHEMA_NAME = "dimes_playground";
    private static final String TBL_NAME_PREFIX = "as_edges_";

    @Autowired
    public ASEdgesDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public void createWeeklyTable(int year, int week) {

        String query =
                "CREATE TABLE " + SCHEMA_NAME + "." + TBL_NAME_PREFIX +
                year + "_" + week + " (asn_source int unsigned NOT NULL COMMENT 'The link source AS number'," +
                "asn_dest int unsigned NOT NULL COMMENT 'The link dest AS number'," +
                "PRIMARY KEY (asn_source)) ENGINE=InnoDB DEFAULT CHARSET=latin1";

//        LOGGER.debug(query);
        this.getJdbcTemplate().execute(query);

    }

    @Override
    public int insertASLinks(int year, int week, Map<Integer , List<Integer>> linksMap) {

        Set<Integer> linksSourceSet = linksMap.keySet();

        String dataStr = "";

        for (Integer asSource : linksSourceSet) {
            ListIterator<Integer> asDestIter = linksMap.get(asSource).listIterator();
            while (asDestIter.hasNext())
                dataStr += "(" + asSource + "," + asDestIter.next() + "),";
        }

        String query =
                "INSERT INTO " + SCHEMA_NAME + "." + TBL_NAME_PREFIX + year + "_" + week + " (asn_source,asn_dest) VALUES " +
                 dataStr.substring(0, dataStr.length() - 1);

//        LOGGER.debug(query);
        return getJdbcTemplate().update(query);

    }
}
