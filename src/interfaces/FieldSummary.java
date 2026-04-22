package interfaces;

public class FieldSummary {
    public int     fieldId;
    public String  fieldName;
    public String  fieldType;
    public boolean isMandatory;

    public FieldSummary(int fieldId, String fieldName, String fieldType, boolean isMandatory) {
        this.fieldId     = fieldId;
        this.fieldName   = fieldName;
        this.fieldType   = fieldType;
        this.isMandatory = isMandatory;
    }
}
