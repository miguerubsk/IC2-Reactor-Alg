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

import io.github.miguerubsk.ic2reactoralg.simulator.ComponentFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Creates and recombines reactor codes. A code is the concatenation of one gene (two hex characters holding a
 * component id) per grid cell, so every operator works on whole genes and never splits one in half.
 *
 * @author Miguel González García
 */
public class CodeHelper {

    /** Hex characters per gene (one grid cell). */
    public static final int GENE_LENGTH = 2;

    /** Number of genes in a code (6 rows x 9 columns). */
    public static final int GENES = 54;

    public static final int CODE_LENGTH = GENES * GENE_LENGTH;

    private final Random random;
    private final List<String> validIds = createIds();

    public CodeHelper(Random random) {
        this.random = random;
    }

    private static List<String> createIds() {
        List<String> result = new ArrayList<>();
        // id 0 is reserved for "empty" (no component); valid ids run from 1 to
        // ComponentFactory.getComponentCount() - 1, matching the 2-hex-char encoding.
        for (int i = 1; i < ComponentFactory.getComponentCount(); i++) {
            result.add(String.format("%02X", i));
        }
        return result;
    }

    private static String gene(String code, int index) {
        return code.substring(index * GENE_LENGTH, (index + 1) * GENE_LENGTH);
    }

    private static void requireValidCode(String code) {
        if (code.length() != CODE_LENGTH) {
            throw new IllegalArgumentException("Expected a code of " + CODE_LENGTH + " characters, got " + code.length());
        }
    }

    /**
     * @return the hex representation of a random valid component id.
     */
    public String getRandomId() {
        return validIds.get(random.nextInt(validIds.size()));
    }

    /**
     * @return a code with a random valid component in every cell.
     */
    public String getRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < GENES; i++) {
            sb.append(getRandomId());
        }
        return sb.toString();
    }

    /**
     * Replaces one randomly chosen gene (any of the {@value #GENES}) with a random component id.
     *
     * @param code the code to mutate.
     * @return the mutated code.
     */
    public String mutateGene(String code) {
        requireValidCode(code);
        int index = random.nextInt(GENES);
        return code.substring(0, index * GENE_LENGTH) + getRandomId() + code.substring((index + 1) * GENE_LENGTH);
    }

    /**
     * The child takes the genes before a random cut point from the first parent and the rest from the second.
     */
    public String onePointCrossover(String code1, String code2) {
        requireValidCode(code1);
        requireValidCode(code2);
        int cut = 1 + random.nextInt(GENES - 1);
        return code1.substring(0, cut * GENE_LENGTH) + code2.substring(cut * GENE_LENGTH);
    }

    /**
     * A random, non-empty run of genes comes from one parent and the genes around it from the other; the run
     * never covers the whole code, so the child always takes something from both parents.
     */
    public String twoPointCrossover(String code1, String code2) {
        requireValidCode(code1);
        requireValidCode(code2);
        int from = random.nextInt(GENES);
        int lastEnd = from == 0 ? GENES - 1 : GENES;
        int to = from + 1 + random.nextInt(lastEnd - from);

        String outer = code1;
        String inner = code2;
        if (random.nextBoolean()) {
            outer = code2;
            inner = code1;
        }
        return outer.substring(0, from * GENE_LENGTH)
                + inner.substring(from * GENE_LENGTH, to * GENE_LENGTH)
                + outer.substring(to * GENE_LENGTH);
    }

    /**
     * Every gene of the child comes from either parent with the same probability.
     */
    public String uniformCrossover(String code1, String code2) {
        requireValidCode(code1);
        requireValidCode(code2);
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < GENES; i++) {
            sb.append(random.nextBoolean() ? gene(code1, i) : gene(code2, i));
        }
        return sb.toString();
    }
}
