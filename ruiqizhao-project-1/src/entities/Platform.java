package entities;

import bagel.Image;
import bagel.util.Rectangle;

/**
 * Represents a static platform in the game world.
 * <p>
 * Platforms are solid surfaces that other entities (like Mario or barrels) can stand on.
 * Each platform is centered at (x, y) and rendered using a fixed platform image.
 */
public class Platform {
    private final double x, y;
    private final Image image;

    /**
     * Constructs a platform centered at the specified (x, y) coordinates.
     *
     * @param x The x-coordinate of the platform's center.
     * @param y The y-coordinate of the platform's center.
     */
    public Platform(double x, double y) {
        this.x = x;
        this.y = y;
        this.image = new Image("res/platform.png");
    }

    /**
     * Draws the platform image centered at its position.
     */
    public void draw() {
        image.drawFromTopLeft(
                x - image.getWidth() / 2,
                y - image.getHeight() / 2
        );
    }

    /**
     * Returns the bounding box used for collision detection.
     *
     * @return A Rectangle representing the platform's collision bounds.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(
                getLeftEdge(),
                getTopEdge(),
                image.getWidth(),
                image.getHeight()
        );
    }

    // --- Geometry helper methods ---

    /**
     * @return The y-coordinate of the top edge of the platform.
     */
    public double getTopEdge() {
        return y - image.getHeight() / 2;
    }

    /**
     * @return The y-coordinate of the bottom edge of the platform.
     */
    public double getBottomEdge() {
        return y + image.getHeight() / 2;
    }

    /**
     * @return The x-coordinate of the left edge of the platform.
     */
    public double getLeftEdge() {
        return x - image.getWidth() / 2;
    }

    /**
     * @return The x-coordinate of the right edge of the platform.
     */
    public double getRightEdge() {
        return x + image.getWidth() / 2;
    }

    /**
     * @return The height of the platform's image.
     */
    public double getHeight() {
        return image.getHeight();
    }
}
