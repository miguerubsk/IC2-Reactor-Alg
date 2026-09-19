package Simulator;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents some kind of vent in a reactor.
 * Ported from Ic2ExpReactorPlanner.components.Vent (generic, data-driven design).
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class Vent extends ReactorComponent {

    private final int selfVent;
    private final int hullDraw;
    private final int sideVent;

    public Vent(final int id, final String baseName, final String name, final Image image, final double maxDamage,
            final double maxHeat, final String sourceMod, final int selfVent, final int hullDraw, final int sideVent) {
        super(id, baseName, name, image, maxDamage, maxHeat, sourceMod);
        this.selfVent = selfVent;
        this.hullDraw = hullDraw;
        this.sideVent = sideVent;
    }

    public Vent(final Vent other) {
        super(other);
        this.selfVent = other.selfVent;
        this.hullDraw = other.hullDraw;
        this.sideVent = other.sideVent;
    }

    @Override
    public double dissipate() {
        double deltaHeat = Math.min(hullDraw, parent.getCurrentHeat());
        currentHullCooling = deltaHeat;
        parent.adjustCurrentHeat(-deltaHeat);
        this.adjustCurrentHeat(deltaHeat);
        final double currentDissipation = Math.min(selfVent, getCurrentHeat());
        currentVentCooling = currentDissipation;
        parent.ventHeat(currentDissipation);
        adjustCurrentHeat(-currentDissipation);
        if (sideVent > 0) {
            List<ReactorComponent> coolableNeighbors = new ArrayList<>(4);
            ReactorComponent component = parent.getComponentAt(row - 1, col);
            if (component != null && component.isCoolable()) {
                coolableNeighbors.add(component);
            }
            component = parent.getComponentAt(row, col + 1);
            if (component != null && component.isCoolable()) {
                coolableNeighbors.add(component);
            }
            component = parent.getComponentAt(row + 1, col);
            if (component != null && component.isCoolable()) {
                coolableNeighbors.add(component);
            }
            component = parent.getComponentAt(row, col - 1);
            if (component != null && component.isCoolable()) {
                coolableNeighbors.add(component);
            }
            for (ReactorComponent coolableNeighbor : coolableNeighbors) {
                double rejectedCooling = coolableNeighbor.adjustCurrentHeat(-sideVent);
                double tempDissipatedHeat = sideVent + rejectedCooling;
                parent.ventHeat(tempDissipatedHeat);
                currentVentCooling += tempDissipatedHeat;
            }
        }
        effectiveVentCooling = Math.max(effectiveVentCooling, currentVentCooling);
        return currentDissipation;
    }

    @Override
    public double getVentCoolingCapacity() {
        double result = selfVent;
        if (sideVent > 0) {
            ReactorComponent component = parent.getComponentAt(row - 1, col);
            if (component != null && component.isCoolable()) {
                result += sideVent;
            }
            component = parent.getComponentAt(row, col + 1);
            if (component != null && component.isCoolable()) {
                result += sideVent;
            }
            component = parent.getComponentAt(row + 1, col);
            if (component != null && component.isCoolable()) {
                result += sideVent;
            }
            component = parent.getComponentAt(row, col - 1);
            if (component != null && component.isCoolable()) {
                result += sideVent;
            }
        }
        return result;
    }

    @Override
    public double getHullCoolingCapacity() {
        return hullDraw;
    }
}
