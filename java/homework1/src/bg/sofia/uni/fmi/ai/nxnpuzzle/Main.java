package bg.sofia.uni.fmi.ai.nxnpuzzle;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try (var in = new Scanner(System.in)) {
            int numTiles = Integer.parseInt(in.nextLine());

            int sideSize = (int) Math.sqrt(numTiles + 1);
            if (Math.pow(sideSize, 2) != (numTiles + 1)) {
                throw new IllegalArgumentException("There must be (x^2)-1 number of tiles");
            }

            int placeOfZero = Integer.parseInt(in.nextLine());

            if (placeOfZero == -1) {
                placeOfZero = numTiles;
            }

            if (!(placeOfZero >= 0 && placeOfZero <= numTiles)) {
                throw new IllegalArgumentException("Destination place of 0 must be between 0 and " + numTiles + " or -1");
            }

            int[][] matrix = new int[sideSize][sideSize];
            int[][] solvedMatrix = new int[sideSize][sideSize];
            int currentZeroPlace = -1;

            for (int i = 0; i < sideSize; i++) {
                for (int j = 0; j < sideSize; j++) {
                    matrix[i][j] = in.nextInt();

                    if (matrix[i][j] == 0) {
                        currentZeroPlace = i * sideSize + j;
                    }

                    solvedMatrix[i][j] = i*sideSize + j + 1;
                }
            }

            solvedMatrix[placeOfZero/sideSize][placeOfZero%sideSize] = 0;
            Matrix.solvedMatrix = solvedMatrix;

            Instant start = Instant.now();
            if (!IDAStar.isSolvable(matrix, placeOfZero, sideSize)) {
                System.out.println("-1");
                return;
            }

            Matrix entryMatrix = new Matrix(matrix, sideSize, 0, placeOfZero, currentZeroPlace);
            IDAStar solver = new IDAStar(entryMatrix, placeOfZero);

            List<Matrix> path = solver.getShortestPath();
            path = path.reversed();
            Instant end = Instant.now();

            System.out.println(path.size() - 1);
            for (int i = 1; i < path.size(); ++i) {
//                printMatrix(path.get(i-1).getMatrix());

                System.out.println(Matrix.calculateMove(path.get(i - 1), path.get(i)));
            }
//            printMatrix(path.get(path.size()-1).getMatrix());
            System.out.println(Duration.between(start, end));
        }
    }

    private static void printMatrix(int[][] matrix) {
        System.out.println("--------");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("--------");
    }

}
