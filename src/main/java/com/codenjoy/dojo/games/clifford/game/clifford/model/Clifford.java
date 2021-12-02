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


import static com.codenjoy.dojo.games.clifford.game.clifford.model.items.Potion.PotionType.MASK_POTION;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.*;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.GET_CLUE_GLOVE;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.GET_CLUE_KNIFE;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.GET_CLUE_RING;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.HERO_DIE;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.KILL_ENEMY;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.KILL_HERO;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.START_ROUND;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.SUICIDE;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.Events.Event.WIN_ROUND;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.*;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.BACKWAYS_COUNT;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.BACKWAY_TICKS;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.CLUE_COUNT_GLOVE;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.CLUE_COUNT_KNIFE;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.CLUE_COUNT_RING;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.GENERATE_KEYS;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.MASK_POTIONS_COUNT;
import static com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings.Keys.ROBBERS_COUNT;
import static com.codenjoy.dojo.services.field.Generator.generate;
import static java.util.stream.Collectors.toList;

import com.codenjoy.dojo.games.clifford.game.clifford.model.items.*;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.Potion.PotionType;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.clue.ClueGlove;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.clue.ClueKnife;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.clue.ClueRing;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.door.Door;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.door.Key;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.door.KeyType;
import com.codenjoy.dojo.games.clifford.game.clifford.model.items.robber.Robber;
import com.codenjoy.dojo.games.clifford.game.clifford.services.GameSettings;
import com.codenjoy.dojo.games.clifford.Element;
import com.codenjoy.dojo.services.BoardUtils;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.annotations.PerformanceOptimized;
import com.codenjoy.dojo.services.field.Accessor;
import com.codenjoy.dojo.services.field.PointField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.round.RoundField;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Clifford extends RoundField<Player> implements Field {

    private Level level;
    private PointField field;
    private List<Player> players;
    private Dice dice;
    private GameSettings settings;

    private int backwaysTimer;
    private Multimap<Hero, Hero> killerWithDeads;

    public Clifford() {
        // do nothing, for testing only
    }

    public Clifford(Dice dice, Level level, GameSettings settings) {
        super(START_ROUND, WIN_ROUND, HERO_DIE, settings);

        this.level = level;
        this.dice = dice;
        this.settings = settings;
        this.field = new PointField();
        this.players = new LinkedList<>();

        clearScore();
    }

    private void generateAll() {
        generatePotions();
        generateClue();
        generateBackways();
        generateRobbers();
    }

    @Override
    public void clearScore() {
        level.saveTo(field);
        field.init(this);

        this.killerWithDeads = ArrayListMultimap.create();
        resetBackwaysTimer();
        robbers().forEach(robber -> robber.init(this));
        generateAll();

        super.clearScore();
    }

    @Override
    public List<Player> players() {
        return players;
    }

    @Override
    protected void cleanStuff() {
        // do nothing
    }

    @Override
    protected void tickField() {
        bulletsGo();
        heroesGo();
        robbersGo();
        bricksGo();
        backwaysGo();

        rewardMurderers();
        generateAll();
    }

    private void diedFromHunter() {
        activeHeroes().forEach(Hero::checkDiedFromHunter);
    }

    private void diedFromWall() {
        activeHeroes().forEach(Hero::checkDiedFromWall);
    }

    @Override
    public void oneMoreDead(Player player) {
        if (!settings.bool(GENERATE_KEYS)) {
            releaseKeys(player.getHero().getKeys());
        }
        super.oneMoreDead(player);
    }

    private void rewardMurderers() {
        killerWithDeads.asMap().forEach((killer, deads) -> {
            for (Hero dead : deads) {
                if (killer == dead) {
                    killer.event(SUICIDE);
                }
                if (!killer.isAlive()) continue;

                if (killer.getTeamId() == dead.getTeamId()) {
                    killer.event(KILL_HERO);
                } else {
                    killer.event(KILL_ENEMY);
                }
            }
        });
        killerWithDeads.clear();
    }

    private void generateClue() {
        generate(clueKnife(),
                settings, CLUE_COUNT_KNIFE,
                player -> freeRandom((Player) player),
                ClueKnife::new);

        generate(clueGlove(),
                settings, CLUE_COUNT_GLOVE,
                player -> freeRandom((Player) player),
                ClueGlove::new);

        generate(clueRing(),
                settings, CLUE_COUNT_RING,
                player -> freeRandom((Player) player),
                ClueRing::new);
    }

    private void generatePotions() {
        generate(potions(),
                settings, MASK_POTIONS_COUNT,
                player -> freeRandom((Player) player),
                pt -> new Potion(pt, PotionType.MASK_POTION));
    }

    private void generateRobbers() {
        generate(robbers(),
                settings, ROBBERS_COUNT,
                player -> freeRandom((Player) player),
                pt -> {
                    Robber robber = new Robber(pt, Direction.LEFT);
                    robber.init(this);
                    return robber;
                });
    }

    private void generateBackways() {
        generate(backways(), settings, BACKWAYS_COUNT,
                player -> freeRandom((Player) player),
                Backway::new);
    }

    private void releaseKeys(Map<KeyType, Integer> keys) {
        for (Map.Entry<KeyType, Integer> entry : keys.entrySet()) {
            generate(keys(), entry.getValue(),
                    player -> freeRandom((Player) player),
                    pt -> new Key(pt, entry.getKey()));
        }
    }

    private void generateKeys(List<Key> keys) {
        for (Key prototype : keys) {
            generate(keys(), 1,
                    player -> freeRandom((Player) player),
                    pt -> new Key(pt, prototype.getKeyType()));
        }
    }

    public BoardReader<?> reader() {
        return field.reader(
                Hero.class,
                Robber.class,
                ClueKnife.class,
                ClueGlove.class,
                ClueRing.class,
                Border.class,
                Brick.class,
                Ladder.class,
                Potion.class,
                Pipe.class,
                Backway.class,
                Door.class,
                Key.class,
                Bullet.class);
    }

    private void bulletsGo() {
        for (Bullet bullet : bullets().copy()) {
            bullet.move();
        }
    }

    @Override
    public void affect(Bullet bullet) {
        for (Hero hero : heroes().getAt(bullet)) {
            if (hero.under(MASK_POTION)) continue;
            if (hero == bullet.getOwner() && !bullet.isBounced()) continue;

            hero.die();
            bullet.remove();
            killerWithDeads.put(bullet.getOwner(), hero);
        }

        for (Brick brick : bricks().getAt(bullet)) {
            if (brick.isNotTransparentForBullet()) {
                brick.crack(bullet.getOwner());
                bullet.remove();
            }
        }

        if (borders().contains(bullet)) {
            if (!bullet.isBounced()) {
                bullet.invertDirection();
            } else {
                bullet.remove();
            }
        }
    }

    private void bricksGo() {
        bricks().forEach(Brick::tick);
        diedFromWall();
        players.stream()
                .map(GamePlayer::getHero)
                .filter(hero -> !hero.isAlive())
                .forEach(dead -> bricks().getAt(dead).stream()
                        .map(Brick::getCrackedBy)
                        .filter(Objects::nonNull)
                        .forEach(killer -> killerWithDeads.put(killer, dead)));
    }

    private Optional<Brick> getBrick(Point pt) {
        return bricks().stream()
                .filter(brick -> brick.equals(pt))
                .findFirst();
    }

    private void heroesGo() {
        for (Player player : players) {
            Hero hero = player.getHero();
            if (!hero.isActiveAndAlive()) {
                continue;
            }

            hero.tick();
            diedFromHunter();

            if (bullets().contains(hero)) {
                bullets().getAt(hero).forEach(this::affect);
                continue;
            }

            if (clueKnife().contains(hero)) {
                clueKnife().removeAt(hero);
                hero.pickClue(GET_CLUE_KNIFE);
            } else if (clueGlove().contains(hero)) {
                clueGlove().removeAt(hero);
                hero.pickClue(GET_CLUE_GLOVE);
            } else if (clueRing().contains(hero)) {
                clueRing().removeAt(hero);
                hero.pickClue(GET_CLUE_RING);
            }

            if (keys().contains(hero)) {
                List<Key> keys = keys().getAt(hero);
                keys.forEach(key -> hero.pick(key.getKeyType()));
                keys().removeAt(hero);
                if (settings.bool(GENERATE_KEYS)) {
                    generateKeys(keys);
                }
            }

            if (potions().contains(hero)) {
                potions().removeAt(hero);
                hero.pick(PotionType.MASK_POTION);
            }

            if (backways().contains(hero)) {
                transport(hero);
            }
        }
        heroes().stream()
                .filter(hero -> hero.under(MASK_POTION))
                .forEach(maskHero -> heroes().getAt(maskHero).stream()
                        .filter(hero -> hero != maskHero)
                        .filter(hero -> !hero.under(MASK_POTION))
                        .forEach(hero -> killerWithDeads.put(maskHero, hero)));
    }

    private void transport(PointImpl point) {
        List<Backway> backways = backways().all();
        for (int i = 0; i < backways.size(); i++) {
            if (backways.get(i).equals(point)) {
                Backway backwayToMove = backways.get(i < backways.size() - 1 ? i + 1 : 0);
                point.move(backwayToMove.getX(), backwayToMove.getY());
                return;
            }
        }
    }

    private void robbersGo() {
        for (Robber robber : robbers().copy()) {
            robber.tick();
            diedFromHunter();

            if (clueKnife().contains(robber) && !robber.withClue()) {
                clueKnife().removeAt(robber);
                robber.getClue(ClueKnife.class);
            } else if (clueGlove().contains(robber) && !robber.withClue()) {
                clueGlove().removeAt(robber);
                robber.getClue(ClueGlove.class);
            } else if (clueRing().contains(robber) && !robber.withClue()) {
                clueRing().removeAt(robber);
                robber.getClue(ClueRing.class);
            }

            if (backways().contains(robber)) {
                transport(robber);
            }
        }
    }

    // TODO сделать чтобы каждый черный ход сам тикал свое время
    private void backwaysGo() {
        if (backwaysTimer == 0) {
            resetBackwaysTimer();
            backways().clear();
            generateBackways();
        } else {
            backwaysTimer--;
        }
    }

    private void resetBackwaysTimer() {
        backwaysTimer = Math.max(1, settings.integer(BACKWAY_TICKS));
    }

    @Override
    public boolean isBarrier(Point pt) {
        return pt.getX() > size() - 1 || pt.getX() < 0
                || pt.getY() < 0 || pt.getY() > size() - 1
                || isFullBrick(pt)
                || isBorder(pt)
                || (isHero(pt) && !under(pt, PotionType.MASK_POTION))
                || doors().getAt(pt).stream().anyMatch(Door::isClosed);
    }

    @Override
    public void suicide(Hero hero) {
        hero.getPlayer().event(SUICIDE);
    }

    @Override
    public boolean tryToCrack(Hero byHero, Point pt) {
        if (!isFullBrick(pt)) {
            return false;
        }

        Point over = Direction.UP.change(pt);
        if (isLadder(over)
                || clueKnife().contains(over)
                || clueGlove().contains(over)
                || clueRing().contains(over)
                || isFullBrick(over)
                || activeHeroes().contains(over)
                || robbers().contains(over)) {
            return false;
        }

        getBrick(pt).ifPresent(brick -> brick.crack(byHero));

        return true;
    }

    @Override
    public boolean isPit(Point pt) {
        Point under = Direction.DOWN.change(pt);

        return !(isFullBrick(under)
                || isLadder(under)
                || isBorder(under)
                || isHero(under)
                || robbers().contains(under));
    }

    @Override
    public boolean isFullBrick(Point pt) {
        return bricks().getAt(pt).stream()
                .anyMatch(brick -> brick.state(null) == Element.BRICK);
    }

    @Override
    public Optional<Point> freeRandom(Player player) {
        return BoardUtils.freeRandom(size(), dice, pt -> isFree(pt));
    }

    @Override
    public boolean isLadder(Point pt) {
        return ladder().contains(pt);
    }

    @Override
    public boolean isPipe(Point pt) {
        return pipe().contains(pt);
    }

    @Override
    public boolean isFree(Point pt) {
        return field.get(pt).isEmpty();
    }

    @Override
    public boolean isHero(Point pt) {
        return activeHeroes().contains(pt);
    }

    // TODO test
    //      может ли пройти через него вор - да
    //      можно ли простреливать под ним - да
    //      является ли место с ним дыркой - да
    //      является ли место с ним препятствием - нет
    @Override
    public List<Hero> activeHeroes() {
        return aliveActive().stream()
                .map(Player::getHero)
                .collect(toList());
    }

    @Override
    public Accessor<Hero> heroes() {
        return field.of(Hero.class);
    }

    @Override
    public boolean isBrick(Point pt) {
        return bricks().contains(pt);
    }

    @Override
    public boolean isHunter(Point pt) {
        return robbers().contains(pt) || isAnyHeroMaskAt(pt);
    }

    @PerformanceOptimized
    private boolean isAnyHeroMaskAt(Point pt) {
        for (Hero hero : heroes().getAt(pt)) {
            if (hero.under(PotionType.MASK_POTION)) {
                if (hero.equals(pt)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void leaveClue(Point pt, Class<? extends Point> type) {
        if (type == ClueKnife.class) {
            clueKnife().add(new ClueKnife(pt));
        } else if (type == ClueGlove.class) {
            clueGlove().add(new ClueGlove(pt));
        } else if (type == ClueRing.class) {
            clueRing().add(new ClueRing(pt));
        }
    }

    @Override
    public boolean under(Point pt, PotionType potion) {
        return heroes().stream()
                .filter(hero -> hero.equals(pt))
                .anyMatch(hero -> hero.under(potion));
    }

    @Override
    public int size() {
        return field.size();
    }

    @Override
    public boolean isBorder(Point pt) {
        return borders().contains(pt);
    }

    @Override
    protected void onAdd(Player player) {
        player.newHero(this);
    }

    @Override
    protected void onRemove(Player player) {
        heroes().removeExact(player.getHero());
    }

    @Override
    public GameSettings settings() {
        return settings;
    }

    public Accessor<Backway> backways() {
        return field.of(Backway.class);
    }

    public Accessor<ClueKnife> clueKnife() {
        return field.of(ClueKnife.class);
    }

    public Accessor<ClueGlove> clueGlove() {
        return field.of(ClueGlove.class);
    }

    public Accessor<ClueRing> clueRing() {
        return field.of(ClueRing.class);
    }

    public Accessor<Border> borders() {
        return field.of(Border.class);
    }

    @Override
    public Accessor<Robber> robbers() {
        return field.of(Robber.class);
    }

    @Override
    public Accessor<Brick> bricks() {
        return field.of(Brick.class);
    }

    @Override
    public List<Hero> visibleHeroes() {
        return activeHeroes().stream()   // TODO test что воры не гонятся за точками спауна
                .filter(Hero::isVisible)
                .collect(toList());
    }

    public Accessor<Ladder> ladder() {
        return field.of(Ladder.class);
    }

    public Accessor<Potion> potions() {
        return field.of(Potion.class);
    }

    public Accessor<Pipe> pipe() {
        return field.of(Pipe.class);
    }

    public int getBackwaysTimer() {
        return backwaysTimer;
    }

    @Override
    public Accessor<Door> doors() {
        return field.of(Door.class);
    }

    @Override
    public Accessor<Key> keys() {
        return field.of(Key.class);
    }

    public Accessor<Bullet> bullets() {
        return field.of(Bullet.class);
    }

    public int countPlayers() {
        return players.size();
    }
}
