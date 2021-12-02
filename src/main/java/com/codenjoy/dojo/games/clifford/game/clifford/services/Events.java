package com.codenjoy.dojo.games.clifford.game.clifford.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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

public class Events {

    private Event type;
    private int amount;

    public enum Event {
        START_ROUND,      // раунд стартовал
        WIN_ROUND,        // герой победил в раунде

        // TODO: implement KILL_ROBBER (for this time robber cannot be killed);
        KILL_HERO,         // герой замуровал в стенке другого героя
        KILL_ENEMY,        // герой замуровал в стенке другого вражеского героя
        HERO_DIE,          // героя убили

        GET_CLUE_KNIFE,  // подобрано улику
        GET_CLUE_GLOVE,
        GET_CLUE_RING,

        SUICIDE;          // герой заблудился и решил суициднуться
    }

    public static Object wrap(Object input) {
        return (input instanceof Event)
                ? new Events((Event) input)
                : input;
    }

    public Events with(int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String toString() {
        return type + ((amount != 0)?("(" + amount + ")"):"");
    }

    public Events(Event type) {
        this.type = type;
    }

    public Events(Event type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public int amount() {
        return amount;
    }

    public Event type() {
        return type;
    }
}
