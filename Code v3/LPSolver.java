import java.util.ArrayList;
import java.util.List;

public class LPSolver {

    // Results filled in by solve()
    private List<double[]> cornerPoints = new ArrayList<>();
    private double[]       optimalPoint;
    private double         optimalZ;

    // ── Step 1: find the intersection of two lines ────────────────
    //   Line 1:  a1*x + b1*y = r1
    //   Line 2:  a2*x + b2*y = r2
    //   Returns null if lines are parallel.
    private double[] intersect(double a1, double b1, double r1,
                               double a2, double b2, double r2) {
        double det = a1 * b2 - a2 * b1;
        if (Math.abs(det) < 1e-10) return null;  // parallel → no intersection
        double x = (r1 * b2 - r2 * b1) / det;
        double y = (a1 * r2 - a2 * r1) / det;
        return new double[]{ x, y };
    }

    // ── Step 2: check if a point satisfies all constraints ────────
    //   Also enforces non-negativity: x >= 0 and y >= 0.
    private boolean isFeasible(double x, double y) {
        if (x < -1e-9 || y < -1e-9) return false;
        for (double[] c : LPProblem.CONSTRAINTS)
            if (c[0] * x + c[1] * y > c[2] + 1e-9) return false;
        return true;
    }

    // ── Step 3: evaluate the objective function Z = cX*x + cY*y ──
    public double Z(double x, double y) {
        return LPProblem.cX * x + LPProblem.cY * y;
    }

    // ── Main solver: runs all steps in order ──────────────────────
    public void solve() {

        // --- Collect all candidate corner points ---
        List<double[]> candidates = new ArrayList<>();

        // Always include the origin (0, 0)
        candidates.add(new double[]{ 0, 0 });

        // Intersect each constraint line with the axes
        for (double[] c : LPProblem.CONSTRAINTS) {
            candidates.add(intersect(c[0], c[1], c[2],  0, 1, 0)); // with x-axis (y=0)
            candidates.add(intersect(c[0], c[1], c[2],  1, 0, 0)); // with y-axis (x=0)
        }

        // Intersect every pair of constraint lines with each other
        double[][] C = LPProblem.CONSTRAINTS;
        for (int i = 0; i < C.length; i++)
            for (int j = i + 1; j < C.length; j++)
                candidates.add(intersect(C[i][0], C[i][1], C[i][2],
                        C[j][0], C[j][1], C[j][2]));

        // --- Keep only feasible candidates ---
        for (double[] pt : candidates)
            if (pt != null && isFeasible(pt[0], pt[1]))
                cornerPoints.add(pt);

        // --- Find the optimal point (highest Z) ---
        optimalZ = Double.NEGATIVE_INFINITY;
        for (double[] pt : cornerPoints) {
            double z = Z(pt[0], pt[1]);
            if (z > optimalZ) {
                optimalZ     = z;
                optimalPoint = pt;
            }
        }
    }

    // ── Getters ───────────────────────────────────────────────────
    public List<double[]> getCornerPoints() { return cornerPoints; }
    public double[]       getOptimalPoint() { return optimalPoint; }
    public double         getOptimalZ()     { return optimalZ;     }
}