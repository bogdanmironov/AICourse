package bg.sofia.uni.fmi.ai.traveller;

import java.io.IOException;
import java.util.List;

import static bg.sofia.uni.fmi.ai.traveller.CityLoader.calculateDistanceMatrix;
import static bg.sofia.uni.fmi.ai.traveller.CityLoader.loadCityCoords;
import static bg.sofia.uni.fmi.ai.traveller.CityLoader.loadCityNames;
import static bg.sofia.uni.fmi.ai.traveller.GA.calculatePathDistance;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> cityNames = loadCityNames("homework3/res/uk12_name.csv");
        double[][] coords = loadCityCoords("homework3/res/uk12_xy.csv");
        double[][] distanceMatrix = calculateDistanceMatrix(coords);

        int[] bestPath = GA.run(cityNames.size(), distanceMatrix);
        double bestDistance = calculatePathDistance(bestPath, distanceMatrix);

        System.out.println("Best Path:");
        for (int city : bestPath) {
            System.out.print(cityNames.get(city) + " -> ");
        }
        System.out.println();
        System.out.println("Best Distance: " + bestDistance);
    }
}
