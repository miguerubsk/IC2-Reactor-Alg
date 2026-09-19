package Simulator;


/**
 * Represents a coolant cell in a reactor.
 * Ported from Ic2ExpReactorPlanner.components.CoolantCell (generic, data-driven design).
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class CoolantCell extends ReactorComponent {

    public CoolantCell(final int id, final String baseName, final String name,
            final double maxDamage, final double maxHeat, final String sourceMod) {
        super(id, baseName, name, maxDamage, maxHeat, sourceMod);
    }

    public CoolantCell(final CoolantCell other) {
        super(other);
    }

    @Override
    public double adjustCurrentHeat(final double heat) {
        currentCellCooling += heat;
        bestCellCooling = Math.max(currentCellCooling, bestCellCooling);
        return super.adjustCurrentHeat(heat);
    }
}
