package bg.sofia.uni.fmi.ai.nxnpuzzle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

public class Matrix {
    public static int[][] solvedMatrix;
    private final int zeroDestinationPlace;

    private final int[][] matrix;
    private final int zeroCurrentPlace;

    private final int size;

    private final int h;
    private final int g;

    public Matrix(int[][] matrix, int side_size, int g, int zeroDestinationPlace, int zeroCurrentPlace) {
        this.matrix = matrix;
        this.zeroDestinationPlace = zeroDestinationPlace;
        this.zeroCurrentPlace = zeroCurrentPlace;
        this.size = side_size;

        this.h = calculateH(zeroDestinationPlace);
        this.g = g;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getSize() {
        return size;
    }

    public int getF() {
        return getG() + getH();
    }

    public int getH() {
        return h;
    }

    public int getG() {
        return g;
    }

    boolean isSolved() {
        return Arrays.deepEquals(solvedMatrix, matrix);
    }

    private int calculateH(int zeroDestinationPlace) {
        int manhattanDistanceSum = 0;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int value = matrix[x][y];

                if (value == 0) {
                    continue;
                }

                if (value <= zeroDestinationPlace) {
                    int targetX = (value - 1) / size;
                    int targetY = (value - 1) % size;
                    int dx = x - targetX;
                    int dy = y - targetY;
                    manhattanDistanceSum += Math.abs(dx) + Math.abs(dy);
                } else {
                    int targetX = (value) / size;
                    int targetY = (value) % size;
                    int dx = x - targetX;
                    int dy = y - targetY;
                    manhattanDistanceSum += Math.abs(dx) + Math.abs(dy);
                }
            }
        }
        return manhattanDistanceSum;
    }

    public Iterable<Matrix> neighbors() {
        PriorityQueue<Matrix> neighbors = new PriorityQueue<>(Comparator.comparingInt(Matrix::getF));

        int zeroRow = zeroCurrentPlace / size;
        int zeroColumn = zeroCurrentPlace % size;

        int[][] destinations = {
            {zeroRow + 1, zeroColumn},
            {zeroRow - 1, zeroColumn},
            {zeroRow, zeroColumn + 1},
            {zeroRow, zeroColumn - 1},
        };

        for (int[] dest : destinations) {
            if (isValidCell(dest[0], dest[1])) {
                var newMatrix = Arrays.stream(matrix).map(int[]::clone).toArray(int[][]::new);
                newMatrix[zeroRow][zeroColumn] = newMatrix[dest[0]][dest[1]];
                newMatrix[dest[0]][dest[1]] = 0;

                neighbors.add(new Matrix(newMatrix, size, g + 1, zeroDestinationPlace, dest[0] * size + dest[1]));
            }
        }

        return neighbors;
    }

    private boolean isValidCell(int neighborZeroRow, int neighborZeroColumn) {
        return neighborZeroRow >= 0 && neighborZeroRow < size && neighborZeroColumn >= 0 && neighborZeroColumn < size;
    }

    public static String calculateMove(Matrix first, Matrix second) {
        int firstX = -1, firstY = -1, secondX = -1, secondY = -1;

        for (int i = 0; i < first.size; ++i) {
            for (int j = 0; j < first.size; ++j) {
                if (first.getMatrix()[i][j] == 0) {
                    firstX = i;
                    firstY = j;
                }
                if (second.getMatrix()[i][j] == 0) {
                    secondX = i;
                    secondY = j;
                }
            }
        }

        if (firstX == -1 || firstY == -1 || secondX == -1 || secondY == -1) {
            throw new IllegalArgumentException("Invalid move");
        }

        //Only straight moves by 1 are allowed
        int dx = secondX - firstX;
        int dy = secondY - firstY;

        //We are tracking the movement of the non-zero tile
        if (dx == -1) {
            return "down";
        } else if (dx == 1) {
            return "up";
        } else if (dy == -1) {
            return "right";
        } else if (dy == 1) {
            return "left";
        }

        throw new IllegalArgumentException("Invalid move");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix1 = (Matrix) o;
        return Objects.deepEquals(matrix, matrix1.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
    }
}
