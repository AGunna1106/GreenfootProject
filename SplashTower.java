import greenfoot.*;
import java.util.List;

/**
 * SplashTower – damages all enemies in range on each shot.
 * Base cost: 125 medals  |  Most expensive tier.
 *
 * upgrade[level] = { upgradeCost, damage, range, fireRate }
 *   Level 0 (base)   : buy cost 125, dmg 1, range 120, fireRate 90
 *   Level 1 (Upgrade): cost  75,     dmg 2, range 140, fireRate 75
 *   Level 2 (Max)    : cost 150,     dmg 4, range 160, fireRate 60
 */
public class SplashTower extends Tower
{
    public SplashTower()
    {
        super(120, 90, 1, 125);
        name = "Splash Tower";

        upgrade = new int[][] {
            { 125, 1, 120, 90 },   // Level 0 – base
            {  75, 2, 140, 75 },   // Level 1
            { 150, 4, 160, 60 }    // Level 2 – max
        };
    }

    /**
     * Splash: damages every enemy within range, not just the primary target.
     */
    protected void shoot(Enemy target)
    {
        List<Enemy> inRange = getObjectsInRange(range, Enemy.class);
        for (Enemy e : inRange)
        {
            Projectile p = new Projectile(e, damage);
            getWorld().addObject(p, getX(), getY());
        }
    }
}
