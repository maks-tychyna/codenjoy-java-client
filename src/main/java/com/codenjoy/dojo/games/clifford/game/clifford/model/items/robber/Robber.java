package com.codenjoy.dojo.games.clifford.game.clifford.model.items.robber;

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


import static java.util.stream.Collectors.toList;

import com.codenjoy.dojo.games.clifford.game.clifford.model.Field;
import com.codenjoy.dojo.games.clifford.game.clifford.model.Hero;
import com.codenjoy.dojo.games.clifford.game.clifford.model.Player;
import com.codenjoy.dojo.games.clifford.Element;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.field.Fieldable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Robber extends PointImpl implements Tickable, Fieldable<Field>, State<Element, Player> {

    private Direction direction;
    private RobberAI ai;
    private Field field;
    private Class<? extends Point> withClue;
    private Hero prey;

    public Robber(Point pt, Direction direction) {
        super(pt);
        withClue = null;
        this.direction = direction;
        this.ai = new AI();
    }

    @Override
    public void init(Field field) {
        this.field = field;
    }

    public RobberAI getAi() {
        return ai;
    }

    public void setAi(RobberAI ai) {
        this.ai = ai;
    }

    private Point under(Point pt) {
        return Direction.DOWN.change(pt);
    }

    @Override
    public void tick() {
        if (isFall()) {
            // при падении в ямку оставляем улику
            if (field.isBrick(under(this)) && withClue != null) {
                // TODO герой не может оставить улику, если он залез в ямку под лестницу, улика должно появиться сбоку
                field.leaveClue(this, withClue);
                withClue = null;
            }
            move(x, y - 1);
            return;
        }

        if (field.isBrick(this)) {
            // если ямка заросла, выбираемся
            if (field.isFullBrick(this)) {
                move(Direction.UP.change(this));
            }
            return;
        }

        List<Hero> heroes = field.visibleHeroes();

        // если тот, за кем охотились уже ушел с поля или умер, или стал невидимым - будем искать нового
        if (prey == null || !prey.isActiveAndAlive() || prey.isVisible()) {
            prey = null;
        }

        List<Hero> free = getFreePreys(heroes);

        Direction direction = ai.getDirection(field, this, (List)free);
        if (direction == null) {
            prey = null;
            return;
        }
        Point reached = ai.getReached();
        prey = findHero(heroes, reached);

        if (direction == Direction.UP && !field.isLadder(this)) return;

        if (direction != Direction.DOWN) {
            this.direction = direction;
        }
        Point pt = direction.change(this);

        // чертик чертику не помеха - пусть проходят друг сквозь друга
        // if (field.isRobberAt(pt.getX(), pt.getY())) return;

        if (!field.isHero(pt)
                && field.isBarrier(pt)) return;

        move(pt);
    }

    private List<Hero> getFreePreys(List<Hero> all) {
        // у нас уже есть за кем охотиться
        if (prey != null) {
            return Arrays.asList(prey);
        }

        // ищем за кем охотиться
        List<Robber> robbers = field.robbers().all();
        // выбираем только тех, за кем еще никто не охотится
        List<Hero> free = all.stream()
                .filter(prey -> robbers.stream()
                        .map(Robber::prey)
                        .filter(Objects::nonNull)
                        .noneMatch(prey::equals))
                .collect(toList());
        // если все заняты, будем бежать за ближайшим
        if (free.isEmpty()) {
            return all;
        }

        return free;
    }

    private Hero findHero(List<Hero> preys, Point reached) {
        return preys.stream()
                .filter(it -> it.equals(reached))
                .findFirst()
                .orElse(null);
    }

    private Hero prey() {
        return prey;
    }

    public boolean isFall() {
        return !field.isBrick(this)
                && (field.isHero(under(this))
                    || field.isPit(this))
                && !field.isPipe(this)
                && !field.isLadder(this);
    }

    @Override
    public Element state(Player player, Object... alsoAtPoint) {
        if (field.isBrick(this)) {
            return Element.ROBBER_PIT;
        }

        if (field.isLadder(this)) {
            return Element.ROBBER_LADDER;
        }

        if (field.isPipe(this)) {
            return Element.ROBBER_PIPE;
        }

        if (isFall()) {
            return Element.ROBBER_FALL;
        }

        return isLeftTurn()
                ? Element.ROBBER_LEFT
                : Element.ROBBER_RIGHT;
    }

    public boolean isLeftTurn() {
        return direction.equals(Direction.LEFT);
    }

    public void getClue(Class<? extends Point> clazz) {
        withClue = clazz;
    }

    public boolean withClue() {
        return withClue != null;
    }

}
