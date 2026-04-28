import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class Tower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Tower extends Actor
{
    protected int id;
    protected int damage;
    protected int[][] upgrade;
    protected int range;
    protected int cost;
    protected int fireRate;
    protected int cooldown;
    /**
     * Constructor for objects of class Tower.
     * 
     */
    public Tower(int range, int fireRate, int damage)
    {    
        getImage().scale(42, 42);
        this.range = range;
        this.fireRate = fireRate;
        this.damage = damage;
        this.cooldown = 0;
    }
    
    public void act()
    {
        Enemy target = findTarget();
        
        if (target != null)
        {
            turnTowards(target.getX(), target.getY());
            attack(target);
        }
        
        if (cooldown > 0){
            cooldown--;
        }
    }
    
    protected Enemy findTarget()
    {
        List<Enemy> enemies = getObjectsInRange(range, Enemy.class);
        
        if (!enemies.isEmpty())
        {
            return enemies.get(0);
        }
        
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
}
