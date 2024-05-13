/*
 * Copyright (C) 13-may-2024 Miguel González García
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
package Main;

import geneticAlg.GeneticAlg;

/**
 *
 * @author Miguel González García
 */
public class IC2ReactorAlg {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*ReactorEntity re = new ReactorEntity("030C0D140D0D0C0D15150C0D0D0C0D0D030D150D030D0D030D0D0C0C0D0D0C0D0D0C0D150D030D0D030D0D030D150D0C150D0C150D0C");
        re.calculateFitness();
		System.out.printf("%f %f %f", re.fitness, re.avgEUoutput, re.leftoverHeat);*/
        GeneticAlg g = new GeneticAlg();
        g.run();
    }

}
