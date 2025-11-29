package bg.sofia.uni.fmi.ai.traveller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GA {
    private static final int POPULATION_SIZE = 1000;
    private static final int NUM_GENERATIONS = 100;
    private static final double MUTATION_RATE = 0.3 ;
    private static final int TOURNAMENT_SIZE = 10;
    private static final int ELITE_COUNT = 5;

    public static int[] run(int numCities, double[][] distanceMatrix) throws IOException {
        List<int[]> population = init(numCities);
        double[] fitness = eval(population, distanceMatrix);
        int bestIdx = getBestIndex(fitness);

        double bestDistance = fitness[bestIdx];
        int[] bestPath = population.get(bestIdx).clone();
        System.out.printf("Generation %d, Best Distance: %.2f%n", 0, bestDistance);

        for (int generation = 0; generation < NUM_GENERATIONS; ++generation) {
            var pPopulation = select(population, fitness);

            List<int[]> sortedParents = new ArrayList<>(pPopulation);
            sortedParents.sort(Comparator.comparingDouble(ind -> calculatePathDistance(ind, distanceMatrix)));

            var cPopulation = reproduceWithElitism(pPopulation);

            population = cPopulation;
            fitness = eval(population, distanceMatrix);

            bestIdx = getBestIndex(fitness);
            double currentBestDistance = fitness[bestIdx];
            if (currentBestDistance < bestDistance) {
                bestDistance = currentBestDistance;
                bestPath = population.get(bestIdx).clone();
            }

            System.out.printf("Generation %d, Best Distance: %.2f%n", generation + 1, currentBestDistance);
        }

        return bestPath;
    }

    private static List<int[]> reproduceWithElitism(List<int[]> sortedParents) {
        List<int[]> newPopulation = new ArrayList<>();

        for (int i = 0; i < ELITE_COUNT; i++) {
            newPopulation.add(sortedParents.get(i).clone());
        }

        Random rand = new Random();
        while (newPopulation.size() < POPULATION_SIZE) {
            int idx1 = rand.nextInt(sortedParents.size());
            int idx2 = rand.nextInt(sortedParents.size());
            int[] parent1 = sortedParents.get(idx1);
            int[] parent2 = sortedParents.get(idx2);
            int[] child = crossover(parent1, parent2);
            mutate(child);
            newPopulation.add(child);
        }

        return newPopulation;
    }

    private static List<int[]> init(int numCities) {
        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<Integer> path = new ArrayList<>();
            for (int j = 0; j < numCities; j++) path.add(j);
            Collections.shuffle(path);
            population.add(path.stream().mapToInt(Integer::intValue).toArray());
        }
        return population;
    }

        private static List<int[]> select(List<int[]> population, double[] fitness) {
            List<int[]> selected = new ArrayList<>();
            Random rand = new Random();
            for (int i = 0; i < POPULATION_SIZE; i++) {
                int bestIdx = rand.nextInt(POPULATION_SIZE);
                for (int j = 1; j < TOURNAMENT_SIZE; j++) {
                    int idx = rand.nextInt(POPULATION_SIZE);
                    if (fitness[idx] < fitness[bestIdx]) bestIdx = idx;
                }
                selected.add(population.get(bestIdx));
            }
            return selected;
        }

    private static int[] crossover(int[] parent1, int[] parent2) {
        int size = parent1.length;
        int[] child = new int[size];
        Arrays.fill(child, -1);

        int start = (int) (Math.random() * size);
        int end = (int) (Math.random() * size);

        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        for (int i = start; i <= end; i++) {
            child[i] = parent1[i];
        }

        int currentIndex = (end + 1) % size;
        for (int i = 0; i < size; i++) {
            int gene = parent2[(end + 1 + i) % size];
            if (!containsGene(child, gene)) {
                child[currentIndex] = gene;
                currentIndex = (currentIndex + 1) % size;
            }
        }

        return child;
    }

    private static boolean containsGene(int[] array, int gene) {
        for (int value : array) {
            if (value == gene) return true;
        }
        return false;
    }

    private static void mutate(int[] path) {
        if (Math.random() < MUTATION_RATE) {
            int start = (int) (Math.random() * path.length);
            int end = (int) (Math.random() * path.length);
            if (start > end) {
                int temp = start;
                start = end;
                end = temp;
            }

            while (start < end) {
                int temp = path[start];
                path[start] = path[end];
                path[end] = temp;
                start++;
                end--;
            }
        }
    }

    private static double[] eval(List<int[]> population, double[][] distanceMatrix) {
        return population.stream().mapToDouble(path -> calculatePathDistance(path, distanceMatrix)).toArray();
    }

    public static double calculatePathDistance(int[] path, double[][] distanceMatrix) {
        double distance = 0;
        for (int i = 0; i < path.length - 1; i++) {
            distance += distanceMatrix[path[i]][path[i + 1]];
        }
//        distance += distanceMatrix[path[path.length - 1]][path[0]];
        return distance;
    }

    private static int getBestIndex(double[] fitness) {
        int bestIdx = 0;
        for (int i = 1; i < fitness.length; i++) {
            if (fitness[i] < fitness[bestIdx]) {
                bestIdx = i;
            }
        }
        return bestIdx;
    }
}
