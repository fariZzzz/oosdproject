/**
 * Abstract base class representing a generic entity in the game.
 * <p>
 * Provides common functionality such as:
 * - Positioning
 * - Gravity and vertical movement
 * - Image rendering
 * - Collision detection via bounding box
 * <p>
 * All game elements (e.g., Mario, Donkey, Barrel) should extend from this.
 */

package entities;

import bagel.Image;
import bagel.Input;
import bagel.util.Rectangle;

public abstract class Entity {
    // Position (top-left corner)
    protected double x, y;

    // Current vertical velocity and gravity constants
    protected double velocityY = 0;
    protected final double gravity = 0.2;
    protected double terminalVelocity = 10;

    // Image used for rendering this entity
    protected Image image;

    /**
     * Constructs a new entity with image and position.
     *
     * @param imagePath Path to the entity's image
     * @param x         Initial x-position (top-left)
     * @param y         Initial y-position (top-left)
     */
    public Entity(String imagePath, double x, double y) {
        this.image = new Image(imagePath);
        this.x = x;
        this.y = y;
    }

    /**
     * Applies gravity to the entity, increasing downward velocity
     * and updating its vertical position.
     */
    public void applyGravity() {
        velocityY = Math.min(velocityY + gravity, terminalVelocity);
        y += velocityY;
    }

    /**
     * Stops downward movement by setting vertical velocity to zero.
     */
    public void stopFalling() {
        velocityY = 0;
    }

    /**
     * Returns the bounding box used for collision detection.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    /**
     * @return Current vertical velocity
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Sets the vertical (y) position of the entity.
     *
     * @param newY New y-position
     */
    public void setY(double newY) {
        this.y = newY;
    }

    /**
     * @return Height of the entity (based on image height)
     */
    public double getHeight() {
        return image.getHeight();
    }

    /**
     * Checks if this entity intersects with another.
     *
     * @param other Another entity
     * @return True if bounding boxes intersect
     */
    public boolean intersects(Entity other) {
        return this.getBoundingBox().intersects(other.getBoundingBox());
    }

    // === Edge and Position Helpers ===

    public double getTopEdge() {
        return y;
    }

    public double getBottomEdge() {
        return y + image.getHeight();
    }

    public double getLeftEdge() {
        return x;
    }

    public double getRightEdge() {
        return x + image.getWidth();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * Renders the entity image at its current top-left coordinates.
     */
    public void draw() {
        image.drawFromTopLeft(x, y);
    }

    /**
     * Abstract method to be implemented by subclasses to define entity behavior.
     *
     * @param input Keyboard and mouse input handler
     */
    public abstract void update(Input input);
}
