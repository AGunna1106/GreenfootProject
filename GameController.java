import greenfoot.*;

/**
 * GameController – bridges the GUI login screen and the Game world.
 * Verifies / creates players via Manager and launches a Game session.
 */
public class GameController
{
    private Player  player;
    private Manager manager;
    private Game    game;
    private GUI     gui;
    private Difficulty difficulty = new Difficulty("EASY");

    public GameController(GUI gui)
    {
        this.gui = gui;
        manager  = new Manager();
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
        if (username.contains(",") || username.contains(";") || username.contains("|") || password.contains(",") || password.contains(";") || password.contains("|"))
            return "Cannot use symbols { , ; | }.";

        Player p = manager.getPlayer(username.trim(), password.trim());
        if (p == null) return "Incorrect password. Try again.";

        player = p;
        manager.savePlayer(p);
        return "Login successful!";
    }

    /**
     * Switches Greenfoot to a new Game world for the given map type.
     */
    public void initializeGame(int mapType)
    {
        player.setMedals(200);
        player.setHealth(100);
        player.setRound(1);

        game = new Game(player, gui, difficulty);
        game.setMap(mapType);
        Greenfoot.setWorld(game);
    }
    
    public String[][] requestLeaderboard() {
        Player[] temp = manager.getAllPlayers();
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setTopRanking(temp);
        return new String[][] {leaderboard.getTopRanking(), player.getPastStats().toArray(new String[0])};
    }
    
    public int endGame() {
        String difficult = difficulty.getSelection();
    
        player.addDifficulty(difficult);
        int end = player.endState();
        manager.savePlayer(player);
        return end;
    }
    
    public void updateDifficulty(String selection){
        difficulty = new Difficulty(selection);
    }

    public Player getCurrentPlayer() { return player; }
    public Game   getGame()          { return game; }
}