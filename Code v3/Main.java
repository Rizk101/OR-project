import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // ── Step 1: Solve the LP problem ──────────────────────────
        LPSolver solver = new LPSolver();
        solver.solve();

        // ── Step 2: Print results to the console ──────────────────
        System.out.println("=== LP SOLVER RESULTS ===");
        System.out.printf("Objective: Z = %.0fx + %.0fy%n",
                LPProblem.cX, LPProblem.cY);
        System.out.println("Corner points of feasible region:");
        for (double[] pt : solver.getCornerPoints())
            System.out.printf("  (%.2f, %.2f)  ->  Z = %.2f%n",
                    pt[0], pt[1], solver.Z(pt[0], pt[1]));
        System.out.printf("Optimal: (%.2f, %.2f)  ->  Z_max = %.2f%n",
                solver.getOptimalPoint()[0],
                solver.getOptimalPoint()[1],
                solver.getOptimalZ());

        // ── Step 3: Build and show the graph window ────────────────
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Linear Programming Solver");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new LPGraph(solver));  // pass solved data to the graph
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}