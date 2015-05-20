package dimes.scheduler.dal;

import dimes.scheduler.dal.entities.TrNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Ido Blutman on 17/02/2015.
 */
@Repository("rawResTrDAO")
class RawResTrDAOImpl extends JdbcDaoSupport implements RawResTrDAO {

    // Constants for use in the queries
    private static final String TBL_NAME_PREFIX = "raw_res_traceroute_";
    private static final String SCHEMA_NAME_PREFIX = "dimes_results_";

    private static class TrNodeRowMapper implements RowMapper<TrNode> {

        @Override
        public TrNode mapRow(ResultSet resultSet, int i) throws SQLException {

            TrNode node = new TrNode();
            node.setMeasurementSeq( resultSet.getLong("MainSequenceNum") );
            node.setHopSeq( resultSet.getInt("sequence") );
            node.setIp(resultSet.getLong(3));
            return node;
        }

    }

    @Autowired
    public RawResTrDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public long getMinMeasurementSeq(int year, int week) {

        String query =
                "SELECT MIN(MainSequenceNum) FROM " + SCHEMA_NAME_PREFIX +
                        year + "." + TBL_NAME_PREFIX + year + "_" + week;

        return getJdbcTemplate().queryForObject(query , BigInteger.class).longValue();

    }

    @Override
    public long getMaxMeasurementSeq(int year, int week) {

        String query =
                "SELECT MAX(MainSequenceNum) FROM " + SCHEMA_NAME_PREFIX +
                        year + "." + TBL_NAME_PREFIX + year + "_" + week;

        return getJdbcTemplate().queryForObject(query , BigInteger.class).longValue();

    }

    @Override
    public List<TrNode> getMeasurementsBySeq(int year, int week, long minMeasurementSeq, long maxMeasurementSeq) {

        String query =
                "SELECT MainSequenceNum, sequence, INET_ATON(hopAddressStr) FROM " +
                        SCHEMA_NAME_PREFIX + year + "." + TBL_NAME_PREFIX + year + "_" + week +
                        " WHERE MainSequenceNum BETWEEN " + minMeasurementSeq + " AND " + maxMeasurementSeq;

        return getJdbcTemplate().query(query, new TrNodeRowMapper());

    }


}
