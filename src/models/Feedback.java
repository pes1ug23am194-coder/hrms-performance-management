package models;

public class Feedback {
    private String id;
    private String empId;
    private String from;
    private String type;
    private String category;
    private String text;
    private String date;

    public Feedback() {}

    public Feedback(String id, String empId, String from, String type, String category, String text, String date) {
        this.id = id;
        this.empId = empId;
        this.from = from;
        this.type = type;
        this.category = category;
        this.text = text;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Feedback{id='" + id + "', to='" + empId + "', from='" + from + "', type='" + type + "'}";
    }
}
