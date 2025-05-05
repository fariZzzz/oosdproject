package entities;

import bagel.Input;
import bagel.util.Rectangle;

/**
 * Represents a barrel entity in the game.
 * Barrels are passive objects affected by gravity,
 * and interact with platforms and Mario (for score or game over).
 */
public class Barrel extends Entity {

    /**
     * Creates a new barrel centered at the given coordinates.
     *
     * @param centerX X coordinate of the center
     * @param centerY Y coordinate of the center
     */
    public Barrel(double centerX, double centerY) {
        super("res/barrel.png", 0, 0);
        this.x = centerX - image.getWidth() / 2;
        this.y = centerY - image.getHeight() / 2;
    }

    /**
     * Updates the barrel's state.
     * Currently, applies gravity to make the barrel fall.
     *
     * @param input The current input state (unused for barrels)
     */
    @Override
    public void update(Input input) {
        applyGravity();
    }

    /**
     * Draws the barrel at its current position.
     */
    @Override
    public void draw() {
        image.drawFromTopLeft(x, y);
    }

    /**
     * Checks if this barrel is overlapping a given platform.
     *
     * @param platform The platform to check against
     * @return true if the barrel overlaps the platform, false otherwise
     */
    public boolean isOverlappingPlatform(Platform platform) {
        return this.getBoundingBox().intersects(platform.getBoundingBox());
    }

    /**
     * Aligns this barrel so that it rests directly above a given platform.
     *
     * @param platform The platform to align above
     */
    public void snapAbovePlatform(Platform platform) {
        this.y = platform.getTopEdge() - this.getHeight();
    }

    /**
     * Gets the bounding box for collision detection.
     *
     * @return The bounding box rectangle
     */
    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }
}
