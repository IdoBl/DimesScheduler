package dimes.scheduler.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * Created by user on 15/05/2015.
 */
@Repository("asPrefixTblDAO")
class ASPrefixTblDAOImpl extends JdbcDaoSupport implements ASPrefixTblDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ASEdgesDAOImpl.class);
    private static final String SCHEMA_NAME = "AS_RESOLUTION";
    private static final String TBL_NAME_PREFIX = "ASPrefixTbl_RV_";

    @Autowired
    public ASPrefixTblDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public int getASNumByIP(int year, int week, long ip) {

        String query =
                "SELECT asNumber FROM " + SCHEMA_NAME + "." + TBL_NAME_PREFIX +
                year + "_" + week + " WHERE LPAD(BIN(" + ip +
                        "),32,0) LIKE CONCAT(LPAD(BIN(asIntegerPrefix),asprefixlength,0),'%') ORDER BY asintegerprefix<<32-asprefixlength DESC LIMIT 1";

       // LOGGER.debug(query);
        return
                this.getJdbcTemplate().queryForObject(query , Integer.class);
    }
}
