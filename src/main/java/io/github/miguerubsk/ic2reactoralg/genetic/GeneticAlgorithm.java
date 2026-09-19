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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Genetic algorithm that searches for the best reactor layout.
 *
 * @author Miguel González García
 */
public class GeneticAlgorithm {

    /** Configuration file read from the current working directory. */
    public static final String CONFIG_FILE = "config.txt";

    private final GeneticConfig config;
    private final Random random;
    private final CodeHelper codeHelper;

    private ReactorEntity best;
    private List<ReactorEntity> population;

    /**
     * Creates the algorithm using the parameters in {@value #CONFIG_FILE}.
     */
    public GeneticAlgorithm() {
        this(GeneticConfig.load(Path.of(CONFIG_FILE)), new Random());
    }

    GeneticAlgorithm(GeneticConfig config, Random random) {
        this.config = config;
        this.random = random;
        this.codeHelper = new CodeHelper(random);
        this.best = randomEntity();
        best.calculateFitness();
        this.population = randomPopulation(0);
    }

    private ReactorEntity randomEntity() {
        return new ReactorEntity(codeHelper.getRandomCode());
    }

    /**
     * Builds a population of random entities, preceded by the given number of already known good ones.
     */
    private List<ReactorEntity> randomPopulation(int alreadyPresent) {
        List<ReactorEntity> result = new ArrayList<>(config.populationSize());
        for (int i = alreadyPresent; i < config.populationSize(); i++) {
            result.add(randomEntity());
        }
        return result;
    }

    /**
     * Restarts the population, keeping only the best individual found so far.
     */
    private void resetPopulation() {
        List<ReactorEntity> restarted = new ArrayList<>(config.populationSize());
        restarted.add(best.freshCopy());
        restarted.addAll(randomPopulation(1));
        population = restarted;
    }

    /**
     * Evaluates the whole population and leaves it sorted best first.
     */
    void evaluateAndSort() {
        population.parallelStream().forEach(ReactorEntity::calculateFitness);
        Collections.sort(population);
    }

    List<ReactorEntity> population() {
        return population;
    }

    /**
     * @return the fittest of the given contestants.
     */
    static ReactorEntity tournamentWinner(List<ReactorEntity> contestants) {
        return Collections.min(contestants);
    }

    private ReactorEntity selectParent() {
        List<ReactorEntity> contestants = new ArrayList<>(config.tournamentSize());
        for (int i = 0; i < config.tournamentSize(); i++) {
            contestants.add(population.get(random.nextInt(population.size())));
        }
        return tournamentWinner(contestants);
    }

    private String breedChild() {
        String parent1 = selectParent().reactor.getCode();
        String parent2 = selectParent().reactor.getCode();

        String child = switch (random.nextInt(4)) {
            case 0 -> codeHelper.twoPointCrossover(parent1, parent2);
            case 1 -> codeHelper.onePointCrossover(parent1, parent2);
            default -> codeHelper.uniformCrossover(parent1, parent2);
        };

        if (random.nextInt(GeneticConfig.MUTATION_SCALE) < config.mutationChance()) {
            child = codeHelper.mutateGene(child);
        }
        return child;
    }

    /**
     * Builds the next generation from the (already sorted) current population.
     */
    private List<ReactorEntity> nextGeneration() {
        List<ReactorEntity> next = new ArrayList<>(config.populationSize());

        for (int i = 0; i < config.freePass(); i++) {
            next.add(population.get(i).freshCopy());
        }
        for (int i = 0; i < config.freshBlood(); i++) {
            next.add(randomEntity());
        }
        while (next.size() < config.populationSize()) {
            next.add(new ReactorEntity(breedChild()));
        }
        return next;
    }

    /**
     * Runs the algorithm for the configured number of generations.
     *
     * @return the best individual found.
     */
    public ReactorEntity run() {
        int generationsWithoutImprovement = 0;

        for (int generation = 0; generation < config.generations(); generation++) {
            evaluateAndSort();
            ReactorEntity top = population.get(0);

            if (top.fitness > best.fitness) {
                generationsWithoutImprovement = 0;
                best = top.freshCopy();
                best.calculateFitness();
                System.out.printf("Found new best! %f%n", best.fitness);
            } else {
                generationsWithoutImprovement++;
            }

            System.out.printf("Just finished generation %d of %d with best fitness of %f, code: %s%n",
                    generation, config.generations(), top.fitness, top.reactor.getCode());

            // With no improvement for too long, restart the population and skip breeding this iteration.
            if (generationsWithoutImprovement >= config.maxGenerationsWithoutImprovement()) {
                System.out.println(generationsWithoutImprovement + " generations without improvement. Restarting population.");
                resetPopulation();
                generationsWithoutImprovement = 0;
                continue;
            }

            population = nextGeneration();
        }

        return best;
    }
}
