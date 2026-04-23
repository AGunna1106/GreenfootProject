import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Tower here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tower extends Actor
{
    private int id;
    private int damage;
    private int[][] upgrade;
    private int range;
    private int cost;
    private int speed;
    private int target;
    /**
     * Constructor for objects of class Tower.
     * 
     */
    public Tower()
    {    
        getImage().scale(42, 42);
    }
}
