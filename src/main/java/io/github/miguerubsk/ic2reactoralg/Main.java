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
package io.github.miguerubsk.ic2reactoralg;

import io.github.miguerubsk.ic2reactoralg.genetic.GeneticAlgorithm;
import io.github.miguerubsk.ic2reactoralg.genetic.ReactorEntity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Miguel González García
 */
public class Main {

    private static final Path RESULT_FILE = Path.of("result.txt");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ReactorEntity best = new GeneticAlgorithm().run();

        System.out.printf("Best found(%f) reactor was: %s%n", best.fitness, best.reactor.getCode());
        try {
            Files.writeString(RESULT_FILE, "Best found(" + best.fitness + ") reactor was: " + best.reactor.getCode());
        } catch (IOException e) {
            System.err.println("Could not write " + RESULT_FILE + ": " + e);
        }
    }

}
