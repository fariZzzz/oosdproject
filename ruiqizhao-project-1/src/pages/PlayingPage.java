/**
 * The PlayingPage class handles the core gameplay loop for Shadow Donkey Kong.
 * It manages the rendering and updating of all game entities, player interactions,
 * scoring logic, game win/lose conditions, and ladder/platform physics.
 */
package pages;

import bagel.*;
import entities.*;
import util.EntityLoader;

import java.util.*;

public class PlayingPage extends GamePage {
    // Constants for gameplay behavior and UI
    private static final int CLIMB_SPEED = 2;
    private static final int PLATFORM_SNAP_BUFFER = 5;
    private static final int SCORE_JUMP_OVER = 30;
    private static final int SCORE_BARREL_DESTROYED = 100;

    // Core game entities and UI elements
    private final Image background;
    private final Mario mario;
    private final Donkey donkey;
    private final Hammer hammer;
    private final List<Platform> platforms = new ArrayList<>();
    private final List<Ladder> ladders = new ArrayList<>();
    private final List<Barrel> barrels = new ArrayList<>();
    private final List<Barrel> barrelsScoredThisJump = new ArrayList<>();
    private final Font font;
    private final int scoreX, scoreY, maxFrames;

    // Game state trackers
    private int score = 0;
    private int frame = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean wasOnGroundLastFrame = true;

    /**
     * Constructs the playing page, initialising game assets and entities.
     */
    public PlayingPage() {
        Properties gameProps = GAME_PROPS;
        Properties messageProps = MESSAGE_PROPS;

        background = new Image(gameProps.getProperty("backgroundImage"));

        mario = new Mario("res/mario_right.png",
                Double.parseDouble(gameProps.getProperty("mario.start.x")),
                Double.parseDouble(gameProps.getProperty("mario.start.y")));

        donkey = new Donkey("res/donkey_kong.png",
                Double.parseDouble(gameProps.getProperty("donkey.start.x")),
                Double.parseDouble(gameProps.getProperty("donkey.start.y")));

        hammer = new Hammer("res/hammer.png",
                Double.parseDouble(gameProps.getProperty("hammer.start.x")),
                Double.parseDouble(gameProps.getProperty("hammer.start.y")));

        initializePlatforms(gameProps);
        initializeLadders();
        initializeBarrels();

        font = new Font(gameProps.getProperty("font"),
                Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize")));
        scoreX = Integer.parseInt(gameProps.getProperty("gamePlay.score.x"));
        scoreY = Integer.parseInt(gameProps.getProperty("gamePlay.score.y"));
        maxFrames = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
    }

    /**
     * Main update method called every frame. Controls logic flow, rendering,
     * physics, input, and win/loss state transitions.
     */
    @Override
    public void update(Input input) {
        mario.tickClimbingBuffer();

        int timeLeft = (maxFrames - frame) / 60;
        if (gameOver || gameWon) {
            GamePage.setNextPage(new EndPage(gameWon, score, timeLeft));
            return;
        }

        frame++;
        background.drawFromTopLeft(0, 0);

        mario.update(input);
        mario.applyGravityIfNeeded();

        donkey.update(input);
        donkey.applyGravity();

        checkBarrelJumpScore();
        updateBarrels(input);
        handleHammerPickup();
        checkWinOrLoseConditions();

        checkPlatformCollision(mario);
        checkPlatformCollision(donkey);
        for (Barrel b : barrels) checkPlatformCollision(b);

        handleLadderClimbing(input);

        drawAll();
        drawScore(timeLeft);
    }

    /** Loads and creates platform entities from config. */
    private void initializePlatforms(Properties gameProps) {
        String[] platformData = gameProps.getProperty("platforms").split(";");
        for (String coord : platformData) {
            String[] xy = coord.split(",");
            platforms.add(new Platform(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])));
        }
    }

    /** Loads ladder entities and aligns them above platforms. */
    private void initializeLadders() {
        List<Ladder> loadedLadders = EntityLoader.loadLadders("res/app.properties");
        for (Ladder ladder : loadedLadders) {
            for (Platform platform : platforms) {
                if (ladder.isOverlappingPlatform(platform)) {
                    ladder.snapAbovePlatform(platform);
                    break;
                }
            }
            ladders.add(ladder);
        }
    }

    /** Loads barrels and aligns them on platforms. */
    private void initializeBarrels() {
        List<Barrel> loadedBarrels = EntityLoader.loadBarrels("res/app.properties");
        for (Barrel barrel : loadedBarrels) {
            for (Platform platform : platforms) {
                if (barrel.isOverlappingPlatform(platform)) {
                    barrel.snapAbovePlatform(platform);
                    break;
                }
            }
            barrels.add(barrel);
        }
    }

    /**
     * Scores Mario's successful jump over a barrel if aligned and not obstructed by a platform.
     */
    private void checkBarrelJumpScore() {
        if (!mario.isJumping()) return;

        for (Barrel b : barrels) {
            if (barrelsScoredThisJump.contains(b)) continue;

            boolean horizontallyAligned = mario.getRightEdge() >= b.getLeftEdge()
                    && mario.getLeftEdge() <= b.getRightEdge();
            boolean marioAbove = mario.getBottomEdge() < b.getTopEdge();

            if (horizontallyAligned && marioAbove && !isBlockedByPlatform(mario, b)) {
                score += SCORE_JUMP_OVER;
                barrelsScoredThisJump.add(b);
            }
        }

        boolean landed = mario.isOnGround() && !wasOnGroundLastFrame;
        if (landed) barrelsScoredThisJump.clear();
        wasOnGroundLastFrame = mario.isOnGround();
    }

    /**
     * Checks if a platform exists between Mario and a barrel blocking the jump score.
     */
    private boolean isBlockedByPlatform(Mario mario, Barrel b) {
        for (Platform p : platforms) {
            boolean overlapsHorizontally = p.getRightEdge() >= mario.getLeftEdge()
                    && p.getLeftEdge() <= mario.getRightEdge();
            boolean platformBetween = p.getTopEdge() < b.getTopEdge()
                    && p.getTopEdge() > mario.getBottomEdge();
            if (overlapsHorizontally && platformBetween) return true;
        }
        return false;
    }

    /**
     * Updates barrel logic, including interaction with Mario and drawing.
     */
    private void updateBarrels(Input input) {
        Iterator<Barrel> iterator = barrels.iterator();
        while (iterator.hasNext()) {
            Barrel b = iterator.next();
            b.update(input);

            if (mario.hasHammer() && mario.getBoundingBox().intersects(b.getBoundingBox())) {
                iterator.remove();
                score += SCORE_BARREL_DESTROYED;
            } else if (!mario.hasHammer() && mario.getBoundingBox().intersects(b.getBoundingBox())) {
                gameOver = true;
            } else {
                b.draw();
            }
        }
    }

    /** Handles hammer pickup and gives Mario invincibility. */
    private void handleHammerPickup() {
        if (!hammer.isCollected() && mario.getBoundingBox().intersects(hammer.getBoundingBox())) {
            hammer.collect();
            mario.collectHammer();
        }
    }

    /** Determines whether Mario has reached win or lose conditions. */
    private void checkWinOrLoseConditions() {
        if (!mario.hasHammer() && mario.getBoundingBox().intersects(donkey.getBoundingBox())) {
            gameOver = true;
        }
        if (mario.hasHammer() && mario.getBoundingBox().intersects(donkey.getBoundingBox())) {
            gameWon = true;
        }
        if (frame >= maxFrames) {
            gameOver = true;
        }
    }

    /** Draws all entities in the correct order. */
    private void drawAll() {
        for (Platform p : platforms) p.draw();
        for (Ladder l : ladders) l.draw();
        donkey.draw();
        mario.draw();
        hammer.draw();
    }

    /** Draws score and time left on screen. */
    private void drawScore(int timeLeft) {
        font.drawString("SCORE " + score, scoreX, scoreY);
        font.drawString("TIME LEFT " + timeLeft, scoreX, scoreY + 30);
    }

    /** Handles collision between entities and platforms. */
    private boolean checkPlatformCollision(Entity entity) {
        if (entity instanceof Mario mario && mario.isClimbingBuffered()) return false;

        double velocityY = entity.getVelocityY();
        double currentBottom = entity.getBottomEdge();
        double futureBottom = entity.getY() + velocityY + entity.getHeight();

        for (Platform p : platforms) {
            if (isFallingOntoPlatform(entity, p, currentBottom, futureBottom, velocityY)) {
                entity.setY(p.getTopEdge() - entity.getHeight());
                entity.stopFalling();
                if (entity instanceof Mario m) m.setOnGround(true);
                return true;
            }
        }
        return false;
    }

    /** Checks if an entity is falling onto a platform based on future Y position. */
    private boolean isFallingOntoPlatform(Entity entity, Platform p, double currentBottom, double futureBottom, double velocityY) {
        boolean horizontalOverlap = entity.getRightEdge() >= p.getLeftEdge()
                && entity.getLeftEdge() <= p.getRightEdge();

        boolean fallingOntoPlatform = currentBottom <= p.getTopEdge() + PLATFORM_SNAP_BUFFER &&
                futureBottom >= p.getTopEdge() && velocityY > 0;

        return horizontalOverlap && fallingOntoPlatform;
    }

    /** Handles Mario's interaction with ladders and climbing mechanics. */
    private void handleLadderClimbing(Input input) {
        boolean onLadder = false;

        for (Ladder ladder : ladders) {
            if (mario.getBoundingBox().intersects(ladder.getBoundingBox())) {
                onLadder = true;

                if (input.isDown(Keys.UP)) {
                    mario.setY(mario.getTopEdge() - CLIMB_SPEED);
                    mario.setClimbing(true);
                    mario.setOnGround(true);
                    return;
                } else if (input.isDown(Keys.DOWN)) {
                    if (!canClimbDown(ladder)) return;
                    mario.setY(mario.getY() + CLIMB_SPEED);
                    mario.setClimbing(true);
                    mario.setOnGround(true);
                    return;
                } else {
                    mario.setClimbing(true);
                    mario.setOnGround(true);
                    return;
                }
            }
        }

        for (Ladder ladder : ladders) {
            if (input.isDown(Keys.DOWN) && mario.isAboveLadder(ladder)) {
                if (isOnPlatformAbove(ladder)) {
                    mario.setClimbing(true);
                    mario.setY(mario.getY() + CLIMB_SPEED);
                    mario.setOnGround(true);
                    return;
                }
            }
        }

        if (!onLadder) {
            mario.setClimbing(false);
            mario.setOnGround(isStandingOnPlatform());
        }
    }

    /**
     * Checks if Mario can descend without being blocked by a non-ladder-covered platform.
     */
    private boolean canClimbDown(Ladder ladder) {
        for (Platform p : platforms) {
            boolean horizontalOverlap = mario.getRightEdge() >= p.getLeftEdge()
                    && mario.getLeftEdge() <= p.getRightEdge();

            boolean intersectsPlatform = mario.getBottomEdge() >= p.getTopEdge()
                    && mario.getY() + CLIMB_SPEED + mario.getHeight() <= p.getTopEdge() + p.getHeight();

            boolean ladderCoversPlatform = ladder.getTopEdge() <= p.getTopEdge()
                    && ladder.getBottomEdge() >= p.getTopEdge() + p.getHeight();

            if (horizontalOverlap && intersectsPlatform && !ladderCoversPlatform) {
                mario.setY(p.getTopEdge() - mario.getHeight());
                mario.setClimbing(false);
                mario.setOnGround(true);
                return false;
            }
        }
        return true;
    }

    /** Checks if Mario is on a platform directly above a ladder and aligned with it. */
    private boolean isOnPlatformAbove(Ladder ladder) {
        for (Platform p : platforms) {
            boolean closeToPlatform = Math.abs(mario.getBottomEdge() - p.getTopEdge()) <= 2;
            boolean overlapsPlatform = mario.getRightEdge() >= p.getLeftEdge()
                    && mario.getLeftEdge() <= p.getRightEdge();
            boolean overlapsLadder = mario.getRightEdge() >= ladder.getLeftEdge()
                    && mario.getLeftEdge() <= ladder.getRightEdge();

            if (closeToPlatform && overlapsPlatform && overlapsLadder) {
                return true;
            }
        }
        return false;
    }

    /** Checks if Mario is standing on any platform. */
    private boolean isStandingOnPlatform() {
        for (Platform p : platforms) {
            boolean horizontal = mario.getRightEdge() >= p.getLeftEdge()
                    && mario.getLeftEdge() <= p.getRightEdge();

            boolean onTop = Math.abs(mario.getBottomEdge() - p.getTopEdge()) <= 2;

            if (horizontal && onTop) return true;
        }
        return false;
    }
}
