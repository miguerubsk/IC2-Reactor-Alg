package Simulator;


/**
 * Represents some form of reactor plating, which changes how much heat the reactor
 * can hold, as well as somewhat reducing explosion power.
 * Ported from Ic2ExpReactorPlanner.components.Plating (generic, data-driven design).
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class Plating extends ReactorComponent {

    private final int heatAdjustment;

    public Plating(final int id, final String baseName, final String name,
            final double maxDamage, final double maxHeat, final String sourceMod, final int heatAdjustment,
            final double explosionPowerMultiplier) {
        super(id, baseName, name, maxDamage, maxHeat, sourceMod);
        this.heatAdjustment = heatAdjustment;
        this.explosionPowerMultiplier = explosionPowerMultiplier;
    }

    public Plating(final Plating other) {
        super(other);
        this.heatAdjustment = other.heatAdjustment;
        this.explosionPowerMultiplier = other.explosionPowerMultiplier;
    }

    @Override
    public void addToReactor() {
        super.addToReactor();
        if (parent != null) {
            parent.adjustMaxHeat(heatAdjustment);
        }
    }

    @Override
    public void removeFromReactor() {
        if (parent != null) {
            parent.adjustMaxHeat(-heatAdjustment);
        }
        super.removeFromReactor();
    }
}
