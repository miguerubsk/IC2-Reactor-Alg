package Simulator;

import java.awt.Image;

/**
 * Represents a condensator in a reactor, either RSH or LZH.
 * Ported from Ic2ExpReactorPlanner.components.Condensator (generic, data-driven design).
 * @author Brian McCloud (original), ported for IC2-Reactor-Alg
 */
public class Condensator extends ReactorComponent {

    public Condensator(final int id, final String baseName, final String name, final Image image,
            final double maxDamage, final double maxHeat, final String sourceMod) {
        super(id, baseName, name, image, maxDamage, maxHeat, sourceMod);
    }

    public Condensator(final Condensator other) {
        super(other);
    }

    @Override
    public double adjustCurrentHeat(final double heat) {
        if (heat < 0.0) {
            return heat;
        }
        currentCondensatorCooling += heat;
        bestCondensatorCooling = Math.max(currentCondensatorCooling, bestCondensatorCooling);
        double acceptedHeat = Math.min(heat, getMaxHeat() - heat);
        double result = heat - acceptedHeat;
        currentHeat += acceptedHeat;
        return result;
    }

    @Override
    public boolean needsCoolantInjected() {
        return currentHeat > 0.85 * getMaxHeat();
    }

    @Override
    public void injectCoolant() {
        currentHeat = 0;
    }
}
