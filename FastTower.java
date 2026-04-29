import greenfoot.*;

/**
 * FastTower – fires rapidly at low damage.
 * Base cost: 75 medals  |  Middle price tier.
 *
 * upgrade[level] = { upgradeCost, damage, range, fireRate }
 *   Level 0 (base)   : buy cost 75,  dmg 1, range 120, fireRate 30
 *   Level 1 (Upgrade): cost 50,      dmg 2, range 130, fireRate 25
 *   Level 2 (Max)    : cost 100,     dmg 3, range 140, fireRate 18
 */
public class FastTower extends Tower
{
    public FastTower()
    {
        super(120, 30, 1, 75);
        name = "Fast Tower";

        // upgrade[level][cost, damage, range, fireRate]
        upgrade = new int[][] {
            {  75, 1, 120, 30 },   // Level 0 – base stats (cost = buy cost)
            {  50, 2, 130, 25 },   // Level 1
            { 100, 3, 140, 18 }    // Level 2 – max
        };
    }

    protected void shoot(Enemy target)
    {
        Projectile p = new Projectile(target, damage);
        getWorld().addObject(p, getX(), getY());
    }
}
