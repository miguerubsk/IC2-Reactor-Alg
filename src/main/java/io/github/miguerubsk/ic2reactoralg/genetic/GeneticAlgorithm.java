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

import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author Miguel González García
 */
public class GeneticAlgorithm {

    private int populationSize, tournamentSize, generations, freePass, freshBlood, mutationChance, maxGenerationsWithoutImprovement; // mutationChance is x in 1 000 000

    private final CodeHelper codeHelper;
    private final Random random;

    private ReactorEntity best;
    private ArrayList<ReactorEntity> population;
    private final MergeSort sort;

    /**
     *
     */
    public GeneticAlgorithm() {
        try {
            FileReader fileReader = new FileReader("config.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" = ");
                switch (split[0]) {
                    case "POPULATION_SIZE":
                        this.populationSize = Integer.parseInt(split[1]);
                        break;
                    case "TOURNAMENT_SIZE":
                        this.tournamentSize = Integer.parseInt(split[1]);
                        break;
                    case "GENERATIONS":
                        this.generations = Integer.parseInt(split[1]);
                        break;
                    case "FREE_PASS":
                        this.freePass = Integer.parseInt(split[1]);
                        break;
                    case "FRESH_BLOOD":
                        this.freshBlood = Integer.parseInt(split[1]);
                        break;
                    case "MUTATION_CHANCE":
                        this.mutationChance = Integer.parseInt(split[1]);
                        break;
                    case "MAX_GENREATIONS_WITHOUT_IMPROVEMENT":
                        this.maxGenerationsWithoutImprovement = Integer.parseInt(split[1]);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
            System.err.println("Using default config");
            populationSize = 100;
            tournamentSize = 3;
            generations = 1000;
            freePass = 1;
            freshBlood = 15;
            mutationChance = 70000;
            maxGenerationsWithoutImprovement = 50;
        }

        if (generations == 0) {
            generations = Integer.MAX_VALUE;
        }

        if (freePass == 0 || freshBlood == 0 || mutationChance == 0 || populationSize == 0 || tournamentSize == 0 || maxGenerationsWithoutImprovement == 0) {
            System.err.println("Using default config");
            populationSize = 100;
            tournamentSize = 3;
            generations = 1000;
            freePass = 1;
            freshBlood = 15;
            mutationChance = 70000;
            maxGenerationsWithoutImprovement = 50;
        }

        sort = new MergeSort();
        codeHelper = new CodeHelper();
        random = new Random(System.currentTimeMillis());
        best = new ReactorEntity(codeHelper.getRandomCode());
        population = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            population.add(new ReactorEntity(codeHelper.getRandomCode()));
        }
        best.calculateFitness();
    }

    /**
     * Reinicia la población manteniendo únicamente al mejor individuo histórico.
     */
    private void resetPopulation() {
        population.clear();
        // Preservamos al mejor individuo global
        population.add(new ReactorEntity(best.reactor.getCode()));
        
        // El resto se genera aleatoriamente
        for (int i = 1; i < populationSize; i++) {
            population.add(new ReactorEntity(codeHelper.getRandomCode()));
        }
    }

    /**
     *
     */
    public void run() {

        int lastImproved = 0;

        for (int k = 0; k < generations; k++) {

            population.stream().parallel().forEach(ReactorEntity::calculateFitness);

            sort.mergeSort(population, 0, population.size() - 1);

            if (population.get(0).fitness > best.fitness) {
                lastImproved = 0;
                best = new ReactorEntity(population.get(0).reactor.getCode());
                best.calculateFitness();
                System.out.printf("Found new best! %f\n", best.fitness);
            } else {
                lastImproved++;
            }

            // Si se alcanza el límite sin mejoras, se reinicia la población y se salta el cruce en esta iteración
            if (lastImproved >= maxGenerationsWithoutImprovement) {
                System.out.println(lastImproved + " generations without improvement. Restarting population.");
                resetPopulation();
                lastImproved = 0;
                System.out.printf("Just finished generation %d of %d with best fitness of %f, code: %s\n", k, generations, population.get(0).fitness, population.get(0).reactor.getCode());
                continue; 
            }

            ArrayList<ReactorEntity> newPop = new ArrayList<>(populationSize);

            for (int i = 0; i < freePass; i++) {
                newPop.add(new ReactorEntity(population.get(i).reactor.getCode()));
            }

            for (int i = 0; i < freshBlood; i++) {
                newPop.add(new ReactorEntity(codeHelper.getRandomCode()));
            }

            for (int i = freePass + freshBlood; i < populationSize; i++) {
                ArrayList<ReactorEntity> tournament1 = new ArrayList<>(tournamentSize);
                ArrayList<ReactorEntity> tournament2 = new ArrayList<>(tournamentSize);

                for (int j = 0; j < tournamentSize; j++) {
                    tournament1.add(population.get(random.nextInt(populationSize)));
                    tournament2.add(population.get(random.nextInt(populationSize)));
                }
                sort.mergeSort(tournament1, 0, tournament1.size() - 1);
                sort.mergeSort(tournament2, 0, tournament2.size() - 1);
                String childCode;

                if (random.nextBoolean()) {

                    if (random.nextBoolean()) {
//                      System.out.println("Using 1PX");
                        childCode = codeHelper.twoPointCrossover(tournament1.get(0).reactor.getCode(), tournament2.get(0).reactor.getCode());
                    } else {
//                      System.out.println("Using 2PX");
                        childCode = codeHelper.onePointCrossover(tournament1.get(0).reactor.getCode(), tournament2.get(0).reactor.getCode());
                    }
                } else {
//                  System.out.println("Using UX");
                    childCode = codeHelper.uniformCrossover(tournament1.get(0).reactor.getCode(), tournament2.get(0).reactor.getCode());
                }

                int proc = random.nextInt(1000000);
                if (proc < mutationChance) {
                    childCode = codeHelper.mutateGene(childCode);
                }

                newPop.add(new ReactorEntity(childCode));
            }
            System.out.printf("Just finished generation %d of %d with best fitness of %f, code: %s\n", k, generations, population.get(0).fitness, population.get(0).reactor.getCode());
            population = newPop;
        }

        System.out.printf("Best found(%f) reactor was: %s", best.fitness, best.reactor.getCode());

        File file = new File("result.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

            try (FileWriter fileWriter = new FileWriter("result.txt")) {
                PrintWriter pw = new PrintWriter(fileWriter);

                pw.write("Best found(" + best.fitness + ") reactor was: " + best.reactor.getCode());
            }

        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}