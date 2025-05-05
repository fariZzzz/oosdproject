package pages;

import bagel.*;

/**
 * The EndPage displays the game over or victory screen.
 * It shows a main message, the final score (including a time bonus),
 * and a prompt to continue. Pressing SPACE returns the player to the HomePage.
 */
public class EndPage extends GamePage {

    // Background image of the screen
    private final Image background;

    // Messages to display
    private final String mainMessage;
    private final String scoreMessage;

    // Fonts for each text element
    private final Font messageFont;
    private final Font scoreFont;
    private final Font promptFont;

    // Y-coordinates for positioning text vertically
    private final double mainY;
    private final double scoreY;
    private final double promptY;

    /**
     * Constructs the EndPage with outcome-dependent messages and final score.
     *
     * @param gameWon   Whether the game was won or lost.
     * @param score     The score achieved through gameplay.
     * @param timeLeft  Remaining time in seconds, used to compute time bonus.
     */
    public EndPage(boolean gameWon, int score, int timeLeft) {
        background = new Image(GAME_PROPS.getProperty("backgroundImage"));

        // Set message content based on win/loss
        mainMessage = gameWon ? "CONGRATULATIONS, YOU WON!" : "GAME OVER, YOU LOST!";
        scoreMessage = "YOUR FINAL SCORE " + (score + timeLeft * 3);

        // Load fonts and their sizes
        messageFont = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("gameEnd.status.fontSize")));
        scoreFont = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("gameEnd.scores.fontSize")));
        promptFont = new Font(GAME_PROPS.getProperty("font"),
                Integer.parseInt(GAME_PROPS.getProperty("home.prompt.fontSize")));

        // Calculate fixed vertical positions
        double windowHeight = Double.parseDouble(GAME_PROPS.getProperty("window.height"));
        mainY = Double.parseDouble(GAME_PROPS.getProperty("gameEnd.status.y"));
        scoreY = mainY + 60;
        promptY = windowHeight - 100;
    }

    /**
     * Renders the end screen and checks for user input to return to home screen.
     *
     * @param input The current keyboard and mouse input.
     */
    @Override
    public void update(Input input) {
        double windowWidth = Double.parseDouble(GAME_PROPS.getProperty("window.width"));
        String prompt = "PRESS SPACE TO CONTINUE...";

        // Draw background image
        background.drawFromTopLeft(0, 0);

        // Center messages horizontally
        double mainX = (windowWidth - messageFont.getWidth(mainMessage)) / 2;
        double scoreX = (windowWidth - scoreFont.getWidth(scoreMessage)) / 2;
        double promptX = (windowWidth - promptFont.getWidth(prompt)) / 2;

        // Display game messages
        messageFont.drawString(mainMessage, mainX, mainY);
        scoreFont.drawString(scoreMessage, scoreX, scoreY);
        promptFont.drawString(prompt, promptX, promptY);

        // Go back to home screen on SPACE key
        if (input.wasPressed(Keys.SPACE)) {
            setNextPage(new HomePage());
        }
    }
}
