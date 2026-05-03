import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Round – manages per-round state and snapshot/restore logic.
 *
 * At the START of each round, call snapshotRoundStart() to record
 * the player's medals, health, round number, and which towers are placed.
 * When the player hits "Restart Round", call restoreRoundStart() to
 * wind everything back to that snapshot — without wiping the whole game.
 */
public class Round
{
    private int   snapshotMedals;
    private int   snapshotHealth;
    private int   snapshotRound;

    public static class TowerRecord
    {
        public final int type;
        public final int col;
        public final int row;
        public final int upgradeLevel;

        public TowerRecord(int type, int col, int row, int upgradeLevel)
        {
            this.type         = type;
            this.col          = col;
            this.row          = row;
            this.upgradeLevel = upgradeLevel;
        }
    }

    private List<TowerRecord> snapshotTowers = new ArrayList<>();
    private Enemy[] enemies;
    private int     enemyCount;
    private int     roundCount;

    public Round() { }

    public void snapshotRoundStart(Player player, List<Tower> placedTowers, int cellSize)
    {
        snapshotMedals = player.getMedals();
        snapshotHealth = player.getHealth();
        snapshotRound  = player.getRound();

        snapshotTowers.clear();
        for (Tower t : placedTowers)
        {
            int col = (t.getX() - cellSize / 2) / cellSize;
            int row = (t.getY() - cellSize / 2) / cellSize;
            snapshotTowers.add(new TowerRecord(towerTypeOf(t), col, row, t.getUpgradeLevel()));
        }
    }

    public void restorePlayerToSnapshot(Player player)
    {
        player.setMedals(snapshotMedals);
        player.setHealth(snapshotHealth);
        player.setRound(snapshotRound);
    }

    public List<TowerRecord> getSnapshotTowers() { return snapshotTowers; }

    public int  getRoundCount()  { return roundCount; }
    public void incrementRound() { roundCount++; }

    public Enemy[] getEnemies()  { return enemies; }

    private static int towerTypeOf(Tower t)
    {
        if (t instanceof FastTower)       return 1;
        if (t instanceof LongRangeTower)  return 2;
        return 3; // SplashTower
    }
}
