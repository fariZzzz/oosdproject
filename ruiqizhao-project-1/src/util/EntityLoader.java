package util;

import entities.Ladder;
import entities.Barrel;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Utility class responsible for loading entity data (like ladders and barrels)
 * from a properties file at runtime.
 */
public class EntityLoader {

    /**
     * Loads ladder entities from the specified properties file.
     * The properties must include:
     * - ladder.count: total number of ladders
     * - ladder.1, ladder.2, ..., ladder.n: each entry contains "x,y"
     *
     * @param path The file path to the properties file (e.g., "res/app.properties")
     * @return A list of Ladder objects
     */
    public static List<Ladder> loadLadders(String path) {
        List<Ladder> ladders = new ArrayList<>();
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(path));
            int count = Integer.parseInt(props.getProperty("ladder.count"));

            for (int i = 1; i <= count; i++) {
                String[] parts = props.getProperty("ladder." + i).split(",");
                double x = Double.parseDouble(parts[0].trim());
                double y = Double.parseDouble(parts[1].trim());
                ladders.add(new Ladder(x, y));
            }
        } catch (Exception e) {
            System.err.println("Failed to load ladders: " + e.getMessage());
        }
        return ladders;
    }

    /**
     * Loads barrel entities from the specified properties file.
     * The properties must include:
     * - barrel.count: total number of barrels
     * - barrel.1, barrel.2, ..., barrel.n: each entry contains "x,y"
     *
     * @param path The file path to the properties file
     * @return A list of Barrel objects
     */
    public static List<Barrel> loadBarrels(String path) {
        List<Barrel> barrels = new ArrayList<>();
        try {
            Properties props = IOUtils.readPropertiesFile(path);
            int count = Integer.parseInt(props.getProperty("barrel.count"));
            for (int i = 1; i <= count; i++) {
                String[] coords = props.getProperty("barrel." + i).split(",");
                double x = Double.parseDouble(coords[0].trim());
                double y = Double.parseDouble(coords[1].trim());
                barrels.add(new Barrel(x, y));
            }
        } catch (Exception e) {
            System.err.println("Failed to load barrels: " + e.getMessage());
        }
        return barrels;
    }
}
