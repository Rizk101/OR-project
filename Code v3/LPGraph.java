import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LPGraph extends JPanel {

    // Solved data received from LPSolver
    private final LPSolver solver;

    // Graph margins (pixels)
    private final int LEFT = 70, RIGHT = 40, TOP = 60, BOTTOM = 55;

    // Axis range (computed from constraint data)
    private double xMax, yMax;

    // ── Constructor: receives the already-solved LPSolver ─────────
    public LPGraph(LPSolver solver) {
        this.solver = solver;
        computeAxisRange();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 560));
    }

    // ── Auto-scale axes based on the largest constraint intercepts ─
    private void computeAxisRange() {
        xMax = 0; yMax = 0;
        for (double[] c : LPProblem.CONSTRAINTS) {
            if (Math.abs(c[0]) > 1e-10) xMax = Math.max(xMax, c[2] / c[0]);
            if (Math.abs(c[1]) > 1e-10) yMax = Math.max(yMax, c[2] / c[1]);
        }
        xMax = Math.ceil(xMax * 1.3);  // 30% padding so lines don't touch the edge
        yMax = Math.ceil(yMax * 1.3);
    }

    // ── Coordinate converters: math → screen pixels ───────────────
    private int px(double x) { return LEFT + (int)(x / xMax * (getWidth()  - LEFT - RIGHT)); }
    private int py(double y) { return getHeight() - BOTTOM - (int)(y / yMax * (getHeight() - TOP - BOTTOM)); }

    // ── Swing calls this to paint the panel ───────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawAxes(g2);
        drawFeasibleRegion(g2);
        drawConstraints(g2);
        drawCornerPoints(g2);
        drawTitle(g2);
    }

    // ── Light grid lines ──────────────────────────────────────────
    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(0.5f));
        int stepX = (int) Math.max(1, xMax / 10);
        int stepY = (int) Math.max(1, yMax / 10);
        for (int x = 0; x <= xMax; x += stepX)
            g2.drawLine(px(x), TOP, px(x), py(0));
        for (int y = 0; y <= yMax; y += stepY)
            g2.drawLine(px(0), py(y), px(xMax), py(y));
    }

    // ── Axes with tick labels and axis names ──────────────────────
    private void drawAxes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(px(0), py(0), px(xMax), py(0)); // X axis
        g2.drawLine(px(0), py(0), px(0), py(yMax)); // Y axis

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        int stepX = (int) Math.max(1, xMax / 10);
        int stepY = (int) Math.max(1, yMax / 10);
        for (int x = 0; x <= xMax; x += stepX)
            g2.drawString(String.valueOf(x), px(x) - 5, py(0) + 15);
        for (int y = 0; y <= yMax; y += stepY)
            g2.drawString(String.valueOf(y), px(0) - 30, py(y) + 4);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("x  (Chicken Meals)", px(xMax / 2) - 55, py(0) + 38);
        g2.drawString("y (Beef Meals)", 2, py(yMax / 2));
    }

    // ── Shaded feasible region (polygon from computed corner points) ──
    private void drawFeasibleRegion(Graphics2D g2) {
        List<double[]> points = solver.getCornerPoints();

        // Sort by angle from centroid so the polygon draws correctly
        double cx = 0, cy = 0;
        for (double[] pt : points) { cx += pt[0]; cy += pt[1]; }
        final double fcx = cx / points.size();
        final double fcy = cy / points.size();
        points.sort((a, b) -> Double.compare(
                Math.atan2(a[1] - fcy, a[0] - fcx),
                Math.atan2(b[1] - fcy, b[0] - fcx)
        ));

        int[] xs = new int[points.size()];
        int[] ys = new int[points.size()];
        for (int i = 0; i < points.size(); i++) {
            xs[i] = px(points.get(i)[0]);
            ys[i] = py(points.get(i)[1]);
        }

        g2.setColor(new Color(144, 238, 144, 120)); // translucent green fill
        g2.fillPolygon(xs, ys, xs.length);
        g2.setColor(new Color(0, 160, 0));
        g2.setStroke(new BasicStroke(1f));
        g2.drawPolygon(xs, ys, xs.length);

        // Label at centroid
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(new Color(0, 120, 0));
        g2.drawString("Feasible", px(fcx) - 20, py(fcy) - 6);
        g2.drawString("Region",  px(fcx) - 16, py(fcy) + 8);
    }

    // ── Constraint lines (intercepts computed from constraint data) ──
    private void drawConstraints(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2f));
        for (int i = 0; i < LPProblem.CONSTRAINTS.length; i++) {
            double a = LPProblem.CONSTRAINTS[i][0];
            double b = LPProblem.CONSTRAINTS[i][1];
            double r = LPProblem.CONSTRAINTS[i][2];

            // y-intercept: x=0 → y=r/b  |  x-intercept: y=0 → x=r/a
            double x1 = 0,  y1 = r / b;
            double x2 = r / a, y2 = 0;

            g2.setColor(LPProblem.LINE_COLORS[i]);
            g2.drawLine(px(x1), py(y1), px(x2), py(y2));

            // Label at midpoint of the line
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString(LPProblem.LABELS[i],
                    px((x1 + x2) / 2) - 30,
                    py((y1 + y2) / 2) - 6);
        }
    }

    // ── Corner points with their Z values ────────────────────────
    private void drawCornerPoints(Graphics2D g2) {
        List<double[]> points = solver.getCornerPoints();
        double[] optimal = solver.getOptimalPoint();

        char letter = 'A';
        for (double[] pt : points) {
            double x = pt[0], y = pt[1];
            double z = solver.Z(x, y);
            boolean isOptimal = (pt == optimal);

            // Dot
            g2.setColor(isOptimal ? Color.RED : new Color(180, 120, 0));
            g2.fillOval(px(x) - 5, py(y) - 5, 10, 10);

            // Label: e.g.  "A(0,0) Z=0"  or  "C(15,0) Z=900 *"
            String label = String.format("%c(%.0f,%.0f) Z=%.0f%s",
                    letter++, x, y, z, isOptimal ? " *" : "");
            g2.setFont(new Font("Arial", isOptimal ? Font.BOLD : Font.PLAIN, 11));
            g2.setColor(isOptimal ? Color.RED : Color.BLACK);

            // Nudge label away from edges
            int lx = px(x) + 7;
            int ly = py(y) - 7;
            if (x > xMax * 0.65) lx = px(x) - 130;
            if (y < yMax * 0.08) ly = py(y) - 14;
            g2.drawString(label, lx, ly);
        }
    }

    // ── Title bar (text built from solver results) ────────────────
    private void drawTitle(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(String.format("LP Graph  -  Maximize Z = %.0fx + %.0fy",
                LPProblem.cX, LPProblem.cY), 90, 28);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(String.format("* Optimal: (%.0f, %.0f)  ->  Z_max = $%.0f",
                solver.getOptimalPoint()[0],
                solver.getOptimalPoint()[1],
                solver.getOptimalZ()), 90, 46);
    }
}