import greenfoot.*;

/**
 * GameController – bridges the GUI login screen and the Game world.
 * Verifies / creates players via Manager and launches a Game session.
 */
public class GameController
{
    private Manager manager;
    private Player  currentPlayer;

    public GameController()
    {
        manager = new Manager();
    }

    /**
     * Attempts login or auto-creates a new account.
     * Returns a status message for the GUI to display.
     */
    public String verifyPlayer(String username, String password)
    {
        if (username == null || username.trim().isEmpty())
            return "Username cannot be empty.";
        if (password == null || password.trim().isEmpty())
            return "Password cannot be empty.";

        Player p = manager.getPlayer(username.trim(), password.trim());
        if (p == null)
        {
            return "Incorrect password. Try again.";
        }
        currentPlayer = p;
        return "Login successful!";
    }

    /**
     * Switches Greenfoot to a new Game world for the given map type.
     */
    public void initializeGame(int mapType)
    {
        if (currentPlayer == null)
        {
            currentPlayer = new Player("Guest", "guest");
        }
        Greenfoot.setWorld(new Game(mapType, currentPlayer));
    }

    public Player getCurrentPlayer() { return currentPlayer; }
}
