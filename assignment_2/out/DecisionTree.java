package decisionTree;

import java.util.*;

public class DecisionTree {
    private Attribute[] attributes;
    private List<DataSample> dataSamples;

    public DecisionTree(Attribute[] attributes, List<DataSample> dataSamples) {
        this.attributes = attributes;
        this.dataSamples = dataSamples;
    }

    public Node buildTree() {
        List<Integer> remainingAttributes = new ArrayList<>();
        for (int i = 0; i < attributes.length; i++) {
            remainingAttributes.add(i);
        }
        return buildTree(dataSamples, remainingAttributes);
    }

    private Node buildTree(List<DataSample> samples, List<Integer> remainingAttributes) {
        if (samples.isEmpty()) {
            return null;
        }

        String majorityLabel = findMajorityLabel(samples);
        if (remainingAttributes.isEmpty() || allSamplesHaveSameLabel(samples)) {
            return new Node(majorityLabel,false);
        }

        int bestAttribute = findBestAttribute(samples, remainingAttributes);
        Node node = new Node(attributes[bestAttribute].getName(),true);
        remainingAttributes.remove(Integer.valueOf(bestAttribute));

        for (String value : attributes[bestAttribute].getValues()) {
            List<DataSample> subset = filterSamplesByAttributeValue(samples, bestAttribute, value);
            Node child = buildTree(subset, new ArrayList<>(remainingAttributes));
            if (child == null) {
                child = new Node(majorityLabel,false);
            }
            node.addChild(value, child);
        }

        return node;
    }

    private String findMajorityLabel(List<DataSample> samples) {
        Map<String, Integer> labelCounts = new HashMap<>();
        for (DataSample sample : samples) {
            labelCounts.put(sample.getLabel(), labelCounts.getOrDefault(sample.getLabel(), 0) + 1);
        }
        return Collections.max(labelCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private boolean allSamplesHaveSameLabel(List<DataSample> samples) {
        String firstLabel = samples.get(0).getLabel();
        for (DataSample sample : samples) {
            if (!sample.getLabel().equals(firstLabel)) {
                return false;
            }
        }
        return true;
    }

    private int findBestAttribute(List<DataSample> samples, List<Integer> remainingAttributes) {
        double bestGain = Double.NEGATIVE_INFINITY;
        int bestAttribute = -1;
        for (int attributeIndex : remainingAttributes) {
            double gain = calculateGain(samples, attributeIndex);
            if (gain > bestGain) {
                bestGain = gain;
                bestAttribute = attributeIndex;
            }
        }
        return bestAttribute;
    }

    private double calculateGain(List<DataSample> samples, int attributeIndex) {
        double originalEntropy = calculateEntropy(samples);
        double newEntropy = 0.0;

        for (String value : attributes[attributeIndex].getValues()) {
            List<DataSample> subset = filterSamplesByAttributeValue(samples, attributeIndex, value);
            double subsetEntropy = calculateEntropy(subset);
            newEntropy += (double) subset.size() / samples.size() * subsetEntropy;
        }

        return originalEntropy - newEntropy;
    }

    private double calculateEntropy(List<DataSample> samples) {
        if (samples.isEmpty()) {
            return 0;
        }
        HashMap<String, Integer> lableCounts = new HashMap<>();
        for (DataSample sample : samples) {
            lableCounts.put(sample.getLabel(), lableCounts.getOrDefault(sample.getLabel(), 0) + 1);
        }

        double entropy = 0.0;
        for (int count : lableCounts.values()) {
            double probability = (double) count / samples.size();
            entropy -= probability * Math.log(probability) / Math.log(2);
        }

        return entropy;
    }

    private List<DataSample> filterSamplesByAttributeValue(List<DataSample> samples, int attributeIndex, String value) {
        List<DataSample> filteredSamples = new ArrayList<>();
        for (DataSample sample : samples) {
            if (sample.getAttributeValues()[attributeIndex].equals(value)) {
                filteredSamples.add(sample);
            }
        }
        return filteredSamples;
    }
}