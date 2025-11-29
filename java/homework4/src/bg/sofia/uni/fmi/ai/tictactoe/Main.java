package bg.sofia.uni.fmi.ai.tictactoe;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        char[][] board = new char[3][3];
        char userCharacter;
        char computerCharacter;
        int depth = 0;


        try (var scanner = new Scanner(System.in)) {
            System.out.println("Do you want to go first? (1 - yes, 0 - no)");
            int answer = scanner.nextInt();

            if (answer == 1) {
                userCharacter = 'X';
                computerCharacter = 'O';
            } else {
                userCharacter = 'O';
                computerCharacter = 'X';
                int place = getBestMove(board, userCharacter, depth);
                board[place / 3][place % 3] = computerCharacter;
                ++depth;
            }

            while (!isGameFinished(board)) {
                printBoard(board);

                System.out.println("Enter move:");
                int x = scanner.nextInt();
                int y = scanner.nextInt();

                if (x < 0 || x > 2 || y < 0 || y > 2) {
                    System.out.println("Invalid move, try again");
                    continue;
                }

                if (board[x][y] == '\u0000') {
                    board[x][y] = userCharacter;
                    ++depth;
                } else {
                    System.out.println("Invalid move, try again");
                    continue;
                }

                if (isGameFinished(board)) {
                    break;
                }

                int place = getBestMove(board, userCharacter, depth);
                board[place / 3][place % 3] = computerCharacter;
                ++depth;
            }

            printBoard(board);
        }

    }

    private static int getBestMove(char[][] board, char userCharacter, int depth) {
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;

        if (userCharacter == 'X') {
            return minAction(board, a, b, depth, false);
        } else {
            return maxAction(board, a, b, depth, true);
        }
    }

    private static int maxAction(char[][] board, int a, int b, int depth, boolean isComputerFirst) {
        int maxScore = Integer.MIN_VALUE;
        int move = -1;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == '\u0000') {
                    board[i][j] = 'X';
                    int score = minScore(board, a, b, depth + 1, isComputerFirst);
                    board[i][j] = '\u0000';

                    if (score > maxScore) {
                        maxScore = score;
                        move = i * 3 + j;
                    }
                }
            }
        }

        return move;
    }

    private static int minAction(char[][] board, int a, int b, int depth, boolean isComputerFirst) {
        int minScore = Integer.MAX_VALUE;
        int move = -1;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == '\u0000') {
                    board[i][j] = 'O';
                    int score = maxScore(board, a, b, depth + 1, isComputerFirst);
                    board[i][j] = '\u0000';

                    if (score < minScore) {
                        minScore = score;
                        move = i * 3 + j;
                    }
                }
            }
        }

        return move;
    }

    private static int maxScore(char[][] board, int a, int b, int depth, boolean isComputerFirst) {
        if (isGameFinished(board)) {
            return getGameScore(board, depth);
        }

        int maxScore = Integer.MIN_VALUE;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == '\u0000') {
                    board[i][j] = 'X';
                    int score = minScore(board, a, b, depth + 1, isComputerFirst);
                    board[i][j] = '\u0000';

                    maxScore = Math.max(maxScore, score);
                    a = Math.max(a, score);

                    if (b <= a) {
                        return maxScore;
                    }
                }
            }
        }

        return maxScore;
    }

    private static int minScore(char[][] board, int a, int b, int depth, boolean isComputerFirst) {
        if (isGameFinished(board)) {
            return getGameScore(board, depth);
        }

        int minScore = Integer.MAX_VALUE;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == '\u0000') {
                    board[i][j] = 'O';
                    int score = maxScore(board, a, b, depth + 1, isComputerFirst);
                    board[i][j] = '\u0000';

                    minScore = Math.min(minScore, score);
                    b = Math.min(b, score);

                    if (b <= a) {
                        return minScore;
                    }
                }
            }
        }

        return minScore;
    }

    private static int getGameScore(char[][] board, int depth) {
        if (hasWinner(board, 'X')) {
            return 10 - depth;
        } else if (hasWinner(board, 'O')) {
            return depth - 10;
        } else {
            return 0;
        }
    }

    private static boolean isGameFinished(char[][] board) {
        return hasWinner(board, 'X') || hasWinner(board, 'O') || isBoardFull(board);
    }

    private static boolean hasWinner(char[][] board, char player) {
        for (int i = 0; i < 3; ++i) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
            (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private static boolean isBoardFull(char[][] board) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (board[i][j] == '\u0000') {
                    return false;
                }
            }
        }
        return true;
    }

    public static void printBoard(char[][] board) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                System.out.print(board[i][j] == '\u0000' ? '-' : board[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

}
