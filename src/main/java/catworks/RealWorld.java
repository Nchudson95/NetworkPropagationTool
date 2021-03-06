package catworks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RealWorld {

    private static final String IEEE_300_Data = "IEEE300.csv";

    public static int[][] IEEE300() {
        return readIntCSV(300, IEEE_300_Data);
    }

    private static int[][] readIntCSV(int n, String filename) {
        int i = 0;
        int[][] adj = new int[300][300];
        String line;
        // System.out.println("\n{");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            while ((line = reader.readLine()) != null) {
                // StringBuilder sb = new StringBuilder(i + ".  ");
                int j = 0;
                String[] node = line.split(",");
                for (String edge : node) {
                    // sb.append(edge + ",");
                    if (edge.equals("1")) {
                        adj[i][j] = 1;
                    } else {
                        adj[i][j] = 0;
                    }
                    j++;
                }
                // System.out.println(sb);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adj;
    }

}
