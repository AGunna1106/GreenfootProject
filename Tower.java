import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Abstract base class for all tower types.
 * Supports buying, placing on valid cells, and upgrading through a 2D upgrade array.
 *
 * upgrade[level][0] = cost to upgrade to this level
 * upgrade[level][1] = damage at this level
 * upgrade[level][2] = range at this level
 * upgrade[level][3] = fireRate at this level
 */
public abstract class Tower extends Actor
{
    protected int id;
    protected int damage;
    protected int[][] upgrade;   // [level][cost, damage, range, fireRate]
    protected int range;
    protected int cost;
    protected int fireRate;
    protected int cooldown;
    protected int upgradeLevel;  // current upgrade level (0 = base)
    protected boolean placed;    // true once placed on the map
    protected String name;

    public Tower(int range, int fireRate, int damage, int cost)
    {
        this.range     = range;
        this.fireRate  = fireRate;
        this.damage    = damage;
        this.cost      = cost;
        this.cooldown  = 0;
        this.upgradeLevel = 0;
        this.placed    = false;
        getImage().scale(42, 42);
    }

    public void act()
    {
        if (!placed || ((Game)getWorld()).isPaused()) return;

        Enemy target = findTarget();

        if (target != null)
        {
            attack(target);
        }

        if (cooldown > 0) cooldown--;
    }

    protected Enemy findTarget()
    {
        List<Enemy> enemies = getObjectsInRange(range, Enemy.class);
        if (!enemies.isEmpty()) return enemies.get(0);
        return null;
    }

    protected void attack(Enemy target)
    {
        if (cooldown == 0)
        {
            shoot(target);
            cooldown = fireRate;
        }
    }

    protected abstract void shoot(Enemy target);

    // Upgrade system
    public boolean canUpgrade()
    {
        return upgradeLevel < upgrade.length - 1;
    }

    public int getNextUpgradeCost()
    {
        if (!canUpgrade()) return -1;
        return upgrade[upgradeLevel + 1][0];
    }

    /**
     * Applies the next upgrade if the player has enough medals.
     * Returns true on success.
     */
    public boolean doUpgrade(Player player)
    {
        if (!canUpgrade()) return false;
        int upgradeCost = getNextUpgradeCost();
        if (player.getMedals() < upgradeCost) return false;

        player.spendMedals(upgradeCost);
        upgradeLevel++;
        applyUpgradeStats();
        return true;
    }

    protected void applyUpgradeStats()
    {
        // upgrade[level] = {cost, damage, range, fireRate}
        damage   = upgrade[upgradeLevel][1];
        range    = upgrade[upgradeLevel][2];
        fireRate = upgrade[upgradeLevel][3];
    }

    // Placement helpers
    public void place()   { placed = true; }
    public boolean isPlaced() { return placed; }

    // Getters
    public int getCost()         { return cost; }
    public int getDamage()       { return damage; }
    public int getRange()        { return range; }
    public int getFireRate()     { return fireRate; }
    public int getUpgradeLevel() { return upgradeLevel; }
    public String getName()      { return name; }

    public String getInfoString()
    {
        String info = name + " (Lvl " + (upgradeLevel + 1) + ")\n"
            + "Dmg: " + damage + "  Rng: " + range + "  Rate: " + fireRate + "\n";
        if (canUpgrade())
            info += "Upgrade: " + getNextUpgradeCost() + " medals";
        else
            info += "MAX LEVEL";
        return info;
    }

    /**
     * Directly sets the tower to a specific upgrade level without spending medals.
     * Used by Round to recreate snapshot towers on restart-round.
     */
    public void applyUpgradeLevelDirectly(int level)
    {
        if (level < 0 || level >= upgrade.length) return;
        upgradeLevel = level;
        applyUpgradeStats();
    }
}
