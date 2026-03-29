import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Cell here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Cell extends Actor
{
    private boolean isValid;
    private boolean isPath;
    /**
     * Constructor for objects of class Cell.
     * 
     */
    public Cell()
    {    
        getImage().scale(42, 42);
    }
    
    public Cell(Color color) {
        getImage().scale(42, 42);
        getImage().setColor(color);
        getImage().fill();
        setImage(getImage());
    }
    
    public void setIsValid(boolean isvalid) {
        isValid = isvalid;
    }
    
    public boolean getIsValid() {
        return isValid;
    }
    
    public void setIsPath(boolean ispath) {
        isPath = ispath;
    }
    
    public boolean getIsPath() {
        return isPath;
    }
}
