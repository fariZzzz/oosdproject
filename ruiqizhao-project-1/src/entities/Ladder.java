package entities;

import bagel.Image;
import bagel.util.Rectangle;

/**
 * Represents a static ladder in the game world.
 * <p>
 * Ladders allow Mario to climb vertically when aligned.
 * They are rendered using a fixed image and support snapping
 * to the top of a platform for precise alignment.
 */
public class Ladder {
    private final double x;
    private double y;
    private final Image image;

    /**
     * Constructs a ladder centered at the given coordinates.
     *
     * @param x The x-coordinate of the ladder's center.
     * @param y The y-coordinate of the ladder's center.
     */
    public Ladder(double x, double y) {
        this.x = x;
        this.y = y;
        this.image = new Image("res/ladder.png");
    }

    /**
     * Draws the ladder image centered at its position.
     */
    public void draw() {
        image.drawFromTopLeft(
                getLeftEdge(),
                getTopEdge()
        );
    }

    /**
     * Returns the bounding box of the ladder for collision detection.
     *
     * @return A Rectangle representing the ladder's area.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle(
                getLeftEdge(),
                getTopEdge(),
                image.getWidth(),
                image.getHeight()
        );
    }

    // --- Edge accessors ---

    /**
     * @return The left x-coordinate of the ladder.
     */
    public double getLeftEdge() {
        return x - image.getWidth() / 2;
    }

    /**
     * @return The right x-coordinate of the ladder.
     */
    public double getRightEdge() {
        return x + image.getWidth() / 2;
    }

    /**
     * @return The top y-coordinate of the ladder.
     */
    public double getTopEdge() {
        return y - image.getHeight() / 2;
    }

    /**
     * @return The bottom y-coordinate of the ladder.
     */
    public double getBottomEdge() {
        return y + image.getHeight() / 2;
    }

    /**
     * Snaps the ladder so that its bottom aligns with the top of a platform.
     *
     * @param platform The platform to snap above.
     */
    public void snapAbovePlatform(Platform platform) {
        this.y = platform.getTopEdge() - image.getHeight() / 2;
    }

    /**
     * Checks if this ladder overlaps with a given platform's bounding box.
     *
     * @param platform The platform to compare with.
     * @return True if overlapping, false otherwise.
     */
    public boolean isOverlappingPlatform(Platform platform) {
        return this.getBoundingBox().intersects(platform.getBoundingBox());
    }
}
