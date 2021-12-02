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


import com.codenjoy.dojo.games.clifford.game.clifford.services.Events;
import com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings;
import com.codenjoy.dojo.games.clifford.game.clifford.services.Scores;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.round.RoundGamePlayer;

public class Player extends RoundGamePlayer<Hero, Field> {

    public Player(EventListener listener, GameSettings settings) {
        super(listener, settings);
    }

    @Override
    public void start(int round, Object startEvent) {
        super.start(round, startEvent);
        hero.clearScores();
    }

    @Override
    public Hero createHero(Point pt) {
        Hero hero = new Hero(pt, Direction.RIGHT);
        hero.setPlayer(this);
        return hero;
    }

    @Override
    public void event(Object event) {
        event = Events.wrap(event);
        hero.addScore(Scores.scoreFor(settings(), event));
        super.event(event);
    }

    private GameSettings settings() {
        return (GameSettings) settings;
    }
}
