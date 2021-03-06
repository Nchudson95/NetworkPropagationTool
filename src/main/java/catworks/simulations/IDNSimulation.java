package catworks.simulations;

// Project import statements.
import catworks.networks.*;
import catworks.networks.metrics.*;
import catworks.phenomena.*;

// Additional import statements.
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 *
 */
public class IDNSimulation extends Simulation {

    private boolean TARGETED = true; // If true, then targeted attacks. Otherwise, random attacks.
    private IDN     networks;
    private boolean separateCentralities;
    private double[][] minimumAndMaximum;
    private HashSet<Integer> immuneNodes;
    

    public IDNSimulation(IDN networks, Phenomena phenomena, int timeSteps, int immuneCount, int failedCount, boolean separateCentralities) {
        this.networks = networks;
        this.phenomena = phenomena;
        this.timeSteps = timeSteps;
        this.immuneCount = immuneCount;
        this.failedCount = failedCount;
        this.separateCentralities = separateCentralities;

        runID = 0;
        simulationID++;
    }

    /**
     * [run description]
     * @param  n           [description]
     * @return             [description]
     * @throws Exception   [description]
     * @throws IOException [description]
     */
    public Object[][] run(int n) throws Exception, IllegalArgumentException {
        if (n <= 0) {
            throw new IllegalArgumentException("`n` must be a positive number.");
        }

        // (1) Initialize `data` such that the rows are of type Integer,
        //     initialized to value 0.
        double[][] data = new double[timeSteps][COLUMNS];
        initMinimumAndMaximum();
        for (int i = 0; i < timeSteps; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                data[i][j] = new Integer(0);
            }
        }

        // (2) Go through the number of simulations and sum up the values between
        //     data sets.
        for (int simulationNum = 0; simulationNum < n; simulationNum++) {
            // Run a single instance of the simulation and add the results of that simulation to our `data` instance.
            sumData(data, run());

            // Check to make sure we're not on the last simulation (if so, there's no need to regenerate/rewire the networks).
            if (simulationNum != n-1) {
                if ((networks.getNetwork(0) instanceof ERNetwork) || (networks.getNetwork(0) instanceof SFNetwork) || (networks.getNetwork(0) instanceof SWNetwork)) {
                    log(networks.getToken() + "  Interdependent network - regenerated.");
                    networks.regenerate();
                } else {
                    log(networks.getToken() + "  Cyber network - rewired.");
                    networks.getNetwork(1).rewire(); // TODO: Complete this so that it works for an arbitrary number of networks.
                }
            }

            // Increment the run id.
            runID++;
        }

        // (3) Go through the final dataset and simply divide each Integer value
        //     by `n` to calculate the average.
        for (int i = 0; i < timeSteps; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                switch (j) {
                    case BETWEENNESS_MIN_COL: break;
                    case BETWEENNESS_MAX_COL: break;
                    case CLOSENESS_MIN_COL:   break;
                    case CLOSENESS_MAX_COL:   break;
                    case DEGREE_MIN_COL:      break;
                    case DEGREE_MAX_COL:      break;
                    case EIGENVECTOR_MIN_COL: break;
                    case EIGENVECTOR_MAX_COL: break;
                    case PATH_DEGREE_MIN_COL: break;
                    case PATH_DEGREE_MAX_COL: break;
                    case WBC_MIN_COL:         break;
                    case WBC_MAX_COL:         break;
                    default:
                        data[i][j] = data[i][j] / (double) n;
                        break;
                }
            }
        }

        // (4) Prepare the final dataset, `finalData`, which will include the header
        // row.
        Object[][] finalData = new Object[timeSteps+1][COLUMNS];
        for (int i = 0; i < COLUMNS;     i++) finalData[0][i] = HEADER[i];
        for (int i = 1; i < timeSteps+1; i++) {
            finalData[i][TIMESTAMP_COL]    = (Integer) ((int) data[i-1][TIMESTAMP_COL]);
            finalData[i][NODE_COUNT_COL]   = (Integer) ((int) data[i-1][NODE_COUNT_COL]);
            finalData[i][IMMUNE_COUNT_COL] = (Integer) ((int) data[i-1][IMMUNE_COUNT_COL]);

            finalData[i][BETWEENNESS_AVG_COL]  = (Double) data[i-1][BETWEENNESS_AVG_COL];
            finalData[i][BETWEENNESS_MIN_COL]  = (Double) minimumAndMaximum[i-1][BETWEENNESS_MIN_COL];
            finalData[i][BETWEENNESS_MAX_COL]  = (Double) minimumAndMaximum[i-1][BETWEENNESS_MAX_COL];

            finalData[i][CLOSENESS_AVG_COL] = (Double) data[i-1][CLOSENESS_AVG_COL];
            finalData[i][CLOSENESS_MIN_COL] = (Double) minimumAndMaximum[i-1][CLOSENESS_MIN_COL];
            finalData[i][CLOSENESS_MAX_COL] = (Double) minimumAndMaximum[i-1][CLOSENESS_MAX_COL];

            finalData[i][DEGREE_AVG_COL] = (Double) data[i-1][DEGREE_AVG_COL];
            finalData[i][DEGREE_MIN_COL] = (Double) minimumAndMaximum[i-1][DEGREE_MIN_COL];
            finalData[i][DEGREE_MAX_COL] = (Double) minimumAndMaximum[i-1][DEGREE_MAX_COL];

            finalData[i][EIGENVECTOR_AVG_COL] = (Double) data[i-1][EIGENVECTOR_AVG_COL];
            finalData[i][EIGENVECTOR_MIN_COL] = (Double) minimumAndMaximum[i-1][EIGENVECTOR_MIN_COL];
            finalData[i][EIGENVECTOR_MAX_COL] = (Double) minimumAndMaximum[i-1][EIGENVECTOR_MAX_COL];

            // Interdependent centrality data.
            finalData[i][PATH_DEGREE_AVG_COL] = (Double) data[i-1][PATH_DEGREE_AVG_COL];
            finalData[i][PATH_DEGREE_MIN_COL] = (Double) minimumAndMaximum[i-1][PATH_DEGREE_MIN_COL];
            finalData[i][PATH_DEGREE_MAX_COL] = (Double) minimumAndMaximum[i-1][PATH_DEGREE_MAX_COL];

            finalData[i][WBC_AVG_COL] = (Double) data[i-1][WBC_AVG_COL];
            finalData[i][WBC_MIN_COL] = (Double) minimumAndMaximum[i-1][WBC_MIN_COL];
            finalData[i][WBC_MAX_COL] = (Double) minimumAndMaximum[i-1][WBC_MAX_COL];
        }
        return finalData;
    }


    /**
     * [run description]
     * @return [description]
     * @throws Exception     [description]
     * @throws IOException   [description]
     */
    protected double[][] run() throws Exception {
        // Initialize the two-dimensional data set that will store the data that
        // will be used to make the .CSV output file.
        double[][] data = initializeData();
        Network bridgedNetwork = networks.bridge();
        final Integer[][] matrix = bridgedNetwork.getArrayMatrix();
        final Integer N = networks.getNumOfNodes();
        int avgIndex, minIndex, maxIndex;

        /**
         * TODO: Implement a helper method that will run each centrality and interdependent-centrality
         *       on the provided topology. This helper method should return data of type Set<Integer>,
         *       with each element being indices for nodes that all the centralities deemed to be "central".
         *       The point of this is to then guarantee that the infection methods do not select nodes belonging
         *       to this set. Let's denote this set to be `S` and `n` to be the number of nodes we wish to infect.
         *       Infection methods should aim to find the `S + n` most central nodes and then infect the first `n`
         *       nodes to not belong to set `S`.
         * 
         *       Further, move the portion of code that regenerates and rewires the network to the run(n) method.
         *       Place it in the loop that deals with running the number of simulations. Place it after the call
         *       to this method and then make a check such that if it's the last simulation, it doesn't bother
         *       regenerating/rewiring -- it's a trivially simple way to maximize efficiency.
         */

        immuneNodes = getInitialImmuneNodes(networks); 
        Network failureNet = networks.getNetwork(0);
        int[] controlledInitialFailure = new int[N];
        int fails = 0;
        if (TARGETED) {
            int index = 0;
            int[] mostCentral = failureNet.mostCentralNodes(new DegreeCentrality(), immuneNodes.size() + failedCount);
            while (fails < failedCount) {
                int node = mostCentral[index];
                if (!immuneNodes.contains(node)) {
                    controlledInitialFailure[node] = Phenomena.AFFLICTED;
                    fails++;
                }
                index++;
            }
        } else {
            Random rand = new Random();
            while (fails < failedCount) {
                int node = rand.nextInt(failureNet.getNumOfNodes());
                if (!immuneNodes.contains(node)) {
                    if (controlledInitialFailure[node] == Phenomena.UNAFFLICTED) {
                        controlledInitialFailure[node] = Phenomena.AFFLICTED;
                        fails++;
                    }
                }
            }
        }

        // PHASE 1: Propagate phenomena using the standard centrality metrics to immunize nodes to failure within an IDN
        //          under both cases -- i) separate local centralities and ii) bridged global centralities.
        for (Centrality metric : CENTRALITIES) {
            // Determine which columns (average, minimum, and maximum) to modify
            // with respect to centrality.
            switch (metric.type()) {
                case Centrality.BETWEENNESS:
                    avgIndex = BETWEENNESS_AVG_COL;
                    minIndex = BETWEENNESS_MIN_COL;
                    maxIndex = BETWEENNESS_MAX_COL;
                    break;
                case Centrality.CLOSENESS:
                    avgIndex = CLOSENESS_AVG_COL;
                    minIndex = CLOSENESS_MIN_COL;
                    maxIndex = CLOSENESS_MAX_COL;
                    break;
                case Centrality.DEGREE:
                    avgIndex = DEGREE_AVG_COL;
                    minIndex = DEGREE_MIN_COL;
                    maxIndex = DEGREE_MAX_COL;
                    break;
                case Centrality.EIGENVECTOR:
                    avgIndex = EIGENVECTOR_AVG_COL;
                    minIndex = EIGENVECTOR_MIN_COL;
                    maxIndex = EIGENVECTOR_MAX_COL;
                    break;
                default:
                    throw new Exception("Invalid Centrality metric type.");
            }

            // Log the start of the simulation to the user.
            log(networks.getToken() + "  Starting Simulation " + runID + " (" + metric + ")");
            int[] initialState = Arrays.copyOf(controlledInitialFailure, N);

            // ===== SUMMARY: 
            // To perform an interdependent network simulation, we must either:
            //   (1) Bridge the network AFTER picking the most central nodes from
            //       each network separately.
            //   (2) Bridge the network BEFORE picking the most central nodes.
            // Upon doing that, immunize and infect the appropriate nodes in the
            // network and set that as the initial state, or the state at time step 0.

            // OPTION 1: Immunize most central nodes of separate networks before bridging.
            if (separateCentralities) {
                int offset = 0;
                int backupImmuneCount = immuneCount;
                immuneCount /= networks.getNumOfNetworks();
                for (int i = 0; i < networks.getNumOfNetworks(); i++) {
                    Network temp = networks.getNetwork(i);
                    immunize(initialState, temp, metric, offset);
                    offset += temp.getNumOfNodes(); // Increment offset by number of nodes.
                }
                immuneCount = backupImmuneCount;
            }
            
            // OPTION 2: Immunize most central nodes of the bridged network. In this case, we
            //           simply immunize and infect once and hard-code `offset` to be 0.
            else immunize(initialState, bridgedNetwork, metric, 0);

            // Propagate phenomena throughout the network and store the state data at
            // the end of each time step.
            int[] currentState = Arrays.copyOf(initialState, N);
            for (int t = 0; t < timeSteps; t++) {
                // Store the number of infected nodes in `data` and then iterate
                // to the next state in the propagation process.
                data[t][IMMUNE_COUNT_COL] = immuneCount;

                // Store the number of infected nodes for the average column.
                int numOfInfected = numberOfInfectedNodes(currentState);
                data[t][avgIndex] = numOfInfected;

                // Store the minimum number of infected nodes for the min column.
                if (minimumAndMaximum[t][minIndex] > numOfInfected)
                    minimumAndMaximum[t][minIndex] = numOfInfected;

                // Store the maximum number of infected nodes for the max column.
                if (minimumAndMaximum[t][maxIndex] < numOfInfected)
                    minimumAndMaximum[t][maxIndex] = numOfInfected;

                // Store the next state of phenomena propagation.
                currentState = phenomena.propagate(matrix, currentState);
            }
            log(networks.getToken() + "  Ending Simulation " + runID + " (" + metric + ")");
        }


        // PHASE 2: Using interdependent centrality metrics against interdependent network 
        //          to immunize nodes for phenomena propagation.
        for (InterdependentCentrality metric : INTERDEPENDENT_CENTRALITIES) {
            // Get the indices for data storage for the current metric.
            switch (metric.type()) {
                case Centrality.PATH_DEGREE:
                    avgIndex = PATH_DEGREE_AVG_COL;
                    minIndex = PATH_DEGREE_MIN_COL;
                    maxIndex = PATH_DEGREE_MAX_COL;
                    break;
                case Centrality.WEIGHTED_BOUNDARY:
                    avgIndex = WBC_AVG_COL;
                    minIndex = WBC_MIN_COL;
                    maxIndex = WBC_MAX_COL;
                    break;
                default:
                    throw new Exception("Invalid Centrality metric type.");
            }

            // Initialize the first state and then immunize and infect it for the first time step..
            log(networks.getToken() + "  Starting Simulation " + runID + " (" + metric + ")");
            int[] initialState = Arrays.copyOf(controlledInitialFailure, N);
            immunize(initialState, networks, metric, 0);

            // Propagate phenomena throughout the network and store the state data at
            // the end of each time step.
            int[] currentState = Arrays.copyOf(initialState, N);
            for (int t = 0; t < timeSteps; t++) {
                // Store the number of infected nodes in `data` and then iterate
                // to the next state in the propagation process.
                data[t][IMMUNE_COUNT_COL] = immuneCount;

                // Store the number of infected nodes for the average column.
                int numOfInfected = numberOfInfectedNodes(currentState);
                data[t][avgIndex] = numOfInfected;

                // Store the minimum number of infected nodes for the min column.
                if (minimumAndMaximum[t][minIndex] > numOfInfected)
                    minimumAndMaximum[t][minIndex] = numOfInfected;

                // Store the maximum number of infected nodes for the max column.
                if (minimumAndMaximum[t][maxIndex] < numOfInfected)
                    minimumAndMaximum[t][maxIndex] = numOfInfected;

                // Store the next state of phenomena propagation.
                currentState = phenomena.propagate(matrix, currentState);
            }
            log(networks.getToken() + "  Ending Simulation " + runID + " (" + metric + ")");
        }
        return data;
    }

    public HashSet<Integer> getInitialImmuneNodes(IDN idn) {
        HashSet<Integer> set = new HashSet<Integer>();
        Centrality[] singleCentralities = { new BetweennessCentrality(), new ClosenessCentrality(), new DegreeCentrality(), new EigenvectorCentrality() };
        InterdependentCentrality[] interCentralities = { new PathDegreeCentrality(), new WBCentrality() };
        int n = idn.getNumOfNetworks();
        int[] nodes;

        // Step 2: If the simulation is running separate or bridged local centralities, compute accordingly to find central indices.
        if (separateCentralities) {
            int immune = immuneCount / n;
            Network net = null;
            for (Centrality metric : singleCentralities) {
                int offset = 0;
                for (int i = 0; i < n; i++) {
                    net = idn.getNetwork(i);
                    nodes = net.mostCentralNodes(metric, immune);
                    for (int node : nodes) set.add(node + offset);
                    offset += net.getNumOfNodes();
                }
            }
        } else {
            Network bridgedNetwork = idn.bridge();
            for (Centrality metric : singleCentralities) {
                nodes = bridgedNetwork.mostCentralNodes(metric, immuneCount);
                for (int node : nodes) set.add(node);
            }
        }

        // Step 3: Find most central nodes of entire interdependent network.
        for (InterdependentCentrality metric : interCentralities) {
            nodes = idn.mostCentralNodes(metric, immuneCount);
            for (int node : nodes) set.add(node);
        }

        return set;
    }

    /**
     * [initializeData description]
     * @return [description]
     */
    protected double[][] initializeData() {
        double[][] _data = new double[timeSteps][COLUMNS];

        for (int t = 0; t < timeSteps; t++) {
            // Set the timestamp and node count columns.
            _data[t][TIMESTAMP_COL] = t;
            _data[t][NODE_COUNT_COL] = networks.getNumOfNodes();
        }

        return _data;
    }


    /**
     * [initMinimumAndMaximum description]
     */
    public void initMinimumAndMaximum() {
        // Initialize the 2D array to keep track of minimum and maximum values;
        minimumAndMaximum = new double[timeSteps][COLUMNS];

        for (int t = 0; t < timeSteps; t++) {
            // Set all the minimum columns to negative infinity.
            minimumAndMaximum[t][BETWEENNESS_MIN_COL] = Double.POSITIVE_INFINITY;
            minimumAndMaximum[t][CLOSENESS_MIN_COL]   = Double.POSITIVE_INFINITY;
            minimumAndMaximum[t][DEGREE_MIN_COL]      = Double.POSITIVE_INFINITY;
            minimumAndMaximum[t][EIGENVECTOR_MIN_COL] = Double.POSITIVE_INFINITY;

            minimumAndMaximum[t][PATH_DEGREE_MIN_COL] = Double.POSITIVE_INFINITY;
            minimumAndMaximum[t][WBC_MIN_COL] = Double.POSITIVE_INFINITY;

            // Set all the minimum columns to negative infinity.
            minimumAndMaximum[t][BETWEENNESS_MAX_COL] = Double.NEGATIVE_INFINITY;
            minimumAndMaximum[t][CLOSENESS_MAX_COL]   = Double.NEGATIVE_INFINITY;
            minimumAndMaximum[t][DEGREE_MAX_COL]      = Double.NEGATIVE_INFINITY;
            minimumAndMaximum[t][EIGENVECTOR_MAX_COL] = Double.NEGATIVE_INFINITY;

            minimumAndMaximum[t][PATH_DEGREE_MAX_COL] = Double.NEGATIVE_INFINITY;
            minimumAndMaximum[t][WBC_MAX_COL] = Double.NEGATIVE_INFINITY;
        }
    }


    /**
     * Counts how many infected/failed/afflicted nodes exist within a nework state.
     * @param  state The current state of a network.
     * @return       The number of infected/failed/afflicted nodes in a network state.
     */
    private int numberOfInfectedNodes(int[] state) {
        int count = 0;
        for (int nodeState : state) {
            if (nodeState == Phenomena.AFFLICTED)
                count++;
        }
        return count;
    }


    /**
     * Immunize the number of nodes set by `immuneCount` in the provided state using a standard centrality metric for single networks. 
     * This method will overlook whether a node is failed or immune before immuzation, so it's best to provide an empty state to this
     * before failing nodes.
     * @param state   The state of the network topology. The state represents failed and immune nodes.
     * @param network Topology you wish to immunize with respect to.
     * @param metric  Standard centrality metric you wish to immunize with respect to.
     */
    private void immunize(int[] state, Network network, Centrality metric, int offset) {
        int[] immuneIndices = network.mostCentralNodes(metric, immuneCount);
        for (Integer immuneIndex : immuneIndices) {
            state[immuneIndex + offset] = Phenomena.IMMUNE;
        }
    }

    /**
     * Immunize the number of nodes set by `immuneCount` in the provided state using an interdepenent network for an interdependent network. 
     * This method will overlook whether a node is failed or immune before immuzation, so it's best to provide an empty state to this
     * before failing nodes.
     * @param state   The state of the network topology. The state represents failed and immune nodes.
     * @param network Topology you wish to immunize with respect to.
     * @param metric  Standard centrality metric you wish to immunize with respect to.
     */
    private void immunize(int[] state, IDN idn, InterdependentCentrality metric, int offset) {
        int[] immuneIndices = idn.mostCentralNodes(metric, immuneCount);
        for (Integer immuneIndex : immuneIndices) {
            state[immuneIndex + offset] = Phenomena.IMMUNE;
        }
    }

}
