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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.miguerubsk.ic2reactoralg.simulator.ComponentFactory;
import java.util.Random;
import org.junit.jupiter.api.Test;

class CodeHelperTest {

    private static final int TRIALS = 2000;

    private final CodeHelper helper = new CodeHelper(new Random(1));

    private static String gene(String code, int index) {
        return code.substring(index * CodeHelper.GENE_LENGTH, (index + 1) * CodeHelper.GENE_LENGTH);
    }

    private static void assertValidGenes(String code) {
        assertEquals(CodeHelper.CODE_LENGTH, code.length());
        for (int i = 0; i < CodeHelper.GENES; i++) {
            int id = Integer.parseInt(gene(code, i), 16);
            assertTrue(id >= 1 && id < ComponentFactory.getComponentCount(), "invalid gene " + gene(code, i) + " at " + i);
        }
    }

    /** Two parents whose genes differ in both hex digits, so a gene split in half would match neither parent. */
    private String parentOf(int id) {
        return String.format("%02X", id).repeat(CodeHelper.GENES);
    }

    @Test
    void randomCodeHasOnlyValidGenes() {
        for (int i = 0; i < 100; i++) {
            assertValidGenes(helper.getRandomCode());
        }
    }

    @Test
    void mutationChangesAtMostOneGeneAndCanReachEveryCell() {
        String code = parentOf(1);
        boolean[] mutated = new boolean[CodeHelper.GENES];
        for (int t = 0; t < TRIALS; t++) {
            String result = helper.mutateGene(code);
            assertValidGenes(result);
            int differing = 0;
            for (int i = 0; i < CodeHelper.GENES; i++) {
                if (!gene(result, i).equals(gene(code, i))) {
                    differing++;
                    mutated[i] = true;
                }
            }
            assertTrue(differing <= 1);
        }
        for (int i = 0; i < mutated.length; i++) {
            assertTrue(mutated[i], "gene " + i + " was never mutated");
        }
    }

    @Test
    void crossoversOnlyProduceGenesFromTheParentsAtTheSamePosition() {
        String parent1 = parentOf(0x0A);
        String parent2 = parentOf(0x1F);
        for (int t = 0; t < TRIALS; t++) {
            for (String child : new String[] {
                helper.onePointCrossover(parent1, parent2),
                helper.twoPointCrossover(parent1, parent2),
                helper.uniformCrossover(parent1, parent2)}) {
                assertEquals(CodeHelper.CODE_LENGTH, child.length());
                for (int i = 0; i < CodeHelper.GENES; i++) {
                    String gene = gene(child, i);
                    assertTrue(gene.equals(gene(parent1, i)) || gene.equals(gene(parent2, i)), "hybrid gene " + gene);
                }
            }
        }
    }

    @Test
    void crossoversMixBothParents() {
        String parent1 = parentOf(0x0A);
        String parent2 = parentOf(0x1F);
        for (int t = 0; t < TRIALS; t++) {
            assertMixed(helper.onePointCrossover(parent1, parent2), parent1, parent2);
            assertMixed(helper.twoPointCrossover(parent1, parent2), parent1, parent2);
        }
    }

    private static void assertMixed(String child, String parent1, String parent2) {
        // one/two point crossover must always take something from each parent
        assertNotEquals(parent1, child);
        assertNotEquals(parent2, child);
    }

    @Test
    void operatorsRejectCodesOfTheWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> helper.mutateGene("0A"));
        assertThrows(IllegalArgumentException.class, () -> helper.onePointCrossover("0A", "0B"));
    }
}
