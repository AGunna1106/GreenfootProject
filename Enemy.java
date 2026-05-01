import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Enemy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Enemy extends Actor
{
    private int health;
    private int damage;
    private int speed;
    private int reward;
    private int[][] path;
    private int currentPoint = 0;
    private Player player;
    
    public Enemy(int health, int damage, int speed, int mapType, Player player)
    {    
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.player = player;
        this.reward = health;
        if(mapType == 0){
            path = new int[][] {
                {100, 350},
                {100, 65},
                {190, 65},
                {190, 480},
                {270, 480},
                {270, 65},
                {350, 65},
                {350, 440},
                {440, 440},
                {440, 270},
                {500, 270}
            };
        }else{
            path = new int[][] {
                {60, 350},
                {60, 20},
                {480, 20},
                {480, 440},
                {150, 440},
                {150, 110},
                {400, 110},
                {400, 350},
                {230, 350},
                {230, 190},
                {310, 190},
                {310, 270}
            };
        }
    }
    
    public void act()
    {
        if (((Game)getWorld()).isPaused()) return; 
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
    
    private void die()
    {
        player.addMedals(reward);
        getWorld().removeObject(this);
    }
    
    private void followPath()
    {
        if (currentPoint >= path.length) 
        {
            dealDamage();
            return;   // must stop here — path array is exhausted
        }

        int targetX = path[currentPoint][0];
        int targetY = path[currentPoint][1];

        turnTowards(targetX, targetY);
        move(speed);

        if (Math.abs(getX() - targetX) < speed + 3 && Math.abs(getY() - targetY) < speed + 3)
        {
            currentPoint++;
        }
    }
    
    private void dealDamage(){
        player.reduceHealth(damage);
        getWorld().removeObject(this);
    }
}