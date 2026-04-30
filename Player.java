import greenfoot.*;
import java.util.*;

/**
 * Player – holds state for health, medals (gold), and round progress.
 *
 * Medals are now adjusted through a single adjustMedals(int) method:
 *   positive amount  → add medals (reward)
 *   negative amount  → spend medals (purchase); returns false if insufficient
 */
public class Player
{
    private int medals;
    private int health;
    private int round;
    private int highStatsIndex;
    private String username;
    private String password;
    private ArrayList<String> pastStats;

    public Player(String un, String pass)
    {
        this.username  = un;
        this.password  = pass;
        this.pastStats = new ArrayList<>();
    }

    // Medals
    public int getMedals() { return medals; }

    /**
     * Positive amount adds medals; negative amount spends medals.
     * Returns false (and makes no change) when spending would go below 0.
     */
    public boolean adjustMedals(int amount)
    {
        if (amount < 0 && medals < -amount) return false;
        medals += amount;
        return true;
    }

    // Convenience wrappers kept for backward compatibility
    public void addMedals(int amount)      { adjustMedals(amount); }
    public boolean spendMedals(int amount) { return adjustMedals(-amount); }
    public boolean canAfford(int cost)     { return medals >= cost; }

    public void setMedals(int medals)  { this.medals = medals; }
    public void setHealth(int health)  { this.health = health; }

    // Health
    public int  getHealth()           { return health; }
    public void reduceHealth(int amt) { health = Math.max(0, health - amt); }
    public boolean isAlive()          { return health > 0; }

    // Round
    public int  getRound()      { return round; }
    public void nextRound()     { round++; }
    public void setRound(int r) { this.round = r; }

    // Account
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void act() { }

    // Past stats / leaderboard
    public String getHighStats()
    {
        return pastStats.isEmpty() ? null : pastStats.get(highStatsIndex);
    }

    private void setHighStats()
    {
        if (pastStats.isEmpty()) {
            highStatsIndex = -1;
            return;
        }
        int highScoreIndex = 0;
        for (int i = 1; i < pastStats.size(); i++) {
            int[] current = parseStats(pastStats.get(i));
            int[] best = parseStats(pastStats.get(highScoreIndex));
            if (isBetter(current, best)) {
                highScoreIndex = i;
            }
        }
        highStatsIndex = highScoreIndex;
    }

    public void addPastStats(String stats) { pastStats.add(stats); setHighStats(); }
    public ArrayList<String> getPastStats() { return pastStats; }

    private int[] parseStats(String stat)
    {
        // format: rounds;difficulty;health;medals
        String[] parts = stat.split("\\;");
        int rounds = Integer.parseInt(parts[0]);
        int difficulty = difficultyValue(parts[1]); // convert to number
        int health = Integer.parseInt(parts[2]);
        int medals = Integer.parseInt(parts[3]);
        return new int[]{rounds, difficulty, health, medals};
    }

    private int difficultyValue(String d)
    {
        if (d.equals("Hard"))   return 3;
        if (d.equals("Medium")) return 2;
        return 1; //Easy
    }

    private boolean isBetter(int[] a, int[] b)
    {
        if (a[0] != b[0]) return a[0] > b[0]; // rounds
        if (a[1] != b[1]) return a[1] > b[1]; // difficulty
        if (a[2] != b[2]) return a[2] > b[2]; // health
        return a[3] > b[3]; // medals
    }
}
