package entities;

import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Rectangle;

/**
 * Represents the controllable player character Mario.
 * Handles movement, jumping, climbing, hammer collection, and animation logic.
 */
public class Mario extends Entity {

    private boolean onGround = false;
    private boolean jumping = false;
    private boolean hasHammer = false;
    private boolean climbing = false;
    private boolean facingRight = true;
    private int climbingBuffer = 0;

    /**
     * Constructs a new Mario instance with image and starting position.
     *
     * @param imagePath Path to Mario's default image.
     * @param x         Initial x-coordinate (top-left).
     * @param y         Initial y-coordinate (top-left).
     */
    public Mario(String imagePath, double x, double y) {
        super(imagePath, x, y);
        updateImage();
    }

    /**
     * Basic update method required by the Entity interface.
     *
     * @param input Bagel input handler for key state.
     */
    @Override
    public void update(Input input) {
        update(input, null);
    }

    /**
     * Updates Mario's position based on user input.
     *
     * @param input     Input from Bagel (keyboard).
     * @param blockList Optional: not used here, but useful for obstacle checks.
     */
    public void update(Input input, java.util.List<Entity> blockList) {
        final double MOVE_SPEED = 3.5;
        final double JUMP_SPEED = -5;
        final double SCREEN_WIDTH = 1024;

        double proposedX = x;
        boolean movingLeft = input.isDown(Keys.LEFT);
        boolean movingRight = input.isDown(Keys.RIGHT);

        if (movingLeft) {
            proposedX -= MOVE_SPEED;
            facingRight = false;
        } else if (movingRight) {
            proposedX += MOVE_SPEED;
            facingRight = true;
        }

        x = proposedX;

        if (onGround && !climbing && input.wasPressed(Keys.SPACE)) {
            velocityY = JUMP_SPEED;
            jumping = true;
            onGround = false;
        }

        // Keep within screen bounds
        if (x < 0) x = 0;
        if (x + image.getWidth() > SCREEN_WIDTH) x = SCREEN_WIDTH - image.getWidth();

        updateImage();
    }

    /**
     * Updates Mario's current sprite based on direction and hammer status.
     */
    private void updateImage() {
        if (hasHammer) {
            image = new Image(facingRight ? "res/mario_hammer_right.png" : "res/mario_hammer_left.png");
        } else {
            image = new Image(facingRight ? "res/mario_right.png" : "res/mario_left.png");
        }
    }

    /**
     * Applies gravity to Mario only if he's not grounded or climbing.
     */
    public void applyGravityIfNeeded() {
        final double TERMINAL_VELOCITY = 10;

        if (!onGround && !climbing) {
            velocityY = Math.min(velocityY + 0.2, TERMINAL_VELOCITY);
            y += velocityY;
        }
    }

    /**
     * Gets Mario's bounding box for collision detection.
     *
     * @return A rectangle representing Mario's bounds.
     */
    @Override
    public Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(new bagel.util.Point(
                x + image.getWidth() / 2,
                y + image.getHeight() / 2));
    }

    /**
     * Renders Mario's current sprite at the top-left position.
     */
    @Override
    public void draw() {
        image.drawFromTopLeft(x, y);
    }

    /**
     * Sets Mario's grounded state.
     *
     * @param value True if grounded; false otherwise.
     */
    public void setOnGround(boolean value) {
        this.onGround = value;
        if (value) {
            jumping = false;
            velocityY = 0;
        }
    }

    /** @return True if Mario is on the ground. */
    public boolean isOnGround() {
        return onGround;
    }

    /** @return True if Mario is in a jump. */
    public boolean isJumping() {
        return jumping;
    }

    /** @return True if Mario is currently climbing. */
    public boolean isClimbing() {
        return climbing;
    }

    /**
     * Sets climbing state and triggers climbing buffer.
     *
     * @param climbing True if Mario is climbing.
     */
    public void setClimbing(boolean climbing) {
        this.climbing = climbing;
        if (climbing) {
            this.climbingBuffer = 4;
        }
    }

    /**
     * Buffered climbing state to smooth climbing logic.
     *
     * @return True if climbing or within the climbing buffer window.
     */
    public boolean isClimbingBuffered() {
        return climbing || climbingBuffer > 0;
    }

    /** Ticks down the climbing buffer counter. */
    public void tickClimbingBuffer() {
        if (climbingBuffer > 0) {
            climbingBuffer--;
        }
    }

    /** @return True if Mario currently holds the hammer. */
    public boolean hasHammer() {
        return hasHammer;
    }

    /** Marks the hammer as collected and updates Mario's image. */
    public void collectHammer() {
        this.hasHammer = true;
        updateImage();
    }

    /**
     * Checks whether Mario is just above a given ladder.
     *
     * @param ladder Ladder to check.
     * @return True if Mario is horizontally aligned and standing above the ladder.
     */
    public boolean isAboveLadder(Ladder ladder) {
        double marioCenterX = x + image.getWidth() / 2;
        boolean horizontallyAligned = marioCenterX >= ladder.getLeftEdge() &&
                marioCenterX <= ladder.getRightEdge();
        boolean standingAbove = Math.abs(getBottomEdge() - ladder.getTopEdge()) <= 10;
        return horizontallyAligned && standingAbove;
    }

    /** @return Width of Mario's current sprite. */
    public double getWidth() {
        return image.getWidth();
    }

    /** @return Mario's current x-coordinate. */
    public double getX() { return x; }

    /** @return Mario's current y-coordinate. */
    public double getY() { return y; }
}
