package com.codenjoy.dojo.games.clifford.game.clifford.model;

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


import com.codenjoy.dojo.games.clifford.game.clifford.model.items.Brick;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.Bullet;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.Potion.PotionType;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.door.Door;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.door.Key;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.robber.Robber;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.field.Accessor;
import com.codenjoy.dojo.services.round.RoundGameField;
import java.util.List;

public interface Field extends RoundGameField<Player> {

    boolean isBarrier(Point pt);

    boolean tryToCrack(Hero hero, Point pt);

    boolean isPit(Point pt);

    boolean isLadder(Point pt);

    boolean isPipe(Point pt);

    boolean isFree(Point pt);

    boolean isFullBrick(Point pt);

    boolean isHero(Point pt);

    boolean isBrick(Point pt);

    boolean isHunter(Point pt);

    void leaveClue(Point pt, Class<? extends Point> clazz);

    boolean under(Point pt, PotionType potion);

    int size();

    boolean isBorder(Point pt);

    List<Hero> activeHeroes();

    Accessor<Hero> heroes();

    void suicide(Hero hero);

    Accessor<Brick> bricks();

    List<Hero> visibleHeroes();

    Accessor<Robber> robbers();

    Accessor<Door> doors();

    Accessor<Key> keys();

    Accessor<Bullet> bullets();

    void affect(Bullet bullet);
}
