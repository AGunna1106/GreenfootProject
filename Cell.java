import greenfoot.*;

/**
 * Cell – a single tile on the map grid.
 * Tracks whether it is a valid tower placement zone,
 * whether it is an enemy path, and whether a tower occupies it.
 */
public class Cell extends Actor
{
    private boolean isValid;   // valid land cell (tower may be placed)
    private boolean isPath;    // enemy path cell
    private boolean occupied;  // a tower is currently on this cell
    private Color originalColor; // Base color

    public Cell(Color color)
    {
        this.originalColor = color;
        GreenfootImage img = new GreenfootImage(42, 42);
        img.setColor(color);
        img.fill();
        setImage(img);
        isValid  = false;
        isPath   = false;
        occupied = false;
    }

    // ----------------------------------------------------------------
    // Validity / path
    // ----------------------------------------------------------------

    public boolean isValid()       { return isValid && !occupied; }
    public void setIsValid(boolean v) { isValid = v; }
    public boolean getIsPath()        { return isPath; }
    public void setIsPath(boolean p)  { isPath = p; }

    // ----------------------------------------------------------------
    // Occupancy
    // ----------------------------------------------------------------

    public boolean isOccupied()          { return occupied; }
    public void setOccupied(boolean occ) { occupied = occ; }

    /**
     * Highlight this cell green (valid) or red (invalid) during placement.
     */
    public void highlight(boolean valid)
    {
        GreenfootImage img = getImage();
        img.setColor(valid ? new Color(0, 255, 0, 100) : new Color(255, 0, 0, 100));
        img.fill();
        setImage(img);
    }

    public void clearHighlight()
    {
        GreenfootImage img = getImage();
        img.setColor(originalColor);
        img.fill();
        setImage(img);
    }
}
