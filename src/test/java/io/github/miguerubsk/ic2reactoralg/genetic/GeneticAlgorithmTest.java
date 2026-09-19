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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GeneticAlgorithmTest {

    private static final String EMPTY = "00".repeat(CodeHelper.GENES);

    private static final GeneticConfig SMALL = new GeneticConfig(24, 3, 6, 1, 4, 70_000, 50);

    private static ReactorEntity withFitness(double fitness) {
        ReactorEntity entity = new ReactorEntity(EMPTY);
        entity.fitness = fitness;
        return entity;
    }

    @Test
    void tournamentWinnerIsTheFittestContestant() {
        ReactorEntity best = withFitness(9);
        assertSame(best, GeneticAlgorithm.tournamentWinner(List.of(withFitness(2), best, withFitness(5))));
    }

    @Test
    void populationIsSortedBestFirstAfterEvaluation() {
        GeneticAlgorithm algorithm = new GeneticAlgorithm(SMALL, new Random(7));
        algorithm.evaluateAndSort();

        List<ReactorEntity> population = algorithm.population();
        assertEquals(SMALL.populationSize(), population.size());
        for (int i = 1; i < population.size(); i++) {
            assertTrue(population.get(i - 1).fitness >= population.get(i).fitness,
                    "population not sorted at index " + i);
        }
    }

    @Test
    void runReturnsABestWhoseFitnessIsConsistentWithItsLayout() {
        ReactorEntity best = new GeneticAlgorithm(SMALL, new Random(7)).run();
        assertNotNull(best);

        ReactorEntity recomputed = new ReactorEntity(best.reactor.getCode());
        recomputed.calculateFitness();
        assertEquals(recomputed.fitness, best.fitness, 1e-9);
    }
}
