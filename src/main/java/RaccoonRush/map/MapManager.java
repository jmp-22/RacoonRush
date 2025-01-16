package RaccoonRush.map;

import RaccoonRush.entity.Entity;
import RaccoonRush.entity.Player;
import RaccoonRush.game.GamePanel;
import RaccoonRush.game.GameManager;
import RaccoonRush.map.tile.Item;
import RaccoonRush.map.tile.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * MapManager class is used to manage the map of the game.
 * It is used to load the map, draw the map, and handle collisions.
 */
public class MapManager implements GameManager {
    private final GamePanel gamePanel;
    private final ItemManager itemManager;
    private final MapLoader mapLoader;
    private Tile[][] map;
    private final BufferedImage[] background;

    /**
     * Constructor for MapManager class
     * @param gamePanel GamePanel object
     */
    public MapManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        itemManager = new ItemManager(gamePanel);
        mapLoader = new MapLoader();
        // Background is made of 4 background tiles each 768x768 in size that are drawn in a 2x2 grid
        background = gamePanel.getImageLoader().getBackgroundImages().toArray(new BufferedImage[0]);
    }

    /**
     * Method to get the screen coordinate of a tile
     * This allows the map to be drawn relative to the player's screen, emulating camera movement
     * @param index index of the tile
     * @param world world coordinate
     * @param screen screen coordinate
     * @return screen coordinate
     */
    public int getScreenCoordinate(int index, int world, int screen) {
        return index * GamePanel.config.tileSize() - world + screen;
    }

    /**
     * Method to draw the background of the map
     * @param g2 Graphics2D object
     */
    private void drawBackground(Graphics2D g2) {
        Player player = gamePanel.getEntityManager().getPlayer();
        int worldX = player.getWorldX();
        int worldY = player.getWorldY();
        int screenX = player.getScreenX();
        int screenY = player.getScreenY();
        int size = GamePanel.config.screenWidth();

        g2.drawImage(background[0], getScreenCoordinate(0, worldX, screenX), getScreenCoordinate(0, worldY, screenY), size, size, null);
        g2.drawImage(background[1], getScreenCoordinate(0, worldX, screenX) + size, getScreenCoordinate(0, worldY, screenY), size, size, null);
        g2.drawImage(background[2], getScreenCoordinate(0, worldX, screenX), getScreenCoordinate(0, worldY, screenY) + size, size, size, null);
        g2.drawImage(background[3], getScreenCoordinate(0, worldX, screenX) + size, getScreenCoordinate(0, worldY, screenY) + size, size, size, null);
    }

    /**
     * Method to draw a tile on the screen
     * @param g2 Graphics2D object
     * @param i row index of the tile
     * @param j column index of the tile
     */
    private void drawTile(Graphics2D g2, int i, int j) {
        Player player = gamePanel.getEntityManager().getPlayer();

        int screenX = getScreenCoordinate(j, player.getWorldX(), player.getScreenX());
        int screenY = getScreenCoordinate(i, player.getWorldY(), player.getScreenY());

        g2.drawImage(map[i][j].getImage(i, j, gamePanel.getItemAnimationFrame()), screenX, screenY, GamePanel.config.tileSize(), GamePanel.config.tileSize(), null);
    }

    /**
     * Method to get the drawing start index of the map
     * This means that the tiles are only drawn if they are within the screen
     * @param world world coordinate
     * @param screen screen coordinate
     * @return start index
     */
    private int getStart(int world, int screen) {
        return Math.max((world - screen) / GamePanel.config.tileSize(), 0);
    }

    /**
     * Method to get the drawing end index of the map
     * This means that the tiles are only drawn if they are within the screen
     * @param world world coordinate
     * @param screen screen coordinate
     * @param max maximum index
     * @return end index
     */
    private int getEnd(int world, int screen, int max) {
        return Math.min((world + screen) / GamePanel.config.tileSize() + 2, max);
    }

    /**
     * Method to update the map
     */
    public void update() {
        itemManager.update();
    }

    /**
     * Method to draw the map on the screen
     * @param g2 Graphics2D object
     */
    public void draw(Graphics2D g2) {
        Player player = gamePanel.getEntityManager().getPlayer();
        int startX = getStart(player.getWorldX(), player.getScreenX());
        int startY = getStart(player.getWorldY(), player.getScreenY());
        int endX = getEnd(player.getWorldX(), player.getScreenX(), GamePanel.config.maxWorldCol());
        int endY = getEnd(player.getWorldY(), player.getScreenY(), GamePanel.config.maxWorldRow());

        // Draw the background first before the tiles
        drawBackground(g2);
        for (int i = startY; i < endY; i++) {
            for (int j = startX; j < endX; j++) {
                if (map[i][j] != null) {
                    drawTile(g2, i, j);
                }
            }
        }
    }

    /**
     * Method to call the MapLoader to load the map from a text file
     * Loads the map array with Tile objects
     * @param filePath path to the text file
     */
    public void loadMap(String filePath) {
        map = mapLoader.loadMap(itemManager, filePath, GamePanel.config.maxWorldCol(), GamePanel.config.maxWorldRow());
    }

    /**
     * Method to check the collision of the player with a tile
     * @param row row index of the tile
     * @param column column index of the tile
     * @return true if the player can move to the tile, false otherwise
     */
    public boolean onCollide(int row, int column, Entity entity) {
        if (row < 0 || column < 0 || row >= map.length || column >= map[row].length) {
            return false;
        }
        if (map[row][column] == null) {
            return true;
        }
        if (entity instanceof Player && map[row][column] instanceof Item item && !item.isCollected()) {
            itemManager.collectItem(item);
        }
        return map[row][column].onCollide(entity);
    }

    /**
     * Method to get the number of donuts left
     * @return the number of donuts left
     */
    public int getDonutsLeft() {
        return itemManager.getDonutsLeft();
    }

    /**
     * Method to get the map array
     * @return the map array
     */
    public Tile[][] getMap() {
        return map;
    }
}