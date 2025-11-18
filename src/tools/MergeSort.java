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

public class MergeSort {

    private final int UMBRAL = 2;

    public MergeSort() {}

    /**
     * Ordenación burbuja mejorada para segmentos pequeños
     */
    private void burbujaMejorado(ArrayList<ReactorEntity> T, int ini, int fin) {
        boolean entra = true;
        for (int i = ini; i < fin && entra; i++) {
            entra = false;
            for (int j = ini; j < fin - 1; j++) {
                if (T.get(j).fitness > T.get(j + 1).fitness) {
                    ReactorEntity aux = T.get(j);
                    T.set(j, T.get(j + 1));
                    T.set(j + 1, aux);
                    entra = true;
                }
            }
        }
    }

    /**
     * Combina dos listas ordenadas en una sola
     */
    private ArrayList<ReactorEntity> merge(ArrayList<ReactorEntity> v1, ArrayList<ReactorEntity> v2) {
        ArrayList<ReactorEntity> aux = new ArrayList<>();
        int x = 0, y = 0;

        // Mezclar mientras haya elementos en ambas listas
        while (x < v1.size() && y < v2.size()) {
            if (v1.get(x).fitness <= v2.get(y).fitness) {
                aux.add(v1.get(x));
                x++;
            } else {
                aux.add(v2.get(y));
                y++;
            }
        }

        // Añadir los restantes de v1
        while (x < v1.size()) {
            aux.add(v1.get(x));
            x++;
        }

        // Añadir los restantes de v2
        while (y < v2.size()) {
            aux.add(v2.get(y));
            y++;
        }

        return aux;
    }

    /**
     * Ordenación por mergesort
     */
    public ArrayList<ReactorEntity> mergesort(ArrayList<ReactorEntity> v, int ini, int fin) {
        if ((fin - ini) <= UMBRAL) {
            burbujaMejorado(v, ini, fin);
            return new ArrayList<>(v.subList(ini, fin));
        } else {
            int centro = ini + ((fin - ini) / 2);

            ArrayList<ReactorEntity> v1 = mergesort(new ArrayList<>(v.subList(ini, centro)), 0, centro - ini);
            ArrayList<ReactorEntity> v2 = mergesort(new ArrayList<>(v.subList(centro, fin)), 0, fin - centro);

            return merge(v1, v2);
        }
    }
}
