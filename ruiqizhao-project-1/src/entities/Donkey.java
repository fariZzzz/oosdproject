package entities;

import bagel.Input;

/**
 * Represents Donkey Kong in the game.
 * <p>
 * Donkey is a passive entity that remains stationary and simply falls due to gravity.
 * It has a lower terminal velocity than Mario.
 * <p>
 * This entity serves as the goal for the player to reach when holding a hammer,
 * or the lose condition when collided with without a hammer.
 */
public class Donkey extends Entity {

    /**
     * Constructs a Donkey object at a specified position.
     *
     * @param imagePath Path to the Donkey image.
     * @param x         X-coordinate (top-left) of Donkey.
     * @param y         Y-coordinate (top-left) of Donkey.
     */
    public Donkey(String imagePath, double x, double y) {
        super(imagePath, x, y);
        // Separate maximum fall speed of 5
        this.terminalVelocity = 5;
    }

    /**
     * Updates Donkey's state each frame.
     * <p>
     * Currently, Donkey is stationary and only subject to gravity.
     * This can be extended for future animations or interactions.
     *
     * @param input The current frame's user input (unused).
     */
    @Override
    public void update(Input input) {
        applyGravity();
    }
}
