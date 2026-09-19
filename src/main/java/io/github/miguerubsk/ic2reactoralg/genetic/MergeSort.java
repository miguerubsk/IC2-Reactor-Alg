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
package io.github.miguerubsk.ic2reactoralg.genetic;

import java.util.ArrayList;

/**
 * Merge sort over {@link ReactorEntity} lists (by fitness), using a bubble sort for small segments.
 */
public class MergeSort {

    private static final int THRESHOLD = 2;

    public MergeSort() {}

    /**
     * Improved bubble sort, used for small segments.
     */
    private void bubbleSort(ArrayList<ReactorEntity> list, int from, int to) {
        boolean swapped = true;
        for (int i = from; i < to && swapped; i++) {
            swapped = false;
            for (int j = from; j < to - 1; j++) {
                if (list.get(j).fitness > list.get(j + 1).fitness) {
                    ReactorEntity temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                }
            }
        }
    }

    /**
     * Merges two sorted lists into a single one.
     */
    private ArrayList<ReactorEntity> merge(ArrayList<ReactorEntity> left, ArrayList<ReactorEntity> right) {
        ArrayList<ReactorEntity> merged = new ArrayList<>();
        int x = 0, y = 0;

        // Merge while both lists still have elements
        while (x < left.size() && y < right.size()) {
            if (left.get(x).fitness <= right.get(y).fitness) {
                merged.add(left.get(x));
                x++;
            } else {
                merged.add(right.get(y));
                y++;
            }
        }

        // Append what is left of the left list
        while (x < left.size()) {
            merged.add(left.get(x));
            x++;
        }

        // Append what is left of the right list
        while (y < right.size()) {
            merged.add(right.get(y));
            y++;
        }

        return merged;
    }

    /**
     * Sorts the range [from, to) using merge sort.
     */
    public ArrayList<ReactorEntity> mergeSort(ArrayList<ReactorEntity> list, int from, int to) {
        if ((to - from) <= THRESHOLD) {
            bubbleSort(list, from, to);
            return new ArrayList<>(list.subList(from, to));
        } else {
            int middle = from + ((to - from) / 2);

            ArrayList<ReactorEntity> left = mergeSort(new ArrayList<>(list.subList(from, middle)), 0, middle - from);
            ArrayList<ReactorEntity> right = mergeSort(new ArrayList<>(list.subList(middle, to)), 0, to - middle);

            return merge(left, right);
        }
    }
}
