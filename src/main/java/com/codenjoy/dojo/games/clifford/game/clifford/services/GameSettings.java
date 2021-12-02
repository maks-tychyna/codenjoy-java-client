package com.codenjoy.dojo.games.clifford.game.clifford.services;

/*-
 * #%L
 * expansion - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 - 2020 Codenjoy
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


import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.*;

import com.codenjoy.dojo.games.clifford.game.clifford.model.Level;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.incativity.InactivitySettings;
import com.codenjoy.dojo.services.level.LevelsSettings;
import com.codenjoy.dojo.services.round.RoundSettings;
import com.codenjoy.dojo.services.semifinal.SemifinalSettings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import com.codenjoy.dojo.services.settings.SettingsReader;
import java.util.Arrays;
import java.util.List;

public class GameSettings extends SettingsImpl
        implements SettingsReader<GameSettings>,
                   InactivitySettings<GameSettings>,
                   RoundSettings<GameSettings>,
                   LevelsSettings<GameSettings>,
                   SemifinalSettings<GameSettings> {

    public enum Keys implements Key {

        MASK_POTIONS_COUNT("[Game] Mask potions count"),
        MASK_TICKS("[Game] Mask ticks"),

        BACKWAYS_COUNT("[Game] Backways count"),
        BACKWAY_TICKS("[Game] Backway ticks"),

        ROBBERS_COUNT("[Game] Robbers count"),

        GENERATE_KEYS("[Game] Generate picked keys"),

        CLUE_COUNT_GLOVE("[Game] Glove clue count"),
        CLUE_SCORE_GLOVE("[Score] Glove clue score"),
        CLUE_SCORE_GLOVE_INCREMENT("[Score] Glove clue score increment"),

        CLUE_COUNT_KNIFE("[Game] Knife clue count"),
        CLUE_SCORE_KNIFE("[Score] Knife clue score"),
        CLUE_SCORE_KNIFE_INCREMENT("[Score] Knife clue score increment"),

        CLUE_COUNT_RING("[Game] Ring clue count"),
        CLUE_SCORE_RING("[Score] Ring clue score"),
        CLUE_SCORE_RING_INCREMENT("[Score] Ring clue score increment"),

        KILL_HERO_SCORE("[Score] Kill hero score"),
        KILL_ENEMY_SCORE("[Score] Kill enemy score"),
        HERO_DIE_PENALTY("[Score] Hero die penalty"),
        SUICIDE_PENALTY("[Score] Suicide penalty");

        private String key;

        Keys(String key) {
            this.key = key;
        }

        @Override
        public String key() {
            return key;
        }
    }

    @Override
    public List<Key> allKeys() {
        return Arrays.asList(Keys.values());
    }

    public GameSettings() {
        initInactivity();
        initRound();
        initSemifinal();

        integer(Keys.MASK_POTIONS_COUNT, 0);
        integer(Keys.MASK_TICKS, 15);

        integer(Keys.BACKWAYS_COUNT, 5);
        integer(Keys.BACKWAY_TICKS, 50);

        integer(Keys.ROBBERS_COUNT, 3);

        bool(Keys.GENERATE_KEYS, false);

        integer(Keys.CLUE_COUNT_GLOVE, 20);
        integer(Keys.CLUE_SCORE_GLOVE, 1);
        integer(Keys.CLUE_SCORE_GLOVE_INCREMENT, 1);

        integer(Keys.CLUE_COUNT_KNIFE, 10);
        integer(Keys.CLUE_SCORE_KNIFE, 2);
        integer(Keys.CLUE_SCORE_KNIFE_INCREMENT, 1);

        integer(Keys.CLUE_COUNT_RING, 5);
        integer(Keys.CLUE_SCORE_RING, 5);
        integer(Keys.CLUE_SCORE_RING_INCREMENT, 1);

        integer(Keys.KILL_HERO_SCORE, 20);
        integer(Keys.KILL_ENEMY_SCORE, 50);
        integer(Keys.HERO_DIE_PENALTY, 1);
        integer(Keys.SUICIDE_PENALTY, 10);

        Levels.setup(this);
    }

    public Level level(int level, Dice dice) {
        return new Level(getRandomLevelMap(level, dice));
    }
}
