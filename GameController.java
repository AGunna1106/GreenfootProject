import greenfoot.*;

/**
 * GameController – the system Controller (iter2.md GRASP pattern).
 *
 * GRASP Roles:
 *   Controller      : central coordinator; handles all system events; never stores domain state
 *   Creator         : creates Game; delegates Player creation to Manager
 *   Indirection     : decouples GUI from domain objects (Player, Game, Manager)
 *
 * UC1  Login          – verifyPlayer() delegates to Manager (Information Expert)
 * UC2  Init Game      – initializeGame() creates Game; Game creates Round; Map creates Cells
 * UC3  Leaderboard    – getCurrentPlayer() exposes player history for display
 */
public class GameController
{
    private Player  player;
    private Manager manager;  // Pure Fabrication: Manager as Player database
    private Game    game;
    private GUI     gui;

    public GameController(GUI gui)
    {
        this.gui = gui;
        manager  = new Manager();   // Pure Fabrication: stable Player store
    }

    // ----------------------------------------------------------------
    // UC1 Non-Trivial Step 4: Login or create new Player
    // Information Expert: Manager retrieves existing Player or creates new one
    // ----------------------------------------------------------------
    public String verifyPlayer(String username, String password)
    {
        if (username == null || username.trim().isEmpty())
            return "Username cannot be empty.";
        if (password == null || password.trim().isEmpty())
            return "Password cannot be empty.";

        Player p = manager.getPlayer(username.trim(), password.trim());
        if (p == null) return "Incorrect password. Try again.";

        player = p;
        return "Login successful!";
    }

    // ----------------------------------------------------------------
    // UC2 Step 2: Initialize Game
    // Creator chain: Controller creates Game -> Game creates Round -> Map creates Cells
    // Resets player stats and builds the map inside the new Game world.
    // ----------------------------------------------------------------
    public void initializeGame(int mapType)
    {
        player.setMedals(200);
        player.setHealth(100);
        player.setRound(1);

        game = new Game(player, gui);   // Creator: Controller creates Game
        game.initMap(mapType);          // Game delegates Map->Cell creation
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------
    public Player getCurrentPlayer() { return player; }
    public Game   getGame()          { return game; }
}
