import javax.swing.*;
import java.awt.*;

/**
 * LP Graph – Maximize Z = 60x + 90y
 *   Station 1:  2x + 6y <= 30
 *   Station 2:  5x + 3y <= 80
 *   x >= 0,  y >= 0
 *
 * Feasible corner points:
 *   A = (0,  0)  → Z =   0
 *   B = (0,  5)  → Z = 450
 *   C = (15, 0)  → Z = 900  ← OPTIMAL
 */
public class LPGraph extends JPanel {

    // ── Graph margins ──────────────────────────────────────────────────────
    final int LEFT = 70, RIGHT = 30, TOP = 50, BOTTOM = 50;

    // ── Axis range ─────────────────────────────────────────────────────────
    final int X_MAX = 20;
    final int Y_MAX = 30;

    // ── Convert math coordinates to screen pixels ──────────────────────────
    int px(double x) { return LEFT + (int)(x / X_MAX * (getWidth()  - LEFT - RIGHT));  }
    int py(double y) { return getHeight() - BOTTOM - (int)(y / Y_MAX * (getHeight() - TOP - BOTTOM)); }

    // ── All drawing happens here ───────────────────────────────────────────
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

    // ── Light grid lines ───────────────────────────────────────────────────
    void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x <= X_MAX; x += 2)
            g2.drawLine(px(x), TOP, px(x), py(0));
        for (int y = 0; y <= Y_MAX; y += 5)
            g2.drawLine(px(0), py(y), px(X_MAX), py(y));
    }

    // ── X and Y axes with tick labels ─────────────────────────────────────
    void drawAxes(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));

        // X axis
        g2.drawLine(px(0), py(0), px(X_MAX), py(0));
        // Y axis
        g2.drawLine(px(0), py(0), px(0), py(Y_MAX));

        g2.setFont(new Font("Arial", Font.PLAIN, 11));

        // X tick labels (every 2 units)
        for (int x = 0; x <= X_MAX; x += 2) {
            g2.drawString(String.valueOf(x), px(x) - 5, py(0) + 15);
        }

        // Y tick labels (every 5 units)
        for (int y = 0; y <= Y_MAX; y += 5) {
            g2.drawString(String.valueOf(y), px(0) - 28, py(y) + 4);
        }

        // Axis name labels
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("x  (Chicken Meals)", px(8), py(0) + 35);
        g2.drawString("y (Beef Meals)", 2, py(Y_MAX / 2));
    }

    // ── Shaded feasible triangle: A(0,0), B(0,5), C(15,0) ────────────────
    void drawFeasibleRegion(Graphics2D g2) {
        int[] xs = { px(0), px(0),  px(15) };
        int[] ys = { py(0), py(5),  py(0)  };

        g2.setColor(new Color(144, 238, 144, 120)); // light green, semi-transparent
        g2.fillPolygon(xs, ys, 3);

        g2.setColor(new Color(0, 160, 0));
        g2.setStroke(new BasicStroke(1f));
        g2.drawPolygon(xs, ys, 3);

        // "Feasible Region" label inside the triangle
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("Feasible", px(3),  py(1) - 10);
        g2.drawString("Region",  px(3),  py(1) + 4);
    }

    // ── Two constraint boundary lines ─────────────────────────────────────
    void drawConstraints(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2f));

        // Constraint 1: 2x + 6y = 30  →  (0,5) to (15,0)
        g2.setColor(new Color(0, 100, 200));   // blue
        g2.drawLine(px(0), py(5), px(15), py(0));
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("2x+6y=30 (St.1)", px(1), py(3) - 4);

        // Constraint 2: 5x + 3y = 80  →  (0,26.67) to (16,0)
        g2.setColor(new Color(200, 80, 0));    // orange-red
        g2.drawLine(px(0), py(26.67), px(16), py(0));
        g2.drawString("5x+3y=80 (St.2)", px(7), py(14));
    }

    // ── Corner points with Z values ───────────────────────────────────────
    void drawCornerPoints(Graphics2D g2) {
        // Each entry: { x, y, label, xOffset, yOffset, isOptimal }
        Object[][] points = {
                { 0,  0,  "A(0,0)  Z=0",    6,  -8, false },
                { 0,  5,  "B(0,5)  Z=450",  6,  -8, false },
                { 15, 0,  "C(15,0) Z=900 *", -100, -8, true  },
        };

        for (Object[] p : points) {
            int x  = (int) p[0], y = (int) p[1];
            String label = (String) p[2];
            int ox = (int) p[3], oy = (int) p[4];
            boolean optimal = (boolean) p[5];

            // Draw dot
            g2.setColor(optimal ? Color.RED : new Color(180, 120, 0));
            g2.fillOval(px(x) - 5, py(y) - 5, 10, 10);

            // Draw label
            g2.setFont(new Font("Arial", optimal ? Font.BOLD : Font.PLAIN, 11));
            g2.drawString(label, px(x) + ox, py(y) + oy);
        }
    }

    // ── Title at the top ──────────────────────────────────────────────────
    void drawTitle(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("LP Graph  –  Maximize Z = 60x + 90y", 90, 30);
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("* Optimal: C(15, 0)  →  Z_max = $900", 90, 46);
    }

    // ── Window setup ──────────────────────────────────────────────────────
    public static void main(String[] args) {
        JFrame frame = new JFrame("Linear Programming Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 560);

        LPGraph panel = new LPGraph();
        panel.setBackground(Color.WHITE);
        frame.add(panel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}