import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Projectile here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Projectile extends Actor
{
    private Enemy target;
    private int speed = 5;
    private int damage;
    
    public Projectile(Enemy target, int damage)
    {
        this.target = target;
        this.damage = damage;
    }
    
    public void act()
    {
        if (getWorld() == null || target == null || target.getWorld() == null) {
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
            return;
        }
    
        turnTowards(target.getX(), target.getY());
        move(speed);
    
        if (intersects(target))
        {
            target.takeDamage(damage);
    
            if (getWorld() != null) {
                getWorld().removeObject(this);
            }
            return;
        }
    }
    
    public void setSpeed(int newSpeed)
    {
        this.speed = speed;
    }
}
