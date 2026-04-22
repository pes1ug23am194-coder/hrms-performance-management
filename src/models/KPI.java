package models;

public class KPI {
    private String kpiId;
    private String name;
    private double target;
    private double current;
    private String unit;
    private String dept;

    public KPI() {}

    public KPI(String kpiId, String name, double target, double current, String unit, String dept) {
        this.kpiId = kpiId;
        this.name = name;
        this.target = target;
        this.current = current;
        this.unit = unit;
        this.dept = dept;
    }

    public String getKpiId() { return kpiId; }
    public void setKpiId(String kpiId) { this.kpiId = kpiId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getTarget() { return target; }
    public void setTarget(double target) { this.target = target; }
    public double getCurrent() { return current; }
    public void setCurrent(double current) { this.current = current; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getDept() { return dept; }
    public void setDept(String dept) { this.dept = dept; }

    @Override
    public String toString() {
        return "KPI{id='" + kpiId + "', name='" + name + "', current=" + current + "/" + target + " " + unit + "}";
    }
}
