import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Game here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Game extends World
{
    private Round round;
    private Map map;
    private Player player;
    private Difficulty difficulty;
    private boolean isPaused;
    /**
     * Constructor for objects of class Game.
     * 
     */
    public Game()
    {    
        // Create a new world with 624x504 cells with a cell size of 1x1 pixels.
        super(624, 504, 1);
        
        map = new Map(this);
        map.addCells(1);
        map.displayMap();
    }
}