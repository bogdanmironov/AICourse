package bg.sofia.uni.fmi.ai.queens;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinConflicts {

    private static int k = 10000;
    private static final Random random = new Random();
    private static int n = -1;

    public int[] solveForBoard(int N) {
        n = N;
        int[] board = new int[n];
        int[] rowConflicts = new int[n];
        int[] mainDiagonalConflicts = new int[2 * n - 1];
        int[] antiDiagonalConflicts = new int[2 * n - 1];

        if (n == 2 || n == 3) {
            return null;
        }

//        if (n > 1000) {
//            k = k * 20;
//        }

        Instant startTime = Instant.now();
        initQueenBoard(board, n, rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts);

        for (int i = 0; i < k * n; ++i) {
            int col =
                columnWithQueenWithMaxConflicts(board, rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts);
            int row = rowWithMinConflicts(col, board, rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts);


            if (!hasConflictsWithMaxCol(rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts, col, board[col])) {
                return board;
            }

            updateBoard(board, col, row, rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts);

            if (Duration.between(startTime, Instant.now()).toSeconds() > 2) {
                return null;
            }
        }

        if (hasConflicts(board, rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts)) {
            return solveForBoard(n);
        }

        return board;
    }

    private void initQueenBoard(int[] board,
                                int n,
                                int[] rowConflicts,
                                int[] mainDiagonalConflicts,
                                int[] antiDiagonalConflicts) {
        int row = random.nextInt(n);

        for (int col = 0; col < n; ++col) {
            board[col] = row;

            rowConflicts[row]++;
            mainDiagonalConflicts[row - col + n - 1]++;
            antiDiagonalConflicts[row + col]++;

            if (row + 2 > n && n % 2 == 0) {
                row = (row + 3) % n;
            } else {
                row = (row + 2) % n;
            }
        }
    }

    private static void updateBoard(int[] board, int col, int row, int[] rowConflicts, int[] mainDiagonalConflicts,
                                    int[] antiDiagonalConflicts) {
        int oldRow = board[col];

        rowConflicts[oldRow]--;
        mainDiagonalConflicts[oldRow - col + n - 1]--;
        antiDiagonalConflicts[oldRow + col]--;

        board[col] = row;

        rowConflicts[row]++;
        mainDiagonalConflicts[row - col + n - 1]++;
        antiDiagonalConflicts[row + col]++;
    }

    private int columnWithQueenWithMaxConflicts(int[] board, int[] rowConflicts, int[] mainDiagonalConflicts,
                                                int[] antiDiagonalConflicts) {
        int maxConflicts = -1;
        List<Integer> maxConflictColumns = new ArrayList<>();

        for (int col = 0; col < n; ++col) {
            int conflicts = countConflicts(col, board[col], rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts);

            if (conflicts > maxConflicts) {
                maxConflicts = conflicts;
                maxConflictColumns.clear();
                maxConflictColumns.add(col);
            } else if (conflicts == maxConflicts) {
                maxConflictColumns.add(col);
            }
        }

        return maxConflictColumns.get(random.nextInt(maxConflictColumns.size()));
    }

    private int rowWithMinConflicts(int col, int[] board, int[] rowConflicts, int[] mainDiagonalConflicts,
                                    int[] antiDiagonalConflicts) {
        int minConflicts = Integer.MAX_VALUE;
        List<Integer> minConflictRows = new ArrayList<>();

        for (int row = 0; row < n; ++row) {
            int conflicts = countConflicts(col, row, rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts);

            if (conflicts < minConflicts) {
                minConflicts = conflicts;
                minConflictRows.clear();
                minConflictRows.add(row);
            } else if (conflicts == minConflicts) {
                minConflictRows.add(row);
            }
        }

        return minConflictRows.get(random.nextInt(minConflictRows.size()));
    }

    private boolean hasConflicts(int[] board, int[] rowConflicts, int[] mainDiagonalConflicts,
                                 int[] antiDiagonalConflicts) {
        for (int col = 0; col < n; ++col) {
            if (countConflicts(col, board[col], rowConflicts, mainDiagonalConflicts, antiDiagonalConflicts) > 0) {
                return true;
            }
        }

        return false;
    }

    private boolean hasConflictsWithMaxCol(int[] rowConflicts,
                                           int[] mainDiagonalConflicts,
                                           int[] antiDiagonalConflicts,
                                           int maxCol,
                                           int maxRow) {
        return rowConflicts[maxRow] +
            mainDiagonalConflicts[maxRow - maxCol + n - 1] +
            antiDiagonalConflicts[maxRow + maxCol]
            - 3 != 0;
    }

    private int countConflicts(int col, int row, int[] rowConflicts, int[] mainDiagonalConflicts,
                               int[] antiDiagonalConflicts) {
        return rowConflicts[row]
            + mainDiagonalConflicts[row - col + n - 1]
            + antiDiagonalConflicts[row + col]
            - 3;
    }
}
