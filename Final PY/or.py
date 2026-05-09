import numpy as np
import matplotlib.pyplot as plt

# ── الحل الرياضي ──────────────────────────────────────────
corner_points = [(0, 0), (0, 5), (15, 0)]  # A, B, C
labels = ["A (0,0)", "B (0,5)", "C (15,0)"]

def Z(x, y):
    return 60 * x + 90 * y

profits = [Z(x, y) for x, y in corner_points]
best_i  = profits.index(max(profits))

# ── طباعة النتائج ─────────────────────────────────────────
print("=" * 40)
print("  Maximize Z = 60x + 90y")
print("=" * 40)
for i, (pt, z) in enumerate(zip(labels, profits)):
    mark = " <- OPTIMAL SOLUTION" if i == best_i else ""
    print(f"  {pt:<12}  Z = ${z:.0f}{mark}")
print("=" * 40)
x_opt, y_opt = corner_points[best_i]
print(f"\n  x (Chicken) = {x_opt}")
print(f"  y (Beef)    = {y_opt}")
print(f"  Max Profit  = ${profits[best_i]:.0f}")

# ── الرسم ────────────────────────────────────────────────
x = np.linspace(0, 20, 400)

fig, ax = plt.subplots(figsize=(8, 6))
fig.patch.set_facecolor("#f5f7fa")

#Constrains
ax.plot(x, (30 - 2*x) / 6, color="#2563eb", lw=2, label="Station 1: 2x+6y=30")
ax.plot(x, (80 - 5*x) / 3, color="#dc2626", lw=2, label="Station 2: 5x+3y=80")

# fasible Region
y_feas = np.minimum((30 - 2*x)/6, (80 - 5*x)/3).clip(0)
ax.fill_between(x, 0, y_feas, alpha=0.15, color="#2563eb", label="Feasible Region")

# نقاط الزوايا
colors = ["#64748b", "#7c3aed", "#f59e0b"]
offsets = [(0.4, 0.3), (0.4, 0.3), (0.3, 0.4)]
for i, ((px, py), lbl) in enumerate(zip(corner_points, labels)):
    c = "#f59e0b" if i == best_i else colors[i]
    ax.scatter(px, py, color=c, s=100, zorder=5, edgecolors="white", lw=1.5)
    ox, oy = offsets[i]  #كتابه البيانات علي النقط 
    ax.annotate(f"{lbl}\nZ=${profits[i]:.0f}", xy=(px, py),
                xytext=(px+ox, py+oy), fontsize=8.5, color=c,
                bbox=dict(boxstyle="round,pad=0.3", fc="white", ec=c, alpha=0.9))

# صندوق النتيجة
ax.text(13, 22,
        f"Optimal Point\n--------------\nx={x_opt}, y={y_opt}\nMax Z = ${profits[best_i]:.0f}",
        fontsize=9, family="monospace",
        bbox=dict(boxstyle="round,pad=0.5", fc="#fefce8", ec="#f59e0b", lw=1.5),
        va="top")

ax.set_xlim(-1, 20)
ax.set_ylim(-1, 30)
ax.axhline(0, color="black", lw=0.8)
ax.axvline(0, color="black", lw=0.8)
ax.grid(True, ls="--", alpha=0.4)
ax.set_xlabel("x  (Chicken Meals)", fontsize=11)
ax.set_ylabel("y  (Beef Meals)",    fontsize=11)
ax.set_title("LP - Food Company\nMaximize Z = 60x + 90y",
             fontsize=13, fontweight="bold")
ax.legend(fontsize=9)

plt.tight_layout()
plt.show()