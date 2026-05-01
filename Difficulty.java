import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Difficulty here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Difficulty
{
    private int multiplierScale;
    private String selection;
    /**
     * Constructor for objects of class Difficulty.
     * 
     */
    public Difficulty()
    {    
        selection = "EASY";
    }
    
    public String getSelection() {
        return selection;
    }
}
