package Autofill;

public class AcroFormField extends FormObject { //implements Comparable<AcroFormField>{

    private String fieldLabel;
    private String fieldName;
    private String fieldValue;
    private String group;

    private String personalFieldName;
    
    public AcroFormField() {
        personalFieldName = "";
    }
    
    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getGroup() {
        return this.group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getPersonalFieldName() {
        return personalFieldName;
    }
    
    public void setPersonalFieldName(String personalFieldName) {
        this.personalFieldName = personalFieldName;
    }

}
