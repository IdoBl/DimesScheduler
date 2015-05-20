package dimes.scheduler.dal;

/**
 * DAO intefrace for ASPrefixTbl
 *
 * Created by Ido Blutman on 15/05/2015.
 */
public interface ASPrefixTblDAO {

    /**
     * Returning longest prefix match AS number for ip
     * @param ip The ip we check
     * @return Longest prefix match AS num
     */
    int getASNumByIP(int year, int week, long ip);
}
