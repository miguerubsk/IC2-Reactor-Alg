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

import Simulator.ComponentFactory;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Miguel González García
 */
public class codeHelper {

    Random rnd = new Random(System.currentTimeMillis());

    private ArrayList<String> createIDs() {

        ArrayList<String> result = new ArrayList<>();
        // id 0 is reserved for "empty" (no component); valid ids run from 1 to
        // ComponentFactory.getComponentCount() - 1, matching the 2-hex-char encoding
        // used throughout this class.
        for (int i = 1; i < ComponentFactory.getComponentCount(); i++) {

            result.add(String.format("%02X", i));
            System.err.println(String.format("%02X", i));

        }
        return result;
    }

    public final ArrayList<String> validIDs = createIDs();

    /**
     *
     * @return
     */
    public String getRandomID() {
        return validIDs.get(rnd.nextInt(validIDs.size()));
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
                if( i < code.length()) sb.append(code.charAt(i));
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

    /**
     *
     * @param code1
     * @param code2
     * @return
     */
    public String twoPointCrossover(String code1, String code2) {
        int pos1 = rnd.nextInt(53) * 2;
        int pos2 = rnd.nextInt(53) * 2;

        if (pos2 < pos1) {
            int aux = pos1;
            pos1 = pos2;
            pos2 = aux;
        }

        StringBuilder sb = new StringBuilder(108);

        if (rnd.nextBoolean()) {
            for (int i = 0; i < 108; i++) {
                if (i >= pos1 && i <= pos2) {
                    sb.append(code2.charAt(i));
                }

                if (i < pos1) {
                    sb.append(code1.charAt(i));
                }

                if (i > pos2) {
                    sb.append(code2.charAt(i));
                }
            }
        } else {
            for (int i = 0; i < 108; i++) {
                if (i >= pos1 && i <= pos2) {
                    sb.append(code1.charAt(i));
                }

                if (i < pos1) {
                    sb.append(code2.charAt(i));
                }

                if (i > pos2) {
                    sb.append(code1.charAt(i));
                }
            }
        }

        return sb.toString();
    }
    
    public String uniformCrossover(String code1, String code2){
        StringBuilder sb = new StringBuilder(108);
        
        for (int i = 0; i < 108; i += 2){
            if(code1.charAt(i) != code2.charAt(i) && code1.charAt(i+1) != code2.charAt(i+1) && rnd.nextBoolean()){
                if(rnd.nextBoolean()){
                    sb.append(code1.charAt(i)).append(code1.charAt(i+1));
                }else{
                    sb.append(code2.charAt(i)).append(code2.charAt(i+1));
                }
            }else{
                if(rnd.nextBoolean()){
                    sb.append(code2.charAt(i)).append(code2.charAt(i+1));
                }else{
                    sb.append(code1.charAt(i)).append(code1.charAt(i+1));
                }
            }
        }
        
        return sb.toString();
    } 
}

//O(n)