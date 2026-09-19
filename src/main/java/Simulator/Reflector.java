package Simulator;


/**
 * Represents a neutron reflector in a reactor.
 * Ported from Ic2ExpReactorPlanner.components.Reflector (generic, data-driven design).
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class Reflector extends ReactorComponent {

    public Reflector(final int id, final String baseName, final String name,
            final double maxDamage, final double maxHeat, final String sourceMod) {
        super(id, baseName, name, maxDamage, maxHeat, sourceMod);
    }

    public Reflector(final Reflector other) {
        super(other);
    }

    @Override
    public boolean isNeutronReflector() {
        return !isBroken();
    }

    @Override
    public double generateHeat() {
        ReactorComponent component = parent.getComponentAt(row - 1, col);
        if (component != null && component.isNeutronReflector()) {
            applyDamage(component.getRodCount());
        }
        component = parent.getComponentAt(row, col + 1);
        if (component != null && component.isNeutronReflector()) {
            applyDamage(component.getRodCount());
        }
        component = parent.getComponentAt(row + 1, col);
        if (component != null && component.isNeutronReflector()) {
            applyDamage(component.getRodCount());
        }
        component = parent.getComponentAt(row, col - 1);
        if (component != null && component.isNeutronReflector()) {
            applyDamage(component.getRodCount());
        }
        return 0;
    }
}
