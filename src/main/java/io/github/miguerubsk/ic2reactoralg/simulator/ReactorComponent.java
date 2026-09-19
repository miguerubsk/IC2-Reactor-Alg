package io.github.miguerubsk.ic2reactoralg.simulator;

/**
 * Represents a component in an IndustrialCraft2 Experimental Nuclear Reactor.
 *
 * Ported from the data-driven "ReactorItem" base class introduced by the
 * upstream Ic2ExpReactorPlanner project (Ic2ExpReactorPlanner.components.ReactorItem).
 * The class keeps the historical name "ReactorComponent" (and the subset of the
 * public API that the genetic algorithm depends on) so that callers outside of
 * the Simulator package do not need to change.
 *
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class ReactorComponent {

    // Fundamental values, set at object instantiation, should never need to be changed.
    public final int id;
    public final String baseName; // non-localized name, for internal/program use (matches ComponentFactory table)
    public final String name; // display name
    protected double maxDamage;
    protected double maxHeat;
    public final String sourceMod; // null for base IC2 components

    // Simulation setting values
    private double initialHeat = 0;

    public double getInitialHeat() {
        return initialHeat;
    }

    public void setInitialHeat(final double value) {
        if (this.isHeatAcceptor() && value >= 0 && value < this.maxHeat) {
            initialHeat = value;
        }
    }

    public int automationThreshold = 9000;

    public int reactorPause = 0;

    // Parent reactor and position
    protected Reactor parent = null;
    protected int row = -10;
    protected int col = -10;

    // Calculated values.
    protected double currentDamage = 0;
    protected double currentHeat = 0;

    protected double currentEuGenerated = 0;
    protected double currentHeatGenerated = 0;

    protected double currentHullHeating = 0;
    protected double currentComponentHeating = 0;
    protected double currentHullCooling = 0;
    protected double currentVentCooling = 0;
    protected double effectiveVentCooling = 0; // best/last vent cooling seen, kept for compatibility

    protected double currentCellCooling = 0;
    protected double bestCellCooling = 0;

    protected double currentCondensatorCooling = 0;
    protected double bestCondensatorCooling = 0;

    protected double explosionPowerMultiplier = 1;

    protected ReactorComponent(final int id, final String baseName, final String name,
            final double maxDamage, final double maxHeat, final String sourceMod) {
        this.id = id;
        this.baseName = baseName;
        this.name = name;
        this.maxDamage = maxDamage;
        this.maxHeat = maxHeat;
        if (maxHeat > 1) {
            automationThreshold = (int) (maxHeat * 0.9);
        } else if (maxDamage > 1) {
            automationThreshold = (int) (maxDamage * 1.1);
        }
        this.sourceMod = sourceMod;
    }

    // Copy constructor for use by subclasses (ComponentFactory uses this to create new instances of the default components).
    protected ReactorComponent(final ReactorComponent other) {
        this.id = other.id;
        this.baseName = other.baseName;
        this.name = other.name;
        this.maxDamage = other.maxDamage;
        this.maxHeat = other.maxHeat;
        this.initialHeat = other.initialHeat;
        this.automationThreshold = other.automationThreshold;
        this.reactorPause = other.reactorPause;
        this.sourceMod = other.sourceMod;
    }

    @Override
    public String toString() {
        String result = name;
        if (initialHeat > 0) {
            result += String.format(" (initial heat: %,d)", (int) initialHeat);
        }
        return result;
    }

    public final int getRow() {
        return row;
    }

    public final void setRow(int row) {
        this.row = row;
    }

    public final int getColumn() {
        return col;
    }

    public final void setColumn(int col) {
        this.col = col;
    }

    protected Reactor getParent() {
        return parent;
    }

    public void setParent(Reactor parent) {
        this.parent = parent;
    }

    /**
     * Checks if this component can accept heat. (e.g. from adjacent fuel rods, or from an exchanger)
     */
    public boolean isHeatAcceptor() {
        return maxHeat > 1 && !isBroken();
    }

    /**
     * Determines if this component can be cooled down, such as by a component heat vent.
     */
    public boolean isCoolable() {
        return maxHeat > 1 && !(this instanceof Condensator);
    }

    /**
     * Checks if this component acts as a neutron reflector, and boosts performance of adjacent fuel rods.
     */
    public boolean isNeutronReflector() {
        return false;
    }

    /**
     * Prepare for a new reactor tick.
     */
    public void preReactorTick() {
        currentHullHeating = 0.0;
        currentComponentHeating = 0.0;
        currentHullCooling = 0.0;
        currentVentCooling = 0.0;
        currentCellCooling = 0.0;
        currentCondensatorCooling = 0.0;
        currentEuGenerated = 0;
        currentHeatGenerated = 0;
    }

    /**
     * Generate heat if appropriate for component type, and spread to reactor or adjacent cells.
     * @return the amount of heat generated by this component.
     */
    public double generateHeat() {
        return 0.0;
    }

    /**
     * Generate energy if appropriate for component type.
     * @return the number of EU generated by this component during the current reactor tick.
     */
    public double generateEnergy() {
        return 0.0;
    }

    /**
     * Dissipate (aka vent) heat if appropriate for component type.
     * @return the amount of heat successfully vented during the current reactor tick.
     */
    public double dissipate() {
        return 0.0;
    }

    /**
     * Transfer heat between component, neighbors, and/or reactor, if appropriate for component type.
     */
    public void transfer() {
        // do nothing by default.
    }

    /**
     * Apply changes to the reactor when adding this component if appropriate, such as for reactor plating.
     */
    public void addToReactor() {
        // do nothing by default.
    }

    /**
     * Apply changes to the reactor when removing this component if appropriate, such as for reactor plating.
     */
    public void removeFromReactor() {
        parent = null;
        this.row = -10;
        this.col = -10;
    }

    /**
     * @return the current heat level of the component.
     */
    public final double getCurrentHeat() {
        return currentHeat;
    }

    /**
     * Resets heat to 0 (used when resetting simulation).
     */
    public final void clearCurrentHeat() {
        currentHeat = initialHeat;
        effectiveVentCooling = 0.0;
        bestCondensatorCooling = 0.0;
        bestCellCooling = 0.0;
    }

    /**
     * Adjusts the component heat up or down.
     * @param heat the amount of heat to adjust by (positive to add heat, negative to remove heat).
     * @return the amount of heat adjustment refused.
     */
    public double adjustCurrentHeat(final double heat) {
        if (isHeatAcceptor()) {
            double result = 0.0;
            double tempHeat = getCurrentHeat();
            tempHeat += heat;
            if (tempHeat > getMaxHeat()) {
                result = getMaxHeat() - tempHeat + 1;
                tempHeat = getMaxHeat();
            } else if (tempHeat < 0.0) {
                result = tempHeat;
                tempHeat = 0.0;
            }
            currentHeat = tempHeat;
            return result;
        }
        return heat;
    }

    /**
     * @return the maximum heat the component can take.
     */
    public double getMaxHeat() {
        return maxHeat;
    }

    /**
     * @return the damage the component has taken.
     */
    public final double getCurrentDamage() {
        return currentDamage;
    }

    /**
     * Clears the damage back to 0 (used when resetting simulation).
     */
    public final void clearDamage() {
        currentDamage = 0.0;
    }

    /**
     * Applies damage to the component, as opposed to heat.
     * @param damage the damage to apply (only used if positive).
     */
    public final void applyDamage(final double damage) {
        if (maxDamage > 1 && damage > 0.0) {
            currentDamage += damage;
        }
    }

    /**
     * @return the maximum damage the component can take.
     */
    public double getMaxDamage() {
        return maxDamage;
    }

    /**
     * Determines if this component is broken in the current tick of the simulation.
     */
    public boolean isBroken() {
        return currentHeat >= getMaxHeat() || currentDamage >= getMaxDamage();
    }

    public double getEffectiveVentCooling() {
        return effectiveVentCooling;
    }

    public double getVentCoolingCapacity() {
        return 0;
    }

    public double getHullCoolingCapacity() {
        return 0;
    }

    public double getBestCondensatorCooling() {
        return bestCondensatorCooling;
    }

    public double getBestCellCooling() {
        return bestCellCooling;
    }

    /**
     * The number of fuel rods in this component (0 for non-fuel-rod components).
     */
    public int getRodCount() {
        return 0;
    }

    /**
     * Determines if this is a condensator that needs a Reactor Coolant Injector item added.
     */
    public boolean needsCoolantInjected() {
        return false;
    }

    /**
     * Simulates having a coolant item added by a Reactor Coolant Injector.
     */
    public void injectCoolant() {
        // do nothing by default.
    }

    public double getExplosionPowerMultiplier() {
        return explosionPowerMultiplier;
    }
}
