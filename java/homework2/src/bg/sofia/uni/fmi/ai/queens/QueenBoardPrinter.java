package bg.sofia.uni.fmi.ai.queens;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueenBoardPrinter {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        BufferedWriter writer = new BufferedWriter(new FileWriter("inputs.txt"));
        int n = scanner.nextInt();
        writer.write(n);
        scanner.close();
        writer.close();

        MinConflicts solver = new MinConflicts();
        Instant startTime = Instant.now();
        int[] solution = solver.solveForBoard(n);
        Instant endTime = Instant.now();
        double timeTakenInSeconds = Duration.between(startTime, endTime).toMillis() / 1000.0;

        if (solution == null) {
            System.out.println("-1");
            return;
        }

        if (n > 100) {
            System.out.printf("%.2f", timeTakenInSeconds);
        } else {
//            printBoard(solution);
            ArrayList<Integer> solutionBoard = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                solutionBoard.add(solution[i]);
            }
            System.out.println(solutionBoard);
        }
    }

    public static void printBoard(List<Integer> board) {
        var boardRepresentationBuilder = new StringBuilder();
        for (int i = 0; i < board.size(); ++i) {
            for (int j = 0; j < board.size(); ++j) {
                if (board.get(j) == i) {
                    boardRepresentationBuilder.append("* ");
                } else {
                    boardRepresentationBuilder.append("_ ");
                }
            }

            boardRepresentationBuilder.append("\n");
        }

        System.out.println(boardRepresentationBuilder);
    }
}
