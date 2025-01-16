package RaccoonRush.entity;

import RaccoonRush.entity.enemy.Enemy;
import RaccoonRush.entity.enemy.EnemyType;
import RaccoonRush.entity.enemy.Raccoon;
import RaccoonRush.game.GameManager;
import RaccoonRush.game.GamePanel;
import RaccoonRush.util.ImageLoader;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * EntityManager class manages the player and enemies
 * This class should only have one instance
 */
public class EntityManager implements GameManager {
    private final GamePanel gamePanel;
    private final Player player;
    private final ArrayList<Enemy> enemyList;

    /**
     * Constructor for the EntityManager
     * @param gamePanel the gamePanel
     */
    public EntityManager (GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        player = createPlayer();
        enemyList = new ArrayList<>();
        createEnemyList();
    }

    /**
     * Creates the player
     * @return the player
     */
    private Player createPlayer() {
        return new Player();
    }

    /**
     * Creates the enemy list
     */
    private void createEnemyList() {
        createEnemy(EnemyType.RACOON, 22, 22);
        createEnemy(EnemyType.RACOON, 5, 5);
    }

    /**
     * Creates an enemy
     * @param type the type of enemy
     * @param startX the starting x position
     * @param startY the starting y position
     */
    private void createEnemy(EnemyType type, int startX, int startY) {
        ImageLoader imageLoader = gamePanel.getImageLoader();
        enemyList.add(switch (type) {
            case RACOON -> new Raccoon(
                    imageLoader.getEnemyRacoonImages(),
                    startX * GamePanel.config.tileSize(),
                    startY * GamePanel.config.tileSize()
            );
            case CAFFEINE -> null;
        });
    }

    /**
     * Updates the player and enemies
     */
    public void update() {
        player.update();
        for (Enemy enemy : enemyList) {
            enemy.update();
            if (!player.isInvincible() && player.getWorldHitbox().intersects(enemy.getWorldHitbox())) {
                player.onCollide(enemy);
            }
        }
    }

    /**
     * Draws the player and enemies
     * @param g2 the graphics2D object
     */
    public void draw(Graphics2D g2) {
        ArrayList<Entity> entityList = new ArrayList<>(enemyList);
        entityList.add(player);
        entityList.sort(Comparator.comparingInt(Entity::getWorldY));
        for (Entity entity : entityList) {
            entity.draw(g2);
        }
    }

    /**
     * Returns the player
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the enemy list
     * @return the enemy list
     */
    public ArrayList<Enemy> getEnemyList() {
        return enemyList;
    }
}
