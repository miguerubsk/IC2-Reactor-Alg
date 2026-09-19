package Simulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a heat exchanger of some sort in a reactor.
 * Ported from Ic2ExpReactorPlanner.components.Exchanger (generic, data-driven design).
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class Exchanger extends ReactorComponent {

    private final int switchSide;
    private final int switchReactor;

    public Exchanger(final int id, final String baseName, final String name,
            final double maxDamage, final double maxHeat, final String sourceMod, final int switchSide,
            final int switchReactor) {
        super(id, baseName, name, maxDamage, maxHeat, sourceMod);
        this.switchSide = switchSide;
        this.switchReactor = switchReactor;
    }

    public Exchanger(final Exchanger other) {
        super(other);
        this.switchSide = other.switchSide;
        this.switchReactor = other.switchReactor;
    }

    @Override
    public void transfer() {
        List<ReactorComponent> heatableNeighbors = new ArrayList<>(4);
        ReactorComponent component = parent.getComponentAt(row, col - 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row, col + 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row - 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row + 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        // Code adapted from decompiled IC2 code, class ItemReactorHeatSwitch, with permission from Thunderdark.
        double myHeat = 0;
        if (switchSide > 0) {
            for (ReactorComponent heatableNeighbor : heatableNeighbors) {
                double mymed = getCurrentHeat() * 100.0 / getMaxHeat();
                double heatablemed = heatableNeighbor.getCurrentHeat() * 100.0 / heatableNeighbor.getMaxHeat();

                double add = (int) (heatableNeighbor.getMaxHeat() / 100.0 * (heatablemed + mymed / 2.0));
                if (add > switchSide) {
                    add = switchSide;
                }
                if (heatablemed + mymed / 2.0 < 1.0) {
                    add = switchSide / 2;
                }
                if (heatablemed + mymed / 2.0 < 0.75) {
                    add = switchSide / 4;
                }
                if (heatablemed + mymed / 2.0 < 0.5) {
                    add = switchSide / 8;
                }
                if (heatablemed + mymed / 2.0 < 0.25) {
                    add = 1;
                }
                if (Math.round(heatablemed * 10.0) / 10.0 > Math.round(mymed * 10.0) / 10.0) {
                    add -= 2 * add;
                } else if (Math.round(heatablemed * 10.0) / 10.0 == Math.round(mymed * 10.0) / 10.0) {
                    add = 0;
                }
                myHeat -= add;
                if (add > 0) {
                    currentComponentHeating += add;
                }
                add = heatableNeighbor.adjustCurrentHeat(add);
                myHeat += add;
            }
        }
        if (switchReactor > 0) {
            double mymed = getCurrentHeat() * 100.0 / getMaxHeat();
            double reactorMed = parent.getCurrentHeat() * 100.0 / parent.getMaxHeat();

            int add = (int) Math.round(parent.getMaxHeat() / 100.0 * (reactorMed + mymed / 2.0));
            if (add > switchReactor) {
                add = switchReactor;
            }
            if (reactorMed + mymed / 2.0 < 1.0) {
                add = switchSide / 2;
            }
            if (reactorMed + mymed / 2.0 < 0.75) {
                add = switchSide / 4;
            }
            if (reactorMed + mymed / 2.0 < 0.5) {
                add = switchSide / 8;
            }
            if (reactorMed + mymed / 2.0 < 0.25) {
                add = 1;
            }
            if (Math.round(reactorMed * 10.0) / 10.0 > Math.round(mymed * 10.0) / 10.0) {
                add -= 2 * add;
            } else if (Math.round(reactorMed * 10.0) / 10.0 == Math.round(mymed * 10.0) / 10.0) {
                add = 0;
            }
            myHeat -= add;
            parent.adjustCurrentHeat(add);
            if (add > 0) {
                currentHullHeating = add;
            } else {
                currentHullCooling = -add;
            }
        }
        adjustCurrentHeat(myHeat);
    }

    @Override
    public double getHullCoolingCapacity() {
        return switchReactor;
    }
}
