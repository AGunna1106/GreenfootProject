import greenfoot.*;

/**
 * Write a description of class GameController here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GameController  
{
    private Player player;
    private Manager manager;
    public GameController()
    {
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    
    public String verifyPlayer(String un, String pass) 
    {
        manager = new Manager();
        player = manager.getPlayer(un, pass);
        if (player == null) {
            return "Wrong password. Try again.";
        }
        return "Login successful!";
    }
    
    public boolean initializeGame(int mapType)
    {
        //initializeStats();
        Greenfoot.setWorld(new Game(mapType));
        return true;
    }
}
