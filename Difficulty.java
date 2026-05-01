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
    public Difficulty(String selection)
    {    
        this.selection = selection;
        if(selection == "EASY") {
            this.multiplierScale = 1;
        }else if(selection == "MEDIUM"){
            this.multiplierScale = 2;
        }else if(selection == "HARD"){
            this.multiplierScale = 3;
        }
    }
    
    public String getSelection() {
        return selection;
    }
    
    public int getMultiplier(){
        return multiplierScale;
    }
}
