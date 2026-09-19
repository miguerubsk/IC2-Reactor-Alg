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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GeneticConfigTest {

    @TempDir
    Path dir;

    private GeneticConfig load(String content) throws IOException {
        Path file = dir.resolve("config.txt");
        Files.writeString(file, content);
        return GeneticConfig.load(file);
    }

    @Test
    void missingFileGivesDefaults() {
        assertSame(GeneticConfig.DEFAULT, GeneticConfig.load(dir.resolve("nope.txt")));
    }

    @Test
    void readsEveryKey() throws IOException {
        GeneticConfig config = load("""
                POPULATION_SIZE = 40
                TOURNAMENT_SIZE = 4
                GENERATIONS = 25
                FREE_PASS = 2
                FRESH_BLOOD = 6
                MUTATION_CHANCE = 1234
                MAX_GENERATIONS_WITHOUT_IMPROVEMENT = 7
                """);
        assertEquals(new GeneticConfig(40, 4, 25, 2, 6, 1234, 7), config);
    }

    /** The repository's config.txt has no MAX_GENERATIONS_WITHOUT_IMPROVEMENT; that used to discard every other value. */
    @Test
    void missingKeyOnlyAffectsThatKey() throws IOException {
        GeneticConfig config = load("""
                POPULATION_SIZE = 100
                TOURNAMENT_SIZE = 3
                GENERATIONS = 10
                FREE_PASS = 1
                FRESH_BLOOD = 15
                MUTATION_CHANCE = 70000
                """);
        assertEquals(10, config.generations());
        assertEquals(GeneticConfig.DEFAULT.maxGenerationsWithoutImprovement(), config.maxGenerationsWithoutImprovement());
    }

    @Test
    void legacyMisspelledKeyStillWorks() throws IOException {
        assertEquals(9, load("MAX_GENREATIONS_WITHOUT_IMPROVEMENT = 9").maxGenerationsWithoutImprovement());
    }

    @Test
    void zeroGenerationsMeansUnlimited() throws IOException {
        assertEquals(Integer.MAX_VALUE, load("GENERATIONS = 0").generations());
    }

    @Test
    void toleratesWhitespaceCommentsAndUnknownKeys() throws IOException {
        GeneticConfig config = load("# comment\nPOPULATION_SIZE =  30  \nSOMETHING_ELSE = 1\n");
        assertEquals(30, config.populationSize());
    }

    @Test
    void rejectsNonNumericValue() {
        assertThrows(IllegalArgumentException.class, () -> load("POPULATION_SIZE = many"));
    }

    @Test
    void rejectsOutOfRangeValues() {
        assertThrows(IllegalArgumentException.class, () -> load("POPULATION_SIZE = 0"));
        assertThrows(IllegalArgumentException.class, () -> load("TOURNAMENT_SIZE = 0"));
        assertThrows(IllegalArgumentException.class, () -> load("MUTATION_CHANCE = 1000001"));
        assertThrows(IllegalArgumentException.class, () -> load("MAX_GENERATIONS_WITHOUT_IMPROVEMENT = 0"));
    }

    @Test
    void rejectsElitismAndFreshBloodLargerThanPopulation() {
        assertThrows(IllegalArgumentException.class, () -> load("POPULATION_SIZE = 10\nFREE_PASS = 1\nFRESH_BLOOD = 15"));
    }
}
