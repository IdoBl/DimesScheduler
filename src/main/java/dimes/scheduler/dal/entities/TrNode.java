package dimes.scheduler.dal.entities;

/**
 * Represents a raw_res_tr node
 * Created by Ido Blutman on 11/05/2015.
 */
public class TrNode {

    private long measurementSeq;
    private int hopSeq;
    private long ip;

    public int getHopSeq() { return hopSeq; }

    public void setHopSeq(int hopSeq) { this.hopSeq = hopSeq; }

    public long getMeasurementSeq() {
        return measurementSeq;
    }

    public void setMeasurementSeq(long measurementSeq) {
        this.measurementSeq = measurementSeq;
    }

    public long getIp() {
        return ip;
    }

    public void setIp(long ip) { this.ip = ip; }

}
