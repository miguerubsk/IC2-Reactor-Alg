package io.github.miguerubsk.ic2reactoralg.simulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents some form of fuel rod (may be single, dual, or quad).
 * Ported from Ic2ExpReactorPlanner.components.FuelRod (generic, data-driven design).
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class FuelRod extends ReactorComponent {

    private final int energyMult;
    private final double heatMult;
    private final int rodCount;
    private final boolean moxStyle;

    public FuelRod(final int id, final String baseName, final String name, final double maxDamage,
            final double maxHeat, final String sourceMod, final int energyMult, final double heatMult,
            final int rodCount, final boolean moxStyle) {
        super(id, baseName, name, maxDamage, maxHeat, sourceMod);
        this.energyMult = energyMult;
        this.heatMult = heatMult;
        this.rodCount = rodCount;
        this.moxStyle = moxStyle;
    }

    public FuelRod(final FuelRod other) {
        super(other);
        this.energyMult = other.energyMult;
        this.heatMult = other.heatMult;
        this.rodCount = other.rodCount;
        this.moxStyle = other.moxStyle;
    }

    @Override
    public boolean isNeutronReflector() {
        return !isBroken();
    }

    private int countNeutronNeighbors() {
        int neutronNeighbors = 0;
        ReactorComponent component = parent.getComponentAt(row + 1, col);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        component = parent.getComponentAt(row - 1, col);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        component = parent.getComponentAt(row, col - 1);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        component = parent.getComponentAt(row, col + 1);
        if (component != null && component.isNeutronReflector()) {
            neutronNeighbors++;
        }
        return neutronNeighbors;
    }

    protected void handleHeat(final int heat) {
        List<ReactorComponent> heatableNeighbors = new ArrayList<>(4);
        ReactorComponent component = parent.getComponentAt(row + 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row - 1, col);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row, col - 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        component = parent.getComponentAt(row, col + 1);
        if (component != null && component.isHeatAcceptor()) {
            heatableNeighbors.add(component);
        }
        if (heatableNeighbors.isEmpty()) {
            parent.adjustCurrentHeat(heat);
            currentHullHeating = heat;
        } else {
            currentComponentHeating = heat;
            for (ReactorComponent heatableNeighbor : heatableNeighbors) {
                heatableNeighbor.adjustCurrentHeat(heat / heatableNeighbors.size());
            }
            int remainderHeat = heat % heatableNeighbors.size();
            heatableNeighbors.get(0).adjustCurrentHeat(remainderHeat);
        }
    }

    @Override
    public double generateHeat() {
        int pulses = countNeutronNeighbors() + (rodCount == 1 ? 1 : (rodCount == 2) ? 2 : 3);
        int heat = (int) (heatMult * pulses * (pulses + 1));
        if (moxStyle && parent.isFluid() && (parent.getCurrentHeat() / parent.getMaxHeat()) > 0.5) {
            heat *= 2;
        }
        currentHeatGenerated = heat;
        handleHeat(heat);
        return currentHeatGenerated;
    }

    @Override
    public double generateEnergy() {
        int pulses = countNeutronNeighbors() + (rodCount == 1 ? 1 : (rodCount == 2) ? 2 : 3);
        double energy = energyMult * pulses;
        if (moxStyle) {
            energy *= (1 + 4.0 * parent.getCurrentHeat() / parent.getMaxHeat());
        }
        currentEuGenerated = energy;
        parent.addEuOutput(energy);
        applyDamage(1.0);
        return energy;
    }

    @Override
    public int getRodCount() {
        return rodCount;
    }
}
