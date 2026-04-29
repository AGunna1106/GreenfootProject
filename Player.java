import greenfoot.*;

/**
 * Player – holds state for health, medals (gold), and round progress.
 */
public class Player
{
    private int medals;
    private int health;
    private int round;
    private String username;
    private String password;

    public Player(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.medals   = 200;   // starting gold
        this.health   = 100;
        this.round    = 1;
    }

    // ----------------------------------------------------------------
    // Medals / Gold
    // ----------------------------------------------------------------

    public int getMedals()       { return medals; }
    public void addMedals(int amount)  { medals += amount; }

    /**
     * Deducts the given amount.  Returns true if successful.
     */
    public boolean spendMedals(int amount)
    {
        if (medals >= amount) { medals -= amount; return true; }
        return false;
    }

    public boolean canAfford(int cost) { return medals >= cost; }

    // ----------------------------------------------------------------
    // Health
    // ----------------------------------------------------------------

    public int getHealth()          { return health; }
    public void reduceHealth(int amt) { health = Math.max(0, health - amt); }
    public boolean isAlive()        { return health > 0; }

    // ----------------------------------------------------------------
    // Round
    // ----------------------------------------------------------------

    public int getRound()      { return round; }
    public void nextRound()    { round++; }

    // ----------------------------------------------------------------
    // Account
    // ----------------------------------------------------------------

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void act() { }
}
