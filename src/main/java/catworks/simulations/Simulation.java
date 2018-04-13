package catworks.simulations;

// Project import statements.
import catworks.networks.*;
import catworks.networks.metrics.*;
import catworks.phenomena.*;

// Additional import statements.
import java.util.Arrays;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 *
 */
public abstract class Simulation {

    protected static final int COLUMNS = 15;
    protected static final String[] HEADER = { "Time Step", "Number of Nodes", "Number of Immune", "Infected - Betweenness (AVG)", "Infected - Betweenness (MIN)", "Infected - Betweenness (MAX)", "Infected - Closeness (AVG)", "Infected - Closeness (MIN)", "Infected - Closeness (MAX)", "Infected - Degree (AVG)", "Infected - Degree (MIN)", "Infected - Degree (MAX)", "Infected - Eigenvector (AVG)", "Infected - Eigenvector (MIN)", "Infected - Eigenvector (MAX)", };
    protected static final Centrality[] CENTRALITIES = { new BetweennessCentrality(), new ClosenessCentrality(), new DegreeCentrality(), new EigenvectorCentrality() };

    protected static final int TIMESTAMP_COL    = 0;
    protected static final int NODE_COUNT_COL   = 1;
    protected static final int IMMUNE_COUNT_COL = 2;

    protected static final int BETWEENNESS_AVG_COL = 3;
    protected static final int BETWEENNESS_MIN_COL = 4;
    protected static final int BETWEENNESS_MAX_COL = 5;

    protected static final int CLOSENESS_AVG_COL = 6;
    protected static final int CLOSENESS_MIN_COL = 7;
    protected static final int CLOSENESS_MAX_COL = 8;

    protected static final int DEGREE_AVG_COL = 9;
    protected static final int DEGREE_MIN_COL = 10;
    protected static final int DEGREE_MAX_COL = 11;

    protected static final int EIGENVECTOR_AVG_COL = 12;
    protected static final int EIGENVECTOR_MIN_COL = 13;
    protected static final int EIGENVECTOR_MAX_COL = 14;

    // Instance variables for simulations.
    // protected Network    network;
    protected Phenomena  phenomena;
    protected int        timeSteps;
    protected double     immuneFraction;
    protected double     infectFraction;
    protected int        immuneCount;
    protected static int simulationID = 0;
    protected int runID = 0;

    /**
     * [run description]
     * @param  n                        [description]
     * @return                          [description]
     * @throws Exception                [description]
     * @throws IOException              [description]
     * @throws IllegalArgumentException [description]
     */
    public abstract Object[][] run(int n) throws Exception, IOException, IllegalArgumentException;


    /**
     * [sumData description]
     * @param data  [description]
     * @param data2 [description]
     */
    protected void sumData(double[][] data, double[][] additive) {
        for (int i = 0; i < timeSteps; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                data[i][j] =  data[i][j] + additive[i][j];
            }
        }
    }

    /**
     * [log description]
     * @param string [description]
     */
    protected void log(String string) {
        System.out.println("[" + new java.util.Date() + "]$ " + string);
    }



}