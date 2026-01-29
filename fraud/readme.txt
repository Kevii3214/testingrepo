Programming Assignment 7: Fraud Detection

/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */
To implement the Clustering constructor, I first implemented the corner cases so
that no argument can be null and k is between 1 and the number of locations. I
also saved the length of the locations array to a global variable for a later
corner case of clusterOf(int i). After dealing with the corner cases, I created
an EdgeWeightedGraph object called map and filled it with the locations as
vertices and an edge as the distance to another location. I then inserted this
EdgeWeightedGraph into Kruskal's algorithm to create a Minimum Spanning Tree.
Since this MST has the ideal paths to each location, I put all the edges in the
MST into a Priority Queue, so I could extract the edges with the smallest weight
easier. I need to do this because I create another EdgeWeightedGraph and will
essentially create clusters by excluding the edges with the largest weights. I
use Priority Queue's delMin() method to add the smallest weights until I have
the desired amount of clusters (k). I then put this new EdgeWeightedGraph into
the CC object global instance variable for later use and also store the number
of clusters (k) from the argument.
/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */
To implement the WeakLearner constructor, I first checked for the corner cases:
no null arguments, compatible array lengths, no input can be 0 length, and values
of labels must be 0 or 1. Initially, I created the constructor with the brute force
algorithm in which that time complexity was Okn^2 because it has loops through
all dimension predictors (dp) (there are k of them) which loops through all sign
predictors (sp) (there are 2) which loops through all value predictors (vp) (there
are n) which has to loop through all the points and add the weights up. It will
then store the weight champion of all these possible iterations. After implementing
it this way, I changed to Oknlogn time by implementing a sweep line algorithm.
Essentially, I sort the input by the dp value in each iteration of the dp loop
(if dp is 0, we sorted by x value and 1 for y value). Since we want to sort all of
the arrays while keeping them aligned, I created a private class Inputs to store
the values in each array and keep them aligned. I created a new array storing this
new class and then sorted by the dp value in the class using Arrays.sort() and
a comparator that compares by the input value correlating to the dp value. Now that
the initial arrays are sorted by the dp value, we go through a nested for loop for
sp which has a nested while loop for vp (reason for a while loop instead of for loop
soon). For each sp iteration, we reset our currentWeight variable to 0 so we can
update it to the initial state (vp = 0) and see what the weight is there. After,
we can sweep through each vp value and just add/subtract the weight if the point
is incorrectly labeled at that vp value. We use a while loop for the vp to handle
points with duplicate vp values. We nest another while loop to find all of these
duplicate vp points and then go through a for loop to handle the added/subtracted
weight at this duplicate vp. Then, it will skip those indexes in our sorted array
since we handled more than 1 point in this iteration. At the end of each iteration,
we compare the currentWeight to the weightChampion to see if this vp has the best
weight. It will store the dp, sp, and vp as the champions of these values if this
is the case. At the end of the constructor, it will store the length of each array
in input[][] to store for later use in a corner case in predict(int[] sample).

/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the small_training.txt and small_test.txt datasets instead,
 *  otherwise this will take too long)
 **************************************************************************** */

      k          T         test accuracy       time (seconds)
   --------------------------------------------------------------------------
     10         10         0.925               0.359375
     20         20         0.925               0.796875
     30         30         0.955               1.140625
     40         40         0.96                1.1609375
     50         50         0.95                2.90625
     60         60         0.965               3.65625
     70         70         0.96                5.03125
     80         80         0.95875             5.84375
     90         90         0.9425              7.59375
     100        100        0.94875             9.59375
     110        110        0.93625             11.5



/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */
The optimal k and T is k = 60 and T = 344 which produces an accuracy of 0.98125.
1.) To find this optimal k and T, I chose the optimal k from the table since I
know from testing k is what causes the test accuracy to suddenly drop at high
inputs. Holding k at 60, I increased T to as much as I could while keeping it
under 10 seconds. I know increasing T will increase the test accuracy, so I
needed to maximize it.
2.) I know small values of T will lead to low test accuracy because it decreases
how many iterations we will be training the model with. Hence, the training
accuracy of the model will decrease, and it will likewise decrease the testing
accuracy. The more we train the model, the higher the test accuracy will be.
3.) A k too small will make fewer clusters and a k too high will make too many
clusters. If we have too little clusters, there will be little to no meaningful
clusters in which we can draw data from and find a meaningful pattern to predict
from. It would essentially be the same as if they were not connected at all.
If we have too many clusters, there will be too many points to draw patterns from,
and we would be essentially predicting from noise from irrelevant points instead
of patterns from aggregated points that have a pattern. Hence, it is best to find
the sweet spot of clusters so that we can draw patterns from clusters that have
some actual meaning rather than predicting from a giant cluster that means nothing
or a bunch of clusters that are basically each point that won't mean anything.

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
I liked this assignment, but it was very hard.
