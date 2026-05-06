import java.awt.Color;

/**
 * ─────────────────────────────────────────────────────────────────
 *  LPProblem.java
 *  Holds all the INPUT DATA for the LP problem.
 *  This is the only file you need to change if you want to
 *  solve a different problem.
 * ─────────────────────────────────────────────────────────────────
 */
public class LPProblem {

    // Objective function:  Z = cX*x + cY*y
    public static final double cX = 60;
    public static final double cY = 90;

    // Constraints: each row is { a, b, rhs } meaning  a*x + b*y <= rhs
    public static final double[][] CONSTRAINTS = {
            { 2, 6, 30 },   // 2x + 6y <= 30  (Station 1)
            { 5, 3, 80 },   // 5x + 3y <= 80  (Station 2)
    };

    // Label shown on the graph for each constraint line
    public static final String[] LABELS = {
            "2x+6y=30 (St.1)",
            "5x+3y=80 (St.2)"
    };

    // Color of each constraint line on the graph
    public static final Color[] LINE_COLORS = {
            new Color(0, 100, 200),   // blue  – Station 1
            new Color(200, 80,  0),   // orange – Station 2
    };
}