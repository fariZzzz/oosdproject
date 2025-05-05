package pages;

import bagel.*;

/**
 * Represents the home screen of the game.
 * Displays the game title and prompt message, and transitions to the playing page when ENTER is pressed.
 */
public class HomePage extends GamePage {
    private final Image background;
    private final Font titleFont;
    private final Font promptFont;
    private final String title;
    private final String prompt;
    private final double titleX, titleY;
    private final double promptX, promptY;

    /**
     * Initialises the HomePage by loading background, text content, and layout settings from game properties.
     */
    public HomePage() {
        background = new Image(GAME_PROPS.getProperty("backgroundImage"));

        double windowWidth = Double.parseDouble(GAME_PROPS.getProperty("window.width"));

        title = MESSAGE_PROPS.getProperty("home.title");
        prompt = MESSAGE_PROPS.getProperty("home.prompt");

        titleFont = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("home.title.fontSize")));
        promptFont = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("home.prompt.fontSize")));

        titleY = Double.parseDouble(GAME_PROPS.getProperty("home.title.y"));
        promptY = Double.parseDouble(GAME_PROPS.getProperty("home.prompt.y"));

        // Center the text horizontally
        titleX = (windowWidth - titleFont.getWidth(title)) / 2;
        promptX = (windowWidth - promptFont.getWidth(prompt)) / 2;
    }

    /**
     * Renders the home screen and listens for ENTER key to begin the game.
     * @param input The user's current keyboard input.
     */
    @Override
    public void update(Input input) {
        // Draw background and text
        background.drawFromTopLeft(0, 0);
        titleFont.drawString(title, titleX, titleY);
        promptFont.drawString(prompt, promptX, promptY);

        // Start game when ENTER is pressed
        if (input.wasPressed(Keys.ENTER)) {
            setNextPage(new PlayingPage());
        }
    }
}
