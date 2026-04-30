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
    // Round snapshot state
    private int   snapshotMedals;
    private int   snapshotHealth;
    private int   snapshotRound;

    /** Lightweight record of a placed tower so we can recreate it. */
    public static class TowerRecord
    {
        public final int type;   // 1=Fast 2=Long 3=Splash
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

    // Enemy / round progression fields (extend as needed)
    private Enemy[] enemies;
    private int     enemyCount;
    private int     roundCount;

    public Round() { }

    // Snapshot / Restore
    /**
     * Call this at the beginning of every round (before any towers are
     * placed or enemies spawn) to record the state we can restore to.
     *
     * @param player        the current player
     * @param placedTowers  list of towers already on the map (may be empty for round 1)
     * @param cellSize      pixel size of each grid cell (needed to recover col/row)
     */
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

    /**
     * Restores player stats to the snapshot taken at round start.
     * Does NOT touch the world or towers — Game.restartRound() does that
     * after calling this method and using getSnapshotTowers() to rebuild.
     */
    public void restorePlayerToSnapshot(Player player)
    {
        player.setMedals(snapshotMedals);
        player.setHealth(snapshotHealth);
        player.setRound(snapshotRound);
    }

    /** Returns the tower records from the snapshot (may be empty). */
    public List<TowerRecord> getSnapshotTowers() { return snapshotTowers; }

    // Round progression helpers (called by Game)
    public int  getRoundCount()  { return roundCount; }
    public void incrementRound() { roundCount++; }

    public Enemy[] getEnemies()  { return enemies; }

    /** Maps a concrete Tower subclass back to its type id. */
    private static int towerTypeOf(Tower t)
    {
        if (t instanceof FastTower)       return 1;
        if (t instanceof LongRangeTower)  return 2;
        return 3; // SplashTower
    }
}
