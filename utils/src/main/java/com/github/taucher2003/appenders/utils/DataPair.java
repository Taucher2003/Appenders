/*
 *
 *  Copyright 2021 Niklas van Schrick and the contributors of the Appenders Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.taucher2003.appenders.utils;

import java.util.Objects;

/**
 * Class to store two objects. Useful as return type to return multiple objects
 *
 * @param <F> type of the first object
 * @param <S> type of the second object
 */
public class DataPair<F, S> {

    private final F first;
    private final S second;

    /**
     * Creates a new DataPair
     *
     * @param first  the first element
     * @param second the second element
     */
    public DataPair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of this pair
     *
     * @return the element
     */
    public F getFirst() {
        return first;
    }

    /**
     * Returns the second element of this pair
     *
     * @return the element
     */
    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataPair<?, ?> dataPair = (DataPair<?, ?>) o;
        return Objects.equals(first, dataPair.first) && Objects.equals(second, dataPair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "DataPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
