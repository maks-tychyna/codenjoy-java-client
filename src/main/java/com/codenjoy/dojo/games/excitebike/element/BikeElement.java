package com.codenjoy.dojo.games.excitebike.element;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.printer.CharElement;

public enum BikeElement implements CharElement {
    BIKE('B'),
    BIKE_AT_ACCELERATOR('A'),
    BIKE_AT_INHIBITOR('I'),
    BIKE_AT_LINE_CHANGER_UP('U'),
    BIKE_AT_LINE_CHANGER_DOWN('D'),
    BIKE_AT_KILLED_BIKE('K'),
    BIKE_AT_SPRINGBOARD_LEFT('L'),
    BIKE_AT_SPRINGBOARD_LEFT_DOWN('M'),
    BIKE_AT_SPRINGBOARD_RIGHT('R'),
    BIKE_AT_SPRINGBOARD_RIGHT_DOWN('S'),
    BIKE_IN_FLIGHT_FROM_SPRINGBOARD('F'),

    BIKE_FALLEN('b'),
    BIKE_FALLEN_AT_ACCELERATOR('a'),
    BIKE_FALLEN_AT_INHIBITOR('i'),
    BIKE_FALLEN_AT_LINE_CHANGER_UP('u'),
    BIKE_FALLEN_AT_LINE_CHANGER_DOWN('d'),
    BIKE_FALLEN_AT_FENCE('f'),
    BIKE_FALLEN_AT_OBSTACLE('o'),

    OTHER_BIKE('Ḃ'),
    OTHER_BIKE_AT_ACCELERATOR('Ā'),
    OTHER_BIKE_AT_INHIBITOR('Ī'),
    OTHER_BIKE_AT_LINE_CHANGER_UP('Ū'),
    OTHER_BIKE_AT_LINE_CHANGER_DOWN('Ď'),
    OTHER_BIKE_AT_KILLED_BIKE('Ḱ'),
    OTHER_BIKE_AT_SPRINGBOARD_LEFT('Ĺ'),
    OTHER_BIKE_AT_SPRINGBOARD_LEFT_DOWN('Ṁ'),
    OTHER_BIKE_AT_SPRINGBOARD_RIGHT('Ř'),
    OTHER_BIKE_AT_SPRINGBOARD_RIGHT_DOWN('Ŝ'),
    OTHER_BIKE_IN_FLIGHT_FROM_SPRINGBOARD('Ḟ'),

    OTHER_BIKE_FALLEN('ḃ'),
    OTHER_BIKE_FALLEN_AT_ACCELERATOR('ā'),
    OTHER_BIKE_FALLEN_AT_INHIBITOR('ī'),
    OTHER_BIKE_FALLEN_AT_LINE_CHANGER_UP('ū'),
    OTHER_BIKE_FALLEN_AT_LINE_CHANGER_DOWN('ď'),
    OTHER_BIKE_FALLEN_AT_FENCE('ḟ'),
    OTHER_BIKE_FALLEN_AT_OBSTACLE('ō');

    final char ch;

    BikeElement(char ch) {
        this.ch = ch;
    }

    public static BikeElement valueOf(char ch) {
        for (BikeElement el : BikeElement.values()) {
            if (el.ch == ch) {
                return el;
            }
        }
        throw new IllegalArgumentException("No such element for " + ch);
    }

    @Override
    public char ch() {
        return ch;
    }

    @Override
    public String toString() {
        return String.valueOf(ch);
    }

}
