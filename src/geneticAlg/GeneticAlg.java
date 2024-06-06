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
package geneticAlg;

import java.util.ArrayList;
import java.util.Collections;
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
public class GeneticAlg {

    private int POPULATION_SIZE, TOURNAMENT_SIZE, GENERATIONS, FREE_PASS, FRESH_BLOOD, MUTATION_CHANCE, MAX_GENREATIONS_WITHOUT_IMPROVEMENT; // x in a 1 000 000

    private final codeHelper ch;
    private final Random rnd;

    private ReactorEntity best, BEST;
    private ArrayList<ReactorEntity> population;

    /**
     *
     */
    public GeneticAlg() {
        try {
            FileReader fileReader = new FileReader("config.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" = ");
                switch (split[0]) {
                    case "POPULATION_SIZE":
                        this.POPULATION_SIZE = Integer.parseInt(split[1]);
                        break;
                    case "TOURNAMENT_SIZE":
                        this.TOURNAMENT_SIZE = Integer.parseInt(split[1]);
                        break;
                    case "GENERATIONS":
                        this.GENERATIONS = Integer.parseInt(split[1]);
                        break;
                    case "FREE_PASS":
                        this.FREE_PASS = Integer.parseInt(split[1]);
                        break;
                    case "FRESH_BLOOD":
                        this.FRESH_BLOOD = Integer.parseInt(split[1]);
                        break;
                    case "MUTATION_CHANCE":
                        this.MUTATION_CHANCE = Integer.parseInt(split[1]);
                        break;
                    case "MAX_GENREATIONS_WITHOUT_IMPROVEMENT":
                        this.MAX_GENREATIONS_WITHOUT_IMPROVEMENT = Integer.parseInt(split[1]);
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
            System.err.println("Using default config");
            POPULATION_SIZE = 100;
            TOURNAMENT_SIZE = 3;
            GENERATIONS = 1000;
            FREE_PASS = 1;
            FRESH_BLOOD = 15;
            MUTATION_CHANCE = 70000;
            MAX_GENREATIONS_WITHOUT_IMPROVEMENT = 50;
        }

        if (GENERATIONS == 0) {
            GENERATIONS = Integer.MAX_VALUE;
        }

        if (FREE_PASS == 0 || FRESH_BLOOD == 0 || MUTATION_CHANCE == 0 || POPULATION_SIZE == 0 || TOURNAMENT_SIZE == 0 || MAX_GENREATIONS_WITHOUT_IMPROVEMENT == 0) {
            System.err.println("Using default config");
            POPULATION_SIZE = 100;
            TOURNAMENT_SIZE = 3;
            GENERATIONS = 1000;
            FREE_PASS = 1;
            FRESH_BLOOD = 15;
            MUTATION_CHANCE = 70000;
            MAX_GENREATIONS_WITHOUT_IMPROVEMENT = 50;
        }

        ch = new codeHelper();
        rnd = new Random(System.currentTimeMillis());
        best = new ReactorEntity(ch.getRandomCode());
        population = new ArrayList<>(POPULATION_SIZE);
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new ReactorEntity(ch.getRandomCode()));
        }
        best.calculateFitness();
    }
    
    private void resetPopulation(){
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new ReactorEntity(ch.getRandomCode()));
        }
    }

    /**
     *
     */
    public void run() {
        
        int lastImproved = 0;

        for (int k = 0; k < GENERATIONS; k++) {

            population.stream().parallel().forEach(ReactorEntity::calculateFitness);

            Collections.sort(population);

            if (population.get(0).fitness > best.fitness) {
                lastImproved = 0;
                best = new ReactorEntity(population.get(0).reactor.getCode());
                best.calculateFitness();
                System.out.printf("Found new best! %f\n", best.fitness);
            }else{
                lastImproved++;
            }
            
            if(lastImproved >= MAX_GENREATIONS_WITHOUT_IMPROVEMENT) {
                System.out.println(lastImproved + " generations without improvement. Restarting population.");
//                resetPopulation();
//                lastImproved = 0;
            }

            ArrayList<ReactorEntity> newPop = new ArrayList<>(POPULATION_SIZE);

            for (int i = 0; i < FREE_PASS; i++) {
                newPop.add(new ReactorEntity(population.get(i).reactor.getCode()));
            }

            for (int i = 0; i < FRESH_BLOOD; i++) {
                newPop.add(new ReactorEntity(ch.getRandomCode()));
            }

            for (int i = FREE_PASS + FRESH_BLOOD; i < POPULATION_SIZE; i++) {
                ArrayList<ReactorEntity> tournament1 = new ArrayList<>(TOURNAMENT_SIZE);
                ArrayList<ReactorEntity> tournament2 = new ArrayList<>(TOURNAMENT_SIZE);

                for (int j = 0; j < TOURNAMENT_SIZE; j++) {
                    tournament1.add(population.get(rnd.nextInt(POPULATION_SIZE)));
                    tournament2.add(population.get(rnd.nextInt(POPULATION_SIZE)));
                }
                Collections.sort(tournament1);
                Collections.sort(tournament2);

                String childCode;

                if (rnd.nextBoolean()) {
                    childCode = ch.onePointCrossover(tournament1.get(0).reactor.getCode(), tournament2.get(0).reactor.getCode());
                } else {
                    childCode = ch.twoPointCrossover(tournament1.get(0).reactor.getCode(), tournament2.get(0).reactor.getCode());
                }

                int proc = rnd.nextInt(1000000);
                if (proc < MUTATION_CHANCE) {
                    childCode = ch.mutateGene(childCode);
                }

                newPop.add(new ReactorEntity(childCode));
            }
            System.out.printf("Just finished generation %d of %d with best fitness of %f, code: %s\n", k, GENERATIONS, population.get(0).fitness, population.get(0).reactor.getCode());
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
