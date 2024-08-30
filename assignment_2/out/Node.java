package decisionTree;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private String attributeName;
    private Map<String, Node> children;
    private String classification;

    public Node(String attributeName, boolean isAttribute) {
        if (isAttribute) {
            this.attributeName = attributeName;
            this.children = new HashMap<>();
        } else {
            this.classification = attributeName;
        }
    }

    public String getAttributeName() {
        return attributeName;
    }

    public Map<String, Node> getChildren() {
        return children;
    }

    public String getClassification() {
        return classification;
    }

    public void addChild(String value, Node child) {
        children.put(value, child);
    }
}