/*
 * Copyright (C) 2024 Miguel González García
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.miguerubsk.ic2reactoralg.genetic;

import io.github.miguerubsk.ic2reactoralg.simulator.FuelRod;
import io.github.miguerubsk.ic2reactoralg.simulator.Reactor;
import io.github.miguerubsk.ic2reactoralg.simulator.ReactorComponent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A candidate reactor layout together with the results of simulating it.
 * Entities are ordered best first (highest fitness first).
 *
 * @author Miguel González García
 */
public class ReactorEntity implements Comparable<ReactorEntity> {

    /** Fitness of reactors that never produced a full reactor tick of output (no rods, or exploding at once). */
    public static final double INVALID_FITNESS = Double.NEGATIVE_INFINITY;

    private static final Logger LOGGER = Logger.getLogger(ReactorEntity.class.getName());

    public final Reactor reactor;

    public final boolean[][] alreadyBroken = new boolean[6][9];

    public final boolean[][] needsCooldown = new boolean[6][9];

    public final int initialHeat = 0;

    public double minEuOutput = Double.MAX_VALUE;

    public double maxEuOutput = 0.0;

    public double minHeatOutput = Double.MAX_VALUE;

    public double maxHeatOutput = 0.0;

    public int brokenComponents = 0;

    public double leftoverHeat = 0;

    public double avgEfficiency = 0;

    public double avgEuOutput = 0;

    public double fitness = 0;

    public String code;

    private int reactorTicks = 0;
    private boolean simulationFailed = false;

    boolean reachedBurn = false;
    boolean reachedEvaporate = false;
    boolean reachedHurt = false;
    boolean reachedLava = false;
    boolean reachedExplode = false;

    /**
     * @param code the reactor code (see {@link Reactor#setCode(String)}).
     */
    public ReactorEntity(String code) {
        this.reactor = new Reactor();
        reactor.setCode(code);
        this.code = code;
    }

    /**
     * @return a new, not yet evaluated entity with the same layout.
     */
    public ReactorEntity freshCopy() {
        return new ReactorEntity(reactor.getCode());
    }

    /**
     * Simulates the reactor and computes its fitness. Can be called more than once; every call starts from scratch.
     */
    public void calculateFitness() {
        resetResults();
        runSimulation();

        if (simulationFailed || reactorTicks == 0) {
            fitness = INVALID_FITNESS;
            return;
        }

        fitness += avgEuOutput * 6;
        fitness += avgEfficiency * 10;
        if (leftoverHeat > 0) {
            fitness -= leftoverHeat;
            fitness -= 500;
        }
        fitness -= brokenComponents * 100;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                if (needsCooldown[i][j]) {
                    fitness -= 5;//dont know bout this
                }
            }
        }

        if (reachedBurn) {
            fitness -= 150;
            fitness -= avgEuOutput;
        }

        if (reachedEvaporate) {
            fitness -= 300;
            fitness -= avgEuOutput * 2;
        }

        if (reachedHurt) {
            fitness -= 500;
            fitness -= avgEuOutput * 3;
        }

        if (reachedLava) {
            fitness -= 800;
            fitness -= avgEuOutput * 4;
        }

        if (reachedExplode) {
            fitness -= 1000;
            fitness -= avgEuOutput * 5;
        }
    }

    private void resetResults() {
        for (int i = 0; i < 6; i++) {
            Arrays.fill(alreadyBroken[i], false);
            Arrays.fill(needsCooldown[i], false);
        }
        minEuOutput = Double.MAX_VALUE;
        maxEuOutput = 0.0;
        minHeatOutput = Double.MAX_VALUE;
        maxHeatOutput = 0.0;
        brokenComponents = 0;
        leftoverHeat = 0;
        avgEfficiency = 0;
        avgEuOutput = 0;
        fitness = 0;
        reactorTicks = 0;
        simulationFailed = false;
        reachedBurn = false;
        reachedEvaporate = false;
        reachedHurt = false;
        reachedLava = false;
        reachedExplode = false;
    }

    private void runSimulation() {
        int totalRodCount = 0;
        try {
            reactor.setCurrentHeat(initialHeat);
            reactor.clearVentedHeat();
            double maxReactorHeat = initialHeat;
            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 9; col++) {
                    ReactorComponent component = reactor.getComponentAt(row, col);
                    if (component != null) {
                        component.clearCurrentHeat();
                        component.clearDamage();
                        totalRodCount += component.getRodCount();
                    }
                }
            }
            double lastEuOutput;
            double totalEuOutput = 0.0;
            do {
                reactor.clearEuOutput();
                reactor.clearVentedHeat();
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 9; col++) {
                        ReactorComponent component = reactor.getComponentAt(row, col);
                        if (component != null) {
                            component.preReactorTick();
                        }
                    }
                }
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 9; col++) {
                        ReactorComponent component = reactor.getComponentAt(row, col);
                        if (component != null && !component.isBroken()) {
                            component.generateHeat();
                            maxReactorHeat = Math.max(reactor.getCurrentHeat(), maxReactorHeat);
                            component.dissipate();
                            maxReactorHeat = Math.max(reactor.getCurrentHeat(), maxReactorHeat);
                            component.transfer();
                            maxReactorHeat = Math.max(reactor.getCurrentHeat(), maxReactorHeat);
                        }
                    }
                }
                updateHeatThresholds(maxReactorHeat);
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 9; col++) {
                        ReactorComponent component = reactor.getComponentAt(row, col);
                        if (component != null && !component.isBroken()) {
                            component.generateEnergy();
                        }
                    }
                }
                lastEuOutput = reactor.getCurrentEuOutput();
                totalEuOutput += lastEuOutput;
                double lastHeatOutput = reactor.getVentedHeat();
                if (reactor.getCurrentHeat() <= reactor.getMaxHeat() && lastEuOutput > 0.0) {
                    reactorTicks++;
                    minEuOutput = Math.min(lastEuOutput, minEuOutput);
                    maxEuOutput = Math.max(lastEuOutput, maxEuOutput);
                    minHeatOutput = Math.min(lastHeatOutput, minHeatOutput);
                    maxHeatOutput = Math.max(lastHeatOutput, maxHeatOutput);
                }
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 9; col++) {
                        ReactorComponent component = reactor.getComponentAt(row, col);
                        if (component != null && component.isBroken() && !alreadyBroken[row][col] && !(component instanceof FuelRod)) {
                            alreadyBroken[row][col] = true;
                            brokenComponents++;
                        }
                    }
                }
            } while (reactor.getCurrentHeat() <= reactor.getMaxHeat() && lastEuOutput > 0.0);

            if (reactorTicks > 0) {
                avgEuOutput = totalEuOutput / (reactorTicks * 20);
            }
            if (reactor.getCurrentHeat() <= reactor.getMaxHeat()) {
                if (reactorTicks > 0 && !reactor.isFluid() && totalRodCount > 0) {
                    avgEfficiency = totalEuOutput / reactorTicks / 100 / totalRodCount;
                }
                leftoverHeat = reactor.getCurrentHeat();
                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 9; col++) {
                        ReactorComponent component = reactor.getComponentAt(row, col);
                        if (component != null && !component.isBroken() && component.getCurrentHeat() > 0.0) {
                            needsCooldown[row][col] = true;
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            simulationFailed = true;
            LOGGER.log(Level.SEVERE, e, () -> "Simulation of reactor " + code + " failed at reactor tick " + reactorTicks);
        }
    }

    private void updateHeatThresholds(double maxReactorHeat) {
        double maxHeat = reactor.getMaxHeat();
        reachedBurn |= maxReactorHeat >= 0.4 * maxHeat;
        reachedEvaporate |= maxReactorHeat >= 0.5 * maxHeat;
        reachedHurt |= maxReactorHeat >= 0.7 * maxHeat;
        reachedLava |= maxReactorHeat >= 0.85 * maxHeat;
        reachedExplode |= maxReactorHeat >= maxHeat;
    }

    /**
     * Orders entities best first: higher fitness sorts before lower fitness.
     */
    @Override
    public int compareTo(ReactorEntity other) {
        return Double.compare(other.fitness, this.fitness);
    }
}
