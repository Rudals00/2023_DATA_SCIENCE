package decisionTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class dt {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Invalid input");
            System.exit(1);
        }
        String trainingFileName = args[0];
        String testFileName = args[1];
        String outputFileName = args[2];
//        String trainingFileName = "dt_train1.txt";
//        String testFileName = "dt_test1.txt";
//        String outputFileName = "dt_result1.txt";
        try {
            Pair<Attribute[], List<DataSample>> trainingData = readDataFromFile(trainingFileName, true);
            Attribute[] attributes = trainingData.getKey();
            List<DataSample> trainingSamples = trainingData.getValue();

            DecisionTree decisionTree = new DecisionTree(attributes, trainingSamples);
            Node root = decisionTree.buildTree();

            Pair<Attribute[], List<DataSample>> testData = readDataFromFile(testFileName, false);
            List<DataSample> testSamples = testData.getValue();

            List<String> predictions = new ArrayList<>();
            String labelName = trainingData.getKey()[trainingData.getKey().length - 1].getName();
            String attributeNames = String.join("\t", Arrays.stream(attributes).map(Attribute::getName).toArray(String[]::new));
            predictions.add(attributeNames + "\t" + labelName);

            for (DataSample sample : testSamples) {
                String prediction = classifySample(root, attributes, sample);
                predictions.add(String.join("\t", sample.getAttributeValues()) + "\t" + prediction);
            }

            writePredictionsToFile(predictions, outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Pair<Attribute[], List<DataSample>> readDataFromFile(String fileName, boolean withLable) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        String[] header = lines.get(0).split("\t");
        Attribute[] attributes = new Attribute[header.length - (withLable ? 1 : 0)];
        for (int i = 0; i < attributes.length; i++) {
            attributes[i] = new Attribute(header[i], new ArrayList<>());
        }

        List<DataSample> samples = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split("\t");
            if (withLable) {
                String[] attributeValues = Arrays.copyOfRange(values, 0, values.length - 1);
                samples.add(new DataSample(attributeValues, values[values.length - 1]));

                for (int j = 0; j < attributeValues.length; j++) {
                    if (!attributes[j].getValues().contains(attributeValues[j])) {
                        attributes[j].getValues().add(attributeValues[j]);
                    }
                }
            } else {
                samples.add(new DataSample(values, null));
            }
        }

        return new Pair<>(attributes, samples);


    }
    private static String classifySample(Node root, Attribute[] attributes, DataSample sample) {
        Node currentNode = root;
        while (currentNode.getClassification() == null) {
            String attributeName = currentNode.getAttributeName();
            String attributeValue = sample.getAttributeValues()[getAttributeIndexByName(attributes, attributeName)];
            currentNode = currentNode.getChildren().get(attributeValue);
        }
        return currentNode.getClassification();
    }

    private static int getAttributeIndexByName(Attribute[] attributes, String attributeName) {
        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i].getName().equals(attributeName)) {
                return i;
            }
        }
        return -1;
    }

    private static void writePredictionsToFile(List<String> predictions, String outputFileName) throws IOException {
        Files.write(Paths.get(outputFileName), predictions);
    }

}
