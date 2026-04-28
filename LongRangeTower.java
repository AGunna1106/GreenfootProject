import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class LongRangeTower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LongRangeTower extends Tower
{
    private int rangeMultiplier;
    /**
     * Constructor for objects of class LongRangeTower.
     * 
     */
    public LongRangeTower()
    {
        super(240, 120, 2);
    }
    
    protected void shoot(Enemy target)
    {
        Projectile p = new Projectile(target, damage);
        p.setSpeed(10);
        getWorld().addObject(p, getX(), getY());
    }
}
