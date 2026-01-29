import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.Comparator;

public class WeakLearner {
    // best dimension predictor to use
    private int dpChampion;
    // best sign predictor to use
    private int spChampion;
    // best value predictor to use
    private int vpChampion;
    // maximized total weight to use
    private double weightChampion;
    // next 3 not necessary if remove helper method (no storage requirement)
    // to store argument input
    private int inputLength;

    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {
        // corner cases
        if (input == null || weights == null || labels == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (input.length != weights.length || input.length != labels.length) {
            throw new IllegalArgumentException("Input, weights, and labels must be"
                                                       + " compatible length");
        }
        if (input.length == 0) {
            throw new IllegalArgumentException("Input cannot be 0 length");
        }
        for (int i = 0; i < input.length; i++) {
            if (input[i].length == 0) {
                throw new IllegalArgumentException("No element of input can "
                                                           + "have 0 length");
            }
            if (labels[i] != 0 && labels[i] != 1) {
                throw new IllegalArgumentException("Values of labels must be 0 or 1");
            }
        }
        double currentWeight;
        for (int dp = 0; dp < input[0].length; dp++) { // k possible dp
            // sort through
            Inputs[] sortedByDP = new Inputs[input.length];
            for (int d = 0; d < input.length; d++) {
                // new useful objects that will synchronize all the array values
                // ensures alignment after sorting by dp values
                sortedByDP[d] = new Inputs(input[d][dp], weights[d], labels[d]);
            }
            // must sort with comparator looking at inputs only for ascending dp order
            Arrays.sort(sortedByDP, Comparator.comparingInt(e -> e.input));
            for (int sp = 0; sp < 2; sp++) { // 2 possible sp
                currentWeight = 0;
                int vp = 0;
                // changed to while loop instead of for to update vp dynamically
                // vp here refers to the index we extract from, not the actual vp
                // we will eventually extract the vp in this loop though
                while (vp < input.length) { // n possible vp
                    // basically sets it the vp line to 0 so we can just add/subtract
                    if (vp == 0) {
                        for (int i = 0; i < sortedByDP.length; i++) {
                            if (sortedByDP[i].getLabel() != sp) {
                                currentWeight += sortedByDP[i].getWeight();
                            }
                        }
                    }
                    // first part of if/else statement checks for dupes
                    if (vp != input.length - 1 && sortedByDP[vp].getInput() ==
                            sortedByDP[vp + 1].getInput()) {
                        Queue<Integer> duplicateIndexes = new Queue<>();
                        duplicateIndexes.enqueue(vp);
                        int numDuplicateIndexes = 1;
                        int currentIndex = vp + 1;
                        // finds all the duplicate indexes
                        while (currentIndex != input.length && sortedByDP[vp]
                                .getInput() == sortedByDP[currentIndex].getInput()) {
                            numDuplicateIndexes++;
                            duplicateIndexes.enqueue(currentIndex);
                            currentIndex++;
                        }
                        // handles all the duplicate indexes at same time
                        for (Integer i : duplicateIndexes) {
                            // updates currentWeight if new sweep line is right or not
                            if (sortedByDP[i].getLabel() == sp) {
                                currentWeight += sortedByDP[i].getWeight();
                            }
                            else {
                                currentWeight -= sortedByDP[i].getWeight();
                            }
                        }
                        vp += numDuplicateIndexes - 1; // skips the duplicates
                    }
                    // updates currentWeight if the new sweep line is correct or not
                    else {
                        if (sortedByDP[vp].getLabel() == sp) {
                            currentWeight += sortedByDP[vp].getWeight();
                        }
                        else {
                            currentWeight -= sortedByDP[vp].getWeight();
                        }
                    }
                    // update champion if new weight is better
                    if (currentWeight > weightChampion) {
                        weightChampion = currentWeight;
                        dpChampion = dp;
                        spChampion = sp;
                        // vp is value not index
                        vpChampion = sortedByDP[vp].getInput();
                    }
                    vp++;
                }
            }
            inputLength = input[0].length;
        }


    }

    // private class to synchronize sorting all the arrays by dp
    private class Inputs {
        private int input; // input at that dp
        private double weight; // weight at that dp
        private int label; // label at that dp

        // constructor for private class to synchronize the sorting
        public Inputs(int input, double weight, int label) {
            this.input = input;
            this.weight = weight;
            this.label = label;
        }

        // returns the input at that dp
        public int getInput() {
            return input;
        }

        // returns the weight at that dp
        public double getWeight() {
            return weight;
        }

        // returns the label at that dp
        public int getLabel() {
            return label;
        }
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        // corner case
        if (sample == null) {
            throw new IllegalArgumentException("Sample cannot be null");
        }
        if (sample.length != inputLength) {
            throw new IllegalArgumentException("Sample must be same length as input");
        }
        if (spChampion == 0) {
            if (sample[dpChampion] <= vpChampion) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            if (sample[dpChampion] <= vpChampion) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dpChampion;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return vpChampion;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return spChampion;
    }

    // unit testing
    public static void main(String[] args) {

    }
}
