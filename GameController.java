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
    private Manager manager = new Manager();
    private Game game;
    
    public GameController()
    {
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    
    public boolean verifyPlayer(String un, String pass) 
    {
        player = manager.getPlayer(un, pass);
        if (player == null) {
            return false;
        }
        return true;
    }
    
    public Cell[][] initializeGame(int mapType)
    {
        player.setMedals(0);
        player.setHealth(100);
        game = new Game(player);
        return(game.setMap(mapType));
        //Greenfoot.setWorld(game);
    }
}
