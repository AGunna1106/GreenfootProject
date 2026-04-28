import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SplashTower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SplashTower extends Tower
{
    private int targetMultiplier;
    /**
     * Constructor for objects of class SplashTower.
     * 
     */
    public SplashTower()
    {
        super(120, 90, 1);
    }
    
    protected void shoot(Enemy target)
    {
        Projectile p = new Projectile(target, damage);
        getWorld().addObject(p, getX(), getY());
    }
}
