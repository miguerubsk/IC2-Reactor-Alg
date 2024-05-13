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
package geneticAlg;

import java.util.Random;

/**
 *
 * @author Miguel González García
 */
public class codeHelper {

    Random rnd = new Random();
    public final String[] validIDs = {"1B", "0D", "11", "14"};

    /**
     * 
     * @return 
     */
    public String getRandomID() {
        return validIDs[rnd.nextInt(validIDs.length)];
    }

    /**
     * 
     * @return 
     */
    public String getRandomCode() {
        StringBuilder sb = new StringBuilder(108);
        for (int i = 0; i < 54; i++) {
            sb.append(getRandomID());
        }
        return sb.toString();
    }

    /**
     * 
     * @param code
     * @return 
     */
    public String mutateGene(String code) {
        StringBuilder sb = new StringBuilder();
        int pos = rnd.nextInt(53) * 2;

        for (int i = 0; i < 108; i++) {
            if (i == pos) {
                sb.append(getRandomID());
                i++;
            } else {
                sb.append(code.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @param code1
     * @param code2
     * @return 
     */
    public String onePointCrossover(String code1, String code2) {
        int pos = rnd.nextInt(53) * 2;
        StringBuilder sb = new StringBuilder(108);
        for (int i = 0; i < 108; i++) {
            if (i >= pos) {
                sb.append(code2.charAt(i));
            } else {
                sb.append(code1.charAt(i));
            }
        }
        return sb.toString();
    }
}
