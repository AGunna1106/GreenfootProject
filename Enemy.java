import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Enemy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Enemy extends Actor
{
    protected int health;
    protected int damage;
    protected int speed;
    protected int reward;
    
    public Enemy()
    {    
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.reward = reward;
    }
    
    public void act()
    {
        move(speed);
        followPath();
    }
    
    public void takeDamage(int amount)
    {
        health -= amount;
        
        if (health <= 0)
        {
            die();
        }
    }
    
    protected void die()
    {
        //implement give reward to player later
        getWorld().removeObject(this);
    }
    
    protected void followPath()
    {
        return; //finish later
    }
}
