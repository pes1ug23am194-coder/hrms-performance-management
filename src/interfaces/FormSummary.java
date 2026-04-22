package interfaces;

public class FormSummary {
    public int    formId;
    public String formName;
    public String layoutType;

    public FormSummary(int formId, String formName, String layoutType) {
        this.formId     = formId;
        this.formName   = formName;
        this.layoutType = layoutType;
    }
}
