import bagel.*;
import java.util.Properties;
import pages.*;
import util.IOUtils;

/**
 * The main class for the Shadow Donkey Kong game.
 * This class extends {@code AbstractGame} and is responsible for managing game initialisation,
 * updates, rendering, and handling user input.
 * <p>
 * It sets up the game world, initialises characters, platforms, ladders, and other game objects,
 * and runs the game loop to ensure smooth gameplay.
 */
public class ShadowDonkeyKong extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;
    private GamePage currentPage;


    public ShadowDonkeyKong(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        this.currentPage = null; // Let update() handle first page setup

    }


    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        // Initial setup
        if (GamePage.GAME_PROPS == null) {
            GamePage.setProps(GAME_PROPS, MESSAGE_PROPS);
            currentPage = new HomePage();
        }

        currentPage.update(input);

        GamePage nextPage = GamePage.consumeNextPage();
        if (nextPage != null) {
            currentPage = nextPage;
        }
    }





    /**
     * The main entry point of the Shadow Donkey Kong game.
     * <p>
     * This method loads the game properties and message files, initialises the game,
     * and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message_en.properties");

        ShadowDonkeyKong game = new ShadowDonkeyKong(gameProps, messageProps);

        game.run();
    }


}
