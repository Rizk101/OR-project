import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints;

/**
 * Linear Programming Graphical Method Visualizer
 * Problem: Maximize Z = 60x + 90y
 * Subject to:
 *   2x + 6y <= 30  (Station 1)
 *   5x + 3y <= 80  (Station 2)
 *   x >= 0, y >= 0
 *
 * Corner Points of Feasible Region:
 *   A = (0, 0)
 *   B = (0, 5)    <- intersection of Station 1 with y-axis
 *   C = (15, 0)   <- intersection of Station 1 with x-axis
 *   Note: The two constraint lines intersect at x=16.25, y=-0.416
 *         which is outside the feasible region (y < 0).
 *   So the feasible region is the triangle: A(0,0), B(0,5), C(15,0)
 *
 * Optimal solution evaluation at corner points:
 *   Z(0,0)  = 0
 *   Z(0,5)  = 60(0) + 90(5) = 450
 *   Z(15,0) = 60(15) + 90(0) = 900  <- MAXIMUM
 */
public class LinearProgrammingGraph extends JPanel {

    // --- Graph layout constants ---
    private static final int PADDING_LEFT   = 80;
    private static final int PADDING_BOTTOM = 60;
    private static final int PADDING_TOP    = 50;
    private static final int PADDING_RIGHT  = 180;

    // --- Data domain ---
    private static final double X_MAX = 20;
    private static final double Y_MAX = 30;

    // --- Color palette ---
    private static final Color BG_COLOR        = new Color(15,  17,  26);
    private static final Color GRID_COLOR      = new Color(40,  45,  65);
    private static final Color AXIS_COLOR      = new Color(200, 205, 220);
    private static final Color LABEL_COLOR     = new Color(200, 205, 220);
    private static final Color C1_COLOR        = new Color(64,  196, 255);   // Station 1 – cyan
    private static final Color C2_COLOR        = new Color(255, 140,  80);   // Station 2 – amber
    private static final Color FEASIBLE_COLOR  = new Color(100, 220, 140, 70);
    private static final Color CORNER_COLOR    = new Color(255, 220,  60);
    private static final Color OPTIMAL_COLOR   = new Color(255,  80, 120);
    private static final Color TITLE_COLOR     = new Color(240, 245, 255);

    public LinearProgrammingGraph() {
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(820, 640));
    }

    // Convert domain x → pixel x
    private int toPixelX(double x, int w) {
        double plotW = w - PADDING_LEFT - PADDING_RIGHT;
        return (int) (PADDING_LEFT + (x / X_MAX) * plotW);
    }

    // Convert domain y → pixel y  (y-axis grows upward)
    private int toPixelY(double y, int h) {
        double plotH = h - PADDING_TOP - PADDING_BOTTOM;
        return (int) (h - PADDING_BOTTOM - (y / Y_MAX) * plotH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        drawTitle(g2, w);
        drawGrid(g2, w, h);
        drawAxes(g2, w, h);
        drawAxisLabels(g2, w, h);
        drawFeasibleRegion(g2, w, h);
        drawConstraintLine1(g2, w, h);
        drawConstraintLine2(g2, w, h);
        drawCornerPoints(g2, w, h);
        drawLegend(g2, w, h);
        drawProfitTable(g2, w, h);
    }

    // -----------------------------------------------------------------------
    private void drawTitle(Graphics2D g2, int w) {
        g2.setColor(TITLE_COLOR);
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        String title = "Linear Programming – Graphical Method";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, 30);

        g2.setColor(new Color(130, 140, 180));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        String sub = "Maximize  Z = 60x + 90y";
        fm = g2.getFontMetrics();
        g2.drawString(sub, (w - fm.stringWidth(sub)) / 2, 46);
    }

    // -----------------------------------------------------------------------
    private void drawGrid(Graphics2D g2, int w, int h) {
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(0.5f));

        for (int x = 0; x <= (int) X_MAX; x += 2) {
            int px = toPixelX(x, w);
            g2.drawLine(px, PADDING_TOP, px, h - PADDING_BOTTOM);
        }
        for (int y = 0; y <= (int) Y_MAX; y += 5) {
            int py = toPixelY(y, h);
            g2.drawLine(PADDING_LEFT, py, w - PADDING_RIGHT, py);
        }
    }

    // -----------------------------------------------------------------------
    private void drawAxes(Graphics2D g2, int w, int h) {
        g2.setColor(AXIS_COLOR);
        g2.setStroke(new BasicStroke(2f));

        int ox = toPixelX(0, w);
        int oy = toPixelY(0, h);
        int xEnd = toPixelX(X_MAX, w);
        int yEnd = toPixelY(Y_MAX, h);

        // X axis
        g2.drawLine(ox, oy, xEnd + 10, oy);
        // Arrow
        g2.fillPolygon(new int[]{xEnd + 10, xEnd + 4, xEnd + 4},
                new int[]{oy,        oy - 4,   oy + 4}, 3);

        // Y axis
        g2.drawLine(ox, oy, ox, yEnd - 10);
        // Arrow
        g2.fillPolygon(new int[]{ox,      ox - 4,    ox + 4},
                new int[]{yEnd - 10, yEnd - 4, yEnd - 4}, 3);
    }

    // -----------------------------------------------------------------------
    private void drawAxisLabels(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.setColor(LABEL_COLOR);

        // X tick labels
        for (int x = 0; x <= (int) X_MAX; x += 2) {
            int px = toPixelX(x, w);
            int py = toPixelY(0, h);
            g2.drawString(String.valueOf(x), px - 5, py + 16);
        }
        // Y tick labels
        for (int y = 0; y <= (int) Y_MAX; y += 5) {
            int px = toPixelX(0, w);
            int py = toPixelY(y, h);
            g2.drawString(String.valueOf(y), px - 30, py + 4);
        }

        // Axis titles
        g2.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2.setColor(new Color(180, 190, 220));
        g2.drawString("x (Chicken Meals)", toPixelX(X_MAX / 2, w) - 55, h - PADDING_BOTTOM + 40);

        // Rotated Y label
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2r.setColor(new Color(180, 190, 220));
        g2r.rotate(-Math.PI / 2);
        g2r.drawString("y (Beef Meals)", -toPixelY(Y_MAX / 2, h) - 40, PADDING_LEFT - 48);
        g2r.dispose();
    }

    // -----------------------------------------------------------------------
    private void drawFeasibleRegion(Graphics2D g2, int w, int h) {
        // Feasible region corners: (0,0), (0,5), (15,0)
        int[] xs = {
                toPixelX(0,  w),
                toPixelX(0,  w),
                toPixelX(15, w)
        };
        int[] ys = {
                toPixelY(0, h),
                toPixelY(5, h),
                toPixelY(0, h)
        };
        g2.setColor(FEASIBLE_COLOR);
        g2.fillPolygon(xs, ys, 3);

        // Dashed border
        g2.setColor(new Color(100, 220, 140, 160));
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{4, 4}, 0));
        g2.drawPolygon(xs, ys, 3);
        g2.setStroke(new BasicStroke(1.5f));

        // Label
        g2.setColor(new Color(100, 220, 140));
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.drawString("Feasible", toPixelX(3, w), toPixelY(1.5, h));
        g2.drawString("Region",  toPixelX(3, w), toPixelY(1.5, h) + 13);
    }

    // -----------------------------------------------------------------------
    private void drawConstraintLine1(Graphics2D g2, int w, int h) {
        // 2x + 6y = 30  →  intercepts: (0,5) and (15,0)
        g2.setColor(C1_COLOR);
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawLine(toPixelX(0, w), toPixelY(5, h),
                toPixelX(15, w), toPixelY(0, h));

        // Label near mid
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString("2x + 6y = 30", toPixelX(3.5, w), toPixelY(4.2, h) - 6);
        g2.drawString("(Station 1)", toPixelX(3.5, w), toPixelY(4.2, h) + 7);
    }

    // -----------------------------------------------------------------------
    private void drawConstraintLine2(Graphics2D g2, int w, int h) {
        // 5x + 3y = 80  →  intercepts: (0, 26.67) and (16, 0)
        g2.setColor(C2_COLOR);
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawLine(toPixelX(0, w),  toPixelY(26.67, h),
                toPixelX(16, w), toPixelY(0, h));

        // Label near mid
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString("5x + 3y = 80", toPixelX(7, w), toPixelY(15, h) - 6);
        g2.drawString("(Station 2)", toPixelX(7, w), toPixelY(15, h) + 7);
    }

    // -----------------------------------------------------------------------
    private void drawCornerPoints(Graphics2D g2, int w, int h) {
        double[][] corners = {{0, 0}, {0, 5}, {15, 0}};
        String[]   labels  = {"A(0,0)\nZ=0", "B(0,5)\nZ=450", "C(15,0)\nZ=900★"};
        int[] offX = {6, 6, -72};
        int[] offY = {-8, -8, -8};

        for (int i = 0; i < corners.length; i++) {
            int px = toPixelX(corners[i][0], w);
            int py = toPixelY(corners[i][1], h);

            boolean isOptimal = (i == 2);

            // Outer glow for optimal
            if (isOptimal) {
                g2.setColor(new Color(255, 80, 120, 50));
                g2.fillOval(px - 10, py - 10, 20, 20);
            }

            // Dot
            g2.setColor(isOptimal ? OPTIMAL_COLOR : CORNER_COLOR);
            g2.fillOval(px - 5, py - 5, 10, 10);
            g2.setColor(BG_COLOR);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(px - 5, py - 5, 10, 10);

            // Label (split by \n)
            g2.setColor(isOptimal ? OPTIMAL_COLOR : CORNER_COLOR);
            g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            String[] lines = labels[i].split("\n");
            for (int l = 0; l < lines.length; l++) {
                g2.drawString(lines[l], px + offX[i], py + offY[i] + l * 13);
            }
        }
    }

    // -----------------------------------------------------------------------
    private void drawLegend(Graphics2D g2, int w, int h) {
        int lx = w - PADDING_RIGHT + 15;
        int ly = PADDING_TOP + 10;
        int lineH = 22;

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(new Color(180, 190, 220));
        g2.drawString("LEGEND", lx, ly);
        ly += 6;

        Object[][] items = {
                {C1_COLOR,       false, "Station 1"},
                {C2_COLOR,       false, "Station 2"},
                {new Color(100, 220, 140, 140), true,  "Feasible"},
                {CORNER_COLOR,   false, "Corner Pt"},
                {OPTIMAL_COLOR,  false, "Optimal ★"},
        };

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        for (Object[] item : items) {
            ly += lineH;
            Color c = (Color) item[0];
            boolean fill = (Boolean) item[1];
            String label = (String) item[2];

            if (fill) {
                g2.setColor(c);
                g2.fillRect(lx, ly - 9, 18, 10);
            } else {
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(lx, ly - 4, lx + 18, ly - 4);
            }
            g2.setColor(LABEL_COLOR);
            g2.drawString(label, lx + 24, ly);
        }
    }

    // -----------------------------------------------------------------------
    private void drawProfitTable(Graphics2D g2, int w, int h) {
        int tx = w - PADDING_RIGHT + 15;
        int ty = PADDING_TOP + 160;

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.setColor(new Color(180, 190, 220));
        g2.drawString("PROFIT TABLE", tx, ty);

        String[] headers = {"Pt", "  x", "  y", "     Z"};
        String[][] rows = {
                {"A", " 0", " 0", "      0"},
                {"B", " 0", " 5", "    450"},
                {"C", "15", " 0", "    900"},
        };

        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        int rowH = 16;
        ty += 10;

        // Header
        g2.setColor(new Color(130, 140, 180));
        StringBuilder hLine = new StringBuilder();
        for (String hdr : headers) hLine.append(hdr);
        g2.drawString(hLine.toString(), tx, ty += rowH);

        // Separator
        g2.setColor(GRID_COLOR);
        g2.drawLine(tx, ty + 3, tx + 130, ty + 3);

        for (int r = 0; r < rows.length; r++) {
            boolean isOpt = (r == 2);
            ty += rowH;
            g2.setColor(isOpt ? OPTIMAL_COLOR : LABEL_COLOR);
            if (isOpt) g2.setFont(new Font("Monospaced", Font.BOLD, 10));
            else        g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            StringBuilder sb = new StringBuilder();
            for (String cell : rows[r]) sb.append(cell);
            String line = sb.toString() + (isOpt ? " ★" : "");
            g2.drawString(line, tx, ty);
        }

        ty += rowH + 6;
        g2.setColor(new Color(130, 140, 180));
        g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
        g2.drawString("★ Optimal at C(15,0)", tx, ty);
        g2.drawString("  Z_max = $900", tx, ty + 12);
    }

    // -----------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LP Graphical Method – Food Company");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            LinearProgrammingGraph panel = new LinearProgrammingGraph();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

