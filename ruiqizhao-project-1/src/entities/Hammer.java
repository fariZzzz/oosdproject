package entities;

import bagel.Input;

/**
 * Represents a hammer in the game that Mario can collect.
 * When collected, it disappears from the game world and enhances Mario's abilities.
 */
public class Hammer extends Entity {

    /** Tracks whether the hammer has been collected by Mario */
    private boolean collected = false;

    /**
     * Constructs a Hammer object at the specified position.
     *
     * @param imagePath Path to the hammer image resource
     * @param x         X-coordinate (top-left)
     * @param y         Y-coordinate (top-left)
     */
    public Hammer(String imagePath, double x, double y) {
        super(imagePath, x, y);
    }

    /**
     * @return True if the hammer has been collected, false otherwise
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Marks the hammer as collected.
     * This prevents it from being drawn or collected again.
     */
    public void collect() {
        collected = true;
    }

    /**
     * Hammers have no update logic (no animation or interaction until collected).
     *
     * @param input Input from the game loop (unused)
     */
    @Override
    public void update(Input input) {
        // Hammer is passive and does not respond to input
    }

    /**
     * Draws the hammer on screen unless it has been collected.
     */
    @Override
    public void draw() {
        if (!collected) {
            image.drawFromTopLeft(x, y);
        }
    }
}
