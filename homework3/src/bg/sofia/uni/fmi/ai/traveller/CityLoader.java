package bg.sofia.uni.fmi.ai.traveller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CityLoader {
    public static List<String> loadCityNames(String filename) throws IOException {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line.trim());
            }
        }
        return names;
    }

    public static double[][] loadCityCoords(String filename) throws IOException {
        List<double[]> coords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                coords.add(new double[] { Double.parseDouble(parts[0]), Double.parseDouble(parts[1]) });
            }
        }
        return coords.toArray(new double[0][0]);
    }

    public static double[][] calculateDistanceMatrix(double[][] coords) {
        int numCities = coords.length;
        double[][] matrix = new double[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            for (int j = i + 1; j < numCities; j++) {
                double distance = Math.sqrt(Math.pow(coords[i][0] - coords[j][0], 2) + Math.pow(coords[i][1] - coords[j][1], 2));
                matrix[i][j] = distance;
                matrix[j][i] = distance;
            }
        }
        return matrix;
    }
}
