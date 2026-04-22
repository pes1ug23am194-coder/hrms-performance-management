package models;

public class Appraisal {
    private String id;
    private String empId;
    private String cycle;
    private Integer mgr;
    private Integer self;
    private Integer peer;
    private Integer finalScore;
    private String status;
    private String date;

    public Appraisal() {}

    public Appraisal(String id, String empId, String cycle, Integer mgr, Integer self, Integer peer, Integer finalScore, String status, String date) {
        this.id = id;
        this.empId = empId;
        this.cycle = cycle;
        this.mgr = mgr;
        this.self = self;
        this.peer = peer;
        this.finalScore = finalScore;
        this.status = status;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public Integer getMgr() { return mgr; }
    public void setMgr(Integer mgr) { this.mgr = mgr; }
    public Integer getSelf() { return self; }
    public void setSelf(Integer self) { this.self = self; }
    public Integer getPeer() { return peer; }
    public void setPeer(Integer peer) { this.peer = peer; }
    public Integer getFinalScore() { return finalScore; }
    public void setFinalScore(Integer finalScore) { this.finalScore = finalScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Appraisal{id='" + id + "', empId='" + empId + "', final=" + finalScore + ", status='" + status + "'}";
    }
}
