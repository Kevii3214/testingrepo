import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StopwatchCPU;

import java.util.Arrays;
import java.util.LinkedList;

public class BoostingAlgorithm {
    private Clustering dimensionReducer; // initializes Clustering object
    // initializes LinkedList for storing Weak Learners
    private LinkedList<WeakLearner> experts;
    private double[] weights; // array of weights for each point
    private int[][] reducedInput; // initializes 2D array reducing input dimension
    private int[] labels; // initializes global labels to use later
    private int numLocations; // for predict() corner case

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels, Point2D[] locations, int k) {
        // corner cases
        if (input == null || labels == null || locations == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (input.length == 0) {
            throw new IllegalArgumentException("Input cannot be 0 length");
        }
        if (k < 1 || k > locations.length) {
            throw new IllegalArgumentException("k cannot be less than 1 or more than "
                                                       + "locations length");
        }
        if (input[0].length != locations.length || input.length != labels.length) {
            throw new IllegalArgumentException("Input, weights, and labels "
                                                       + "must be compatible length");
        }
        for (int i = 0; i < input.length; i++) {
            if (labels[i] != 0 && labels[i] != 1) {
                throw new IllegalArgumentException("Values of labels must be 0 or 1");
            }
        }
        dimensionReducer = new Clustering(locations, k); // create clustering object
        // reduce input from n x m to n x k
        reducedInput = new int[input.length][k];
        for (int i = 0; i < input.length; i++) {
            reducedInput[i] = dimensionReducer.reduceDimensions(input[i]);
        }
        this.labels = Arrays.copyOf(labels, labels.length);
        weights = new double[input.length];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1.0 / input.length;
        }
        experts = new LinkedList<>(); // to store weak learners in iterate()
        numLocations = locations.length;
    }

    // return the current weight of the ith point
    public double weightOf(int i) {
        return weights[i];
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        WeakLearner currentWeakLearner = new
                WeakLearner(reducedInput, weights, labels);
        experts.add(currentWeakLearner);
        for (int i = 0; i < weights.length; i++) {
            if (currentWeakLearner.predict(reducedInput[i]) != labels[i]) {
                weights[i] *= 2;
            }
        }
        // renormalize array
        double sumofWeights = 0.0;
        for (double i : weights) {
            sumofWeights += i;
        }
        for (int i = 0; i < weights.length; i++) {
            weights[i] /= sumofWeights;
        }
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        // corner cases
        if (sample == null) {
            throw new IllegalArgumentException("Sample cannot be null");
        }
        if (sample.length != numLocations) {
            throw new IllegalArgumentException("Sample must be same length as input");
        }
        int[] reducedSample = dimensionReducer.reduceDimensions(sample);
        int numZeroes = 0;
        int numOnes = 0;
        for (WeakLearner w : experts) {
            if (w.predict(reducedSample) == 0) {
                numZeroes++;
            }
            else {
                numOnes++;
            }
        }
        if (numZeroes >= numOnes) {
            return 0;
        }
        else {
            return 1;
        }
    }

    // unit testing
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet testing = new DataSet(args[1]);
        int numClusters = Integer.parseInt(args[2]);
        int iterations = Integer.parseInt(args[3]);
        StopwatchCPU timer = new StopwatchCPU(); // start timing
        int[][] trainingInput = training.getInput();
        int[][] testingInput = testing.getInput();
        int[] trainingLabels = training.getLabels();
        int[] testingLabels = testing.getLabels();
        Point2D[] trainingLocations = training.getLocations();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(
                trainingInput, trainingLabels, trainingLocations, numClusters
        );
        for (int t = 0; t < iterations; t++)
            model.iterate();

        // calculate the training data set accuracy
        double trainingAccuracy = 0;
        for (int i = 0; i < training.getN(); i++)
            if (model.predict(trainingInput[i]) == trainingLabels[i])
                trainingAccuracy += 1;
        trainingAccuracy /= training.getN();

        // calculate the test data set accuracy
        double testingAccuracy = 0;
        for (int i = 0; i < testing.getN(); i++)
            if (model.predict(testingInput[i]) == testingLabels[i])
                testingAccuracy += 1;
        testingAccuracy /= testing.getN();
        double time = timer.elapsedTime(); // stops time
        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model: " + testingAccuracy);
        StdOut.println("Run Time of mode: " + time);
    }
}
