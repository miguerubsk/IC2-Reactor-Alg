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

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Parameters of the genetic algorithm.
 *
 * @param populationSize number of individuals per generation.
 * @param tournamentSize number of individuals drawn for each parent selection tournament.
 * @param generations number of generations to run; 0 means "run until stopped" (stored as {@link Integer#MAX_VALUE}).
 * @param freePass number of best individuals copied unchanged into the next generation.
 * @param freshBlood number of brand new random individuals added to each generation.
 * @param mutationChance probability of mutating a child, as x in {@value #MUTATION_SCALE}.
 * @param maxGenerationsWithoutImprovement generations without a new best before the population is restarted.
 */
public record GeneticConfig(int populationSize, int tournamentSize, int generations, int freePass, int freshBlood,
        int mutationChance, int maxGenerationsWithoutImprovement) {

    public static final int MUTATION_SCALE = 1_000_000;

    public static final GeneticConfig DEFAULT = new GeneticConfig(100, 3, 1000, 1, 15, 70_000, 50);

    private static final Logger LOGGER = Logger.getLogger(GeneticConfig.class.getName());

    // Legacy spelling of the last key, kept so old config files keep working.
    private static final String LEGACY_MAX_WITHOUT_IMPROVEMENT = "MAX_GENREATIONS_WITHOUT_IMPROVEMENT";
    private static final String MAX_WITHOUT_IMPROVEMENT = "MAX_GENERATIONS_WITHOUT_IMPROVEMENT";

    private static final Set<String> KNOWN_KEYS = Set.of("POPULATION_SIZE", "TOURNAMENT_SIZE", "GENERATIONS",
            "FREE_PASS", "FRESH_BLOOD", "MUTATION_CHANCE", MAX_WITHOUT_IMPROVEMENT, LEGACY_MAX_WITHOUT_IMPROVEMENT);

    public GeneticConfig {
        if (populationSize < 1) {
            throw new IllegalArgumentException("POPULATION_SIZE must be at least 1, got " + populationSize);
        }
        if (tournamentSize < 1) {
            throw new IllegalArgumentException("TOURNAMENT_SIZE must be at least 1, got " + tournamentSize);
        }
        if (generations < 0) {
            throw new IllegalArgumentException("GENERATIONS must not be negative, got " + generations);
        }
        if (freePass < 0 || freshBlood < 0) {
            throw new IllegalArgumentException("FREE_PASS and FRESH_BLOOD must not be negative");
        }
        if ((long) freePass + freshBlood > populationSize) {
            throw new IllegalArgumentException("FREE_PASS + FRESH_BLOOD (" + (freePass + freshBlood)
                    + ") must not exceed POPULATION_SIZE (" + populationSize + ")");
        }
        if (mutationChance < 0 || mutationChance > MUTATION_SCALE) {
            throw new IllegalArgumentException("MUTATION_CHANCE must be between 0 and " + MUTATION_SCALE
                    + ", got " + mutationChance);
        }
        if (maxGenerationsWithoutImprovement < 1) {
            throw new IllegalArgumentException("MAX_GENERATIONS_WITHOUT_IMPROVEMENT must be at least 1, got "
                    + maxGenerationsWithoutImprovement);
        }
        if (generations == 0) {
            generations = Integer.MAX_VALUE;
        }
    }

    /**
     * Loads the configuration from a {@code KEY = VALUE} file. Keys that are missing keep their default value;
     * if the file does not exist the whole default configuration is used.
     *
     * @param file the configuration file.
     * @return the configuration.
     * @throws IllegalArgumentException if a value is not a number or is out of range.
     * @throws UncheckedIOException if the file exists but cannot be read.
     */
    public static GeneticConfig load(Path file) {
        if (!Files.isRegularFile(file)) {
            LOGGER.warning(() -> "Config file " + file + " not found. Using default config.");
            return DEFAULT;
        }
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read config file " + file, e);
        }
        return fromProperties(properties);
    }

    static GeneticConfig fromProperties(Properties properties) {
        for (String key : properties.stringPropertyNames()) {
            if (!KNOWN_KEYS.contains(key.trim())) {
                LOGGER.warning(() -> "Ignoring unknown config key " + key);
            }
        }
        return new GeneticConfig(
                readInt(properties, DEFAULT.populationSize, "POPULATION_SIZE"),
                readInt(properties, DEFAULT.tournamentSize, "TOURNAMENT_SIZE"),
                readInt(properties, DEFAULT.generations, "GENERATIONS"),
                readInt(properties, DEFAULT.freePass, "FREE_PASS"),
                readInt(properties, DEFAULT.freshBlood, "FRESH_BLOOD"),
                readInt(properties, DEFAULT.mutationChance, "MUTATION_CHANCE"),
                readInt(properties, DEFAULT.maxGenerationsWithoutImprovement, MAX_WITHOUT_IMPROVEMENT,
                        LEGACY_MAX_WITHOUT_IMPROVEMENT));
    }

    private static int readInt(Properties properties, int defaultValue, String... keys) {
        for (String key : keys) {
            String value = properties.getProperty(key);
            if (value != null) {
                try {
                    return Integer.parseInt(value.trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid value for " + key + ": '" + value.trim() + "'", e);
                }
            }
        }
        return defaultValue;
    }
}
