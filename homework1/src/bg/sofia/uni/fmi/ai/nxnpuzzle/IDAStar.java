package bg.sofia.uni.fmi.ai.nxnpuzzle;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class IDAStar {
    int NODE_F_SOLVED = -1;
    Matrix entryMatrix;
    int placeOfZero;

    public IDAStar(Matrix entryMatrix, int placeOfZero) {
        this.entryMatrix = entryMatrix;
        this.placeOfZero = placeOfZero;
    }

    public List<Matrix> getShortestPath() {
        Deque<Matrix> path = new ArrayDeque<>();
        path.push(entryMatrix);

        int bound = entryMatrix.getH();

        while (true) {
//            System.out.println(bound);
            int nodeF = searchNode(path, bound);

            if (nodeF == NODE_F_SOLVED) {
                return path.stream().toList();
            }

            if (nodeF == Integer.MAX_VALUE) {
                return Collections.emptyList();
            }

            bound = nodeF;
        }
    }

    public int searchNode(Deque<Matrix> path, int bound) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path should not be empty when searching nodes.");
        }

        Matrix node = path.peek();

        if (node.getF() > bound) {
            return node.getF();
        }

        if (node.isSolved()) {
            return NODE_F_SOLVED;
        }

        int minF = Integer.MAX_VALUE;

        for (Matrix neighbor : node.neighbors()) {
            if (!path.contains(neighbor)) {
                path.push(neighbor);

                int minChildNodeF = searchNode(path, bound);

                if (minChildNodeF == NODE_F_SOLVED) {
                    return minChildNodeF;
                }

                if (minChildNodeF < minF) {
                    minF = minChildNodeF;
                }

                path.pop();
            }
        }

        return minF;
    }

    public static boolean isSolvable(int[][] start, int goalPlaceOfZero, int width) {
        int inversions = inversions(start, width);

        if (width % 2 == 0) {
            int goalZeroRowIndex = goalPlaceOfZero / width;
            int startZeroRowIndex = getZeroRow(start);
            return (inversions % 2) == (goalZeroRowIndex + startZeroRowIndex) % 2;
        } else {
            return (inversions % 2) == 0;
        }
    }

    private static int getZeroRow(int[][] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                if (matrix[i][j] == 0) {
                    return i;
                }
            }
        }

        throw new IllegalArgumentException("Zero row could not be found.");
    }

    private static int inversions(int[][] numbers, int width) {
        int inversions = 0;

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < width; ++j) {
                int n = numbers[i][j];

                if (n <= 1) {
                    continue;
                }

                for (int index = i * width + j + 1; index < width * width; ++index) {
                    int m = numbers[index / width][index % width];

                    if (m > 0 && n > m) {
                        inversions++;
                    }
                }
            }
        }

        return inversions;
    }

}
