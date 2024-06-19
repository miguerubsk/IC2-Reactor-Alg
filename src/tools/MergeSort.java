/*
 * Copyright (C) 06-jun-2024 Miguel González García
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
package tools;

import geneticAlg.ReactorEntity;
import java.util.ArrayList;

/**
 *
 * @author Miguel González García
 */
public class MergeSort {

    private final int UMBRAL = 2;

    public MergeSort() {
    }
    

    /**
     *
     * @param T
     * @param ini
     * @param fin
     */
    private void burbujaMejorado(ArrayList<ReactorEntity> T, int ini, int fin) {
        int i, j;
        ReactorEntity aux;
        boolean entra = true;
        for (i = ini; i < fin && entra; i++) {
            entra = false;
            for (j = ini; j < fin; j++) {
                if (T.get(j).fitness > T.get(j + 1).fitness) {
                    aux = T.get(j);
                    T.set(j, T.get(j + 1));
                    T.set(j + 1, aux);
                    entra = true;
                }
            }
        }
    }

    /**
     *
     * @param v
     * @param ini
     * @param fin
     * @param centro
     */
    private void merge(ArrayList<ReactorEntity> v1, ArrayList<ReactorEntity> v2, int centro) {
        int x, y, z;
        ArrayList<ReactorEntity> aux = new ArrayList<>();
        x = 0;
        y = centro + 1;
        z = 0;

        while ((x <= v1.size()) && (y <= v2.size())) {

            if (v1.get(x).fitness <= v2.get(y).fitness) {
                aux.set(z, v1.get(x));
                x++;
            } else {
                aux.set(z, v2.get(y));
                y++;
            }
            z++;
        }

        while (x <= v1.size()) {
            aux.set(z, v1.get(x));
            x++;
            z++;
        }

        while (y <= v2.size()) {
            aux.set(z, v2.get(z));
            y++;
            z++;
        }

        z = 0;
        for (x = 0; x <= v2.size(); x++) {
            v1.set(x, aux.get(z));
            z++;
        }

    }

    /**
     *
     * @param v
     * @param ini
     * @param fin
     */
    public void mergesort(ArrayList<ReactorEntity> v, int ini, int fin) {
        if (v.size() + 1 <= UMBRAL) {
            burbujaMejorado(v, ini, fin);
        } else {
            
            int centro = ini + ((fin - ini) / 2);
            
            ArrayList<ReactorEntity> v1 = new ArrayList<>();
            ArrayList<ReactorEntity> v2 = new ArrayList<>();
            
            v1.addAll(v.subList(ini, centro));
            v2.addAll(v.subList(centro, fin));
            
            mergesort(v1, ini, centro);
            mergesort(v2, centro + 1, fin);

            merge(v1, v2, centro);
        }
    }
}