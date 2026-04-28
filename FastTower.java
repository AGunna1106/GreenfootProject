import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class FastTower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FastTower extends Tower
{
    private int speedMultiplier;
    /**
     * Constructor for objects of class FastTower.
     * 
     */
    public FastTower()
    {
        super(120, 60, 1);
    }
    
    protected void shoot(Enemy target)
    {
        Projectile p = new Projectile(target, damage);
        getWorld().addObject(p, getX(), getY());
    }
}
