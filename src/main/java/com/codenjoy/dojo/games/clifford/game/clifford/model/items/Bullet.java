package com.codenjoy.dojo.games.clifford.game.clifford.model.items;

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

import com.codenjoy.dojo.games.clifford.game.clifford.model.Field;
import com.codenjoy.dojo.games.clifford.game.clifford.model.Hero;
import com.codenjoy.dojo.games.clifford.game.clifford.model.Player;
import com.codenjoy.dojo.games.clifford.Element;
import com.codenjoy.dojo.services.MovingObject;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.State;

public class Bullet extends MovingObject implements State<Element, Player> {

    private Hero owner;
    private Field field;
    private boolean bounced;

    public Bullet(Hero owner) {
        super(owner.getX(), owner.getY(), owner.getDirection());
        moving = true;
        speed = 2;
        this.owner = owner;
        this.field = owner.field();
    }

    public Hero getOwner() {
        return owner;
    }

    public boolean isBounced() {
        return bounced;
    }

    public void invertDirection() {
        bounced = true;
        direction = direction.inverted();
    }

    @Override
    protected void moving(Point pt) {
        if (pt.isOutOf(field.size())) {
            remove();
        } else {
            move(pt);
            field.affect(this);
        }
    }

    public void remove() {
        moving = false;
        field.bullets().removeExact(this);
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        return Element.BULLET;
    }

    @Override
    public String toString() {
        return String.format("[%s,%s,%s]", x, y, direction);
    }
}
