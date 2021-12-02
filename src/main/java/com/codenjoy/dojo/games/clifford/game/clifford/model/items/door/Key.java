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

public class Key extends PointImpl implements State<Element, Player> {

    private KeyType keyType;

    public Key(Point point, KeyType keyType) {
        super(point);
        this.keyType = keyType;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        switch (keyType) {
            case GOLD: return Element.KEY_GOLD;
            case SILVER: return Element.KEY_SILVER;
            case BRONZE: return Element.KEY_BRONZE;
        }
        throw new IllegalArgumentException("invalid keyType " + keyType);
    }

    @Override
    public String toString() {
        return String.format("[%s,%s=%s]",
                x, y,
                keyType);
    }
}
