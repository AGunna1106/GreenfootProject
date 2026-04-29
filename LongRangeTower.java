import greenfoot.*;

/**
 * LongRangeTower – fires slow but hits far and hard.
 * Base cost: 50 medals  |  Cheapest tier.
 *
 * upgrade[level] = { upgradeCost, damage, range, fireRate }
 *   Level 0 (base)   : buy cost 50,  dmg 2, range 240, fireRate 120
 *   Level 1 (Upgrade): cost 60,      dmg 4, range 280, fireRate 100
 *   Level 2 (Max)    : cost 120,     dmg 6, range 320, fireRate  80
 */
public class LongRangeTower extends Tower
{
    public LongRangeTower()
    {
        super(240, 120, 2, 50);
        name = "Long Range Tower";

        upgrade = new int[][] {
            {  50, 2, 240, 120 },  // Level 0 – base
            {  60, 4, 280, 100 },  // Level 1
            { 120, 6, 320,  80 }   // Level 2 – max
        };
    }

    protected void shoot(Enemy target)
    {
        Projectile p = new Projectile(target, damage);
        p.setSpeed(10);
        getWorld().addObject(p, getX(), getY());
    }
}
