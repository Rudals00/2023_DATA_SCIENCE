package apriori;

import java.io.*;
import java.util.*;

public class Apriori {

    public static Set<Set<String>> getFrequentItemsets(List<Set<String>> transactions, double minSupport) {

        Map<String, Integer> itemCounts = new HashMap<>();
        for (Set<String> transaction : transactions) {
            for (String item : transaction) {
                itemCounts.put(item, itemCounts.getOrDefault(item, 0) + 1);
            }
        }

        Set<Set<String>> frequentItemsets = new LinkedHashSet<>();

        Set<Set<String>> candidateItemsets = new HashSet<>();
        for (String item : itemCounts.keySet()) {
            Set<String> itemset = new HashSet<>(Arrays.asList(item));
            candidateItemsets.add(itemset);
        }

        int k = 1;
        while (!candidateItemsets.isEmpty()) {
            Map<Set<String>, Integer> candidateCounts = new HashMap<>();
            for (Set<String> candidate : candidateItemsets) {
                for (Set<String> transaction : transactions) {
                    if (transaction.containsAll(candidate)) {
                        candidateCounts.put(candidate, candidateCounts.getOrDefault(candidate, 0) + 1);
                    }
                }
            }

            for (Set<String> candidate : candidateCounts.keySet()) {
                double support = (double) candidateCounts.get(candidate) / transactions.size();
                if (support >= minSupport) {
                    frequentItemsets.add(candidate);
                }
            }

            Set<Set<String>> nextCandidateItemsets = new HashSet<>();
            for (Set<String> itemset1 : frequentItemsets) {
                for (Set<String> itemset2 : frequentItemsets) {
                    if (itemset1 != itemset2) {
                        Set<String> candidate = new HashSet<>(itemset1);
                        candidate.addAll(itemset2);
                        if (candidate.size() == k + 1) {
                            nextCandidateItemsets.add(candidate);
                        }
                    }
                }
            }

            candidateItemsets = nextCandidateItemsets;
            k++;
        }
        return frequentItemsets;
    }

    public static double calculateSupport(Set<String> itemset, List<Set<String>> transactions) {
        double support = 0.0;

        for (Set<String> transaction : transactions) {
            if (transaction.containsAll(itemset)) {
                support += 1.0;
            }
        }

        return support / transactions.size();
    }

    public static double calculateConfidence(Set<String> Antecedent, Set<String> Consequent, List<Set<String>> transactions) {
        double jointSupport = 0.0;
        double antecedentSupport = 0.0;
        double confidence = 0.0;

        for (Set<String> transaction : transactions) {
            if (transaction.containsAll(Antecedent) && transaction.containsAll(Consequent)) {
                jointSupport += 1.0;
            }
        }

        for (Set<String> transaction : transactions) {
            if (transaction.containsAll(Antecedent)) {
                antecedentSupport += 1.0;
            }
        }

        if (antecedentSupport > 0) {
            confidence = jointSupport / antecedentSupport;
        }
        return confidence;
    }

    public static Set<Set<String>> getSubsets(Set<String> set) {
        List<String> itemList = new ArrayList<>(set);
        Set<Set<String>> subsets = new HashSet<>();
        getSubsetsHelper(itemList, subsets, new HashSet<>(), 0);
        return subsets;
    }

    public static void getSubsetsHelper(List<String> itemList, Set<Set<String>> subsets, Set<String> currentSubset, int index) {
        if (index == itemList.size()) {
            if (!currentSubset.isEmpty()) {
                subsets.add(new HashSet<>(currentSubset));
            }
            return;
        }

        String item = itemList.get(index);

        getSubsetsHelper(itemList, subsets, currentSubset, index + 1);

        currentSubset.add(item);
        getSubsetsHelper(itemList, subsets, currentSubset, index + 1);
        currentSubset.remove(item);
    }

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("command error");
            System.exit(1);
        }

        int minSupInt = Integer.parseInt(args[0]);
        double minSup = Math.round(minSupInt * 100.0 / 100.0) / 100.0;
        String inputFile = args[1];
        String outputFile = args[2];

        List<Set<String>> transactions = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] items = line.trim().split("\t");
                Set<String> transaction = new HashSet<>();
                for (String item : items) {
                    transaction.add(item);
                }
                transactions.add(transaction);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Failed to read transactions from file: " + e.getMessage());
        }
        Set<Set<String>> frequentItemsets = getFrequentItemsets(transactions, minSup);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            boolean first = true;
            for (Set<String> itemset : frequentItemsets) {
                if (itemset.size() > 1) {
                    Set<Set<String>> consequentSubsets = getSubsets(itemset);
                    for (Set<String> consequent : consequentSubsets) {
                        if (consequent.size() < itemset.size()) {
                            if (!first) writer.write("\n");
                            else first = false;
                            Set<String> antecedent = new HashSet<>(itemset);
                            antecedent.removeAll(consequent);
                            double support = calculateSupport(itemset, transactions);
                            double confidence = calculateConfidence(antecedent, consequent, transactions);
                            writer.write("{" + String.join(", ", antecedent) + "}\t{" + String.join(", ", consequent) + "}\t" + String.format("%.2f", support * 100) + "\t" + String.format("%.2f", confidence * 100));
                        }
                    }
                }
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to write output: " + e.getMessage());
        }
    }
}