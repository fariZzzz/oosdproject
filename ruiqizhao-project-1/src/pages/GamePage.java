package pages;

import bagel.Input;
import java.util.Properties;

/**
 * Abstract base class for all pages in the game (e.g., HomePage, PlayingPage, EndPage).
 * Provides static property storage and transition handling between pages.
 */
public abstract class GamePage {

    /** Global properties loaded from config files */
    public static Properties GAME_PROPS;
    public static Properties MESSAGE_PROPS;

    /** Reference to the next page to transition to */
    private static GamePage nextPage = null;

    /**
     * Sets shared properties used by all pages.
     *
     * @param gameProps     Game configuration properties (e.g., dimensions, assets)
     * @param messageProps  Localised text or messages for display
     */
    public static void setProps(Properties gameProps, Properties messageProps) {
        GAME_PROPS = gameProps;
        MESSAGE_PROPS = messageProps;
    }

    /**
     * Requests a transition to another page.
     *
     * @param page The page to transition to on the next update cycle
     */
    public static void setNextPage(GamePage page) {
        nextPage = page;
    }

    /**
     * Returns the next page (if set), and resets the transition.
     *
     * @return The next GamePage to load, or null if no transition is requested
     */
    public static GamePage consumeNextPage() {
        GamePage temp = nextPage;
        nextPage = null;
        return temp;
    }

    /**
     * Abstract update method that all page subclasses must implement.
     * Called every frame to render and process input.
     *
     * @param input The current input state from keyboard/mouse
     */
    public abstract void update(Input input);
}
