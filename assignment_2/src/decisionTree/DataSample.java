package decisionTree;

public class DataSample {
    private String[] attributeValues;
    private String label;

    public DataSample(String[] attributeValues, String label) {
        this.attributeValues = attributeValues;
        this.label = label;
    }

    public String[] getAttributeValues() {
        return attributeValues;
    }

    public String getLabel() {
        return label;
    }
}