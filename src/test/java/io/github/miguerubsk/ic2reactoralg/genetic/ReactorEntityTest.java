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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReactorEntityTest {

    private static final String EMPTY = "00".repeat(CodeHelper.GENES);

    private static ReactorEntity withFitness(double fitness) {
        ReactorEntity entity = new ReactorEntity(EMPTY);
        entity.fitness = fitness;
        return entity;
    }

    @Test
    void sortsBestFirst() {
        List<ReactorEntity> entities = new ArrayList<>(List.of(withFitness(3), withFitness(9), withFitness(-5), withFitness(7)));
        entities.sort(null);
        assertEquals(List.of(9.0, 7.0, 3.0, -5.0), entities.stream().map(e -> e.fitness).toList());
    }

    @Test
    void compareToIsConsistent() {
        ReactorEntity low = withFitness(1);
        ReactorEntity high = withFitness(2);
        assertTrue(high.compareTo(low) < 0);
        assertTrue(low.compareTo(high) > 0);
        assertEquals(0, withFitness(2).compareTo(high));
        assertEquals(0, high.compareTo(high));
    }

    /** Values recorded from the simulator, to notice unintended changes in the simulation or the fitness function. */
    @Test
    void fitnessMatchesRecordedValues() {
        assertFitness("10220E19061A1004230412211608021216061D0A060902181D110104211B06071F0B12210D221D2323061D080B0F1C1F1C0422011802", -18643.489498933897);
        assertFitness("1F011D0F0204081421111E0A0E0515140B02130F09221B010B1D1F1E0E0812040A05130A081B041A1922190202181F15171E130C2311", -7277.228287081342);
        assertFitness("04212204181F2113220A1A211B0F0B0D1912011E04090C0C0E081A011012120113181607211D21190A0408201E190B0A02201F0C131A", -562.2054350183093);
    }

    private static void assertFitness(String code, double expected) {
        ReactorEntity entity = new ReactorEntity(code);
        entity.calculateFitness();
        assertEquals(expected, entity.fitness, 1e-9);
    }

    @Test
    void calculatingTwiceGivesTheSameFitness() {
        ReactorEntity entity = new ReactorEntity("1F011D0F0204081421111E0A0E0515140B02130F09221B010B1D1F1E0E0812040A05130A081B041A1922190202181F15171E130C2311");
        entity.calculateFitness();
        double first = entity.fitness;
        entity.calculateFitness();
        assertEquals(first, entity.fitness);
    }

    @Test
    void reactorWithoutFuelRodsIsInvalidNotNaN() {
        ReactorEntity entity = new ReactorEntity(EMPTY);
        entity.calculateFitness();
        assertEquals(ReactorEntity.INVALID_FITNESS, entity.fitness);
    }

    @Test
    void reactorThatExplodesInTheFirstTickIsInvalidNotNaN() {
        ReactorEntity entity = new ReactorEntity("03".repeat(CodeHelper.GENES)); // only quad uranium rods
        entity.calculateFitness();
        assertFalse(Double.isNaN(entity.fitness));
        assertEquals(ReactorEntity.INVALID_FITNESS, entity.fitness);
    }

    @Test
    void freshCopyKeepsTheLayoutButNotTheResults() {
        ReactorEntity entity = new ReactorEntity("1F011D0F0204081421111E0A0E0515140B02130F09221B010B1D1F1E0E0812040A05130A081B041A1922190202181F15171E130C2311");
        entity.calculateFitness();
        ReactorEntity copy = entity.freshCopy();
        assertEquals(entity.reactor.getCode(), copy.reactor.getCode());
        assertEquals(0.0, copy.fitness);
    }
}
