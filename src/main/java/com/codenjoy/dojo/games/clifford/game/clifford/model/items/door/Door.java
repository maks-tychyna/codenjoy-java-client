package com.codenjoy.dojo.games.clifford.game.clifford.model.items.door;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2021 Codenjoy
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

import com.codenjoy.dojo.games.clifford.game.clifford.model.Player;
import com.codenjoy.dojo.games.clifford.Element;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;

public class Door extends PointImpl implements State<Element, Player> {

    private State state;
    private KeyType keyType;

    public Door(Point point, State state, KeyType keyType) {
        super(point);
        this.state = state;
        this.keyType = keyType;
    }

    public boolean isOpened() {
        return state == State.OPENED;
    }

    public void open() {
        state = State.OPENED;
    }

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    public void close() {
        state = State.CLOSED;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        switch (keyType) {
            case GOLD:
                return isOpened()
                        ? Element.OPENED_DOOR_GOLD
                        : Element.CLOSED_DOOR_GOLD;
            case SILVER:
                return isOpened()
                        ? Element.OPENED_DOOR_SILVER
                        : Element.CLOSED_DOOR_SILVER;
            case BRONZE:
                return isOpened()
                        ? Element.OPENED_DOOR_BRONZE
                        : Element.CLOSED_DOOR_BRONZE;
        }
        throw new IllegalArgumentException("invalid keyType " + keyType);
    }

    public enum State {
        OPENED, CLOSED
    }

    @Override
    public String toString() {
        return String.format("[%s,%s=%s:%s]",
                x, y,
                keyType,
                state);
    }
}
