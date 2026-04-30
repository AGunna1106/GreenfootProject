    import greenfoot.*;

/**
 * GUI – login and main-menu world (World 1 of 2).
 *
 * GRASP Roles (iter2.md):
 *   GUI (View/Boundary) : only displays state and forwards input — never contains logic
 *   Indirection         : sits between player input and game logic;
 *                         routes every action to GameController or Game
 *   Low Coupling        : GUI knows Game's public interface only; no domain logic here
 *
 * Two phases:
 *   Login / Menu  – GUI is the active Greenfoot world (act() runs)
 *   In-game       – Game is the active world; GUI is a rendering service
 *                   (Game calls gui.refreshPanel / gui.handlePanelClick)
 *
 * UC1  Login        – login() collects credentials, delegates to GameController
 * UC2  Start Game   – startGame() asks GameController to init, sets Game as world
 * UC3  Leaderboard  – menu stub
 * UC5  Help         – panel button routes to game.toggleHelp()
 * UC6  Place Tower  – panel buttons route to game.startPlacement()
 * UC7  Upgrade      – panel button routes to game.handleUpgrade()
 * UC8  Difficulty   – menu stub
 * UC9  Pause/Resume – panel buttons route to game.doPause() / game.doResume()
 */
public class GUI extends World
{
    // ----------------------------------------------------------------
    // Layout constants — hardcoded here to avoid circular static
    // initialisation with Game (both extend World; Greenfoot is sensitive
    // to cross-class static field references at compile time).
    // These values must match Game's constants exactly.
    // ----------------------------------------------------------------
    public static final int MAP_W   = 504;
    public static final int PANEL_W = 156;
    public static final int WORLD_W = 660;
    public static final int WORLD_H = 504;
    public static final int PANEL_X = 504;

    // ----------------------------------------------------------------
    // In-game panel layout (button Y-centres and dimensions)
    // All layout decisions live in GUI — Game has zero display code.
    // ----------------------------------------------------------------
    static final int BTN_FAST_Y         =  95;
    static final int BTN_LONG_Y         = 135;
    static final int BTN_SPLASH_Y       = 175;
    static final int BTN_UPGRADE_Y      = 290;
    static final int BTN_PAUSE_Y        = 480;
    static final int BTN_RESUME_Y       = 220;
    static final int BTN_HELP_Y         = 270;
    static final int BTN_RESTART_RND_Y  = 320;
    static final int BTN_RESTART_GAME_Y = 370;
    static final int BTN_W = PANEL_W - 8;   // 148
    static final int BTN_H = 28;

    // ----------------------------------------------------------------
    // Login / menu state
    // ----------------------------------------------------------------
    private GameController gameController;
    private boolean loginStarted    = false;
    private boolean loggedIn        = false;
    private boolean waitingToSwitch = false;
    private boolean choosingMap     = false;
    private int     delayCounter    = 0;
    private int     mapType         = 0;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public GUI()
    {
        super(WORLD_W, WORLD_H, 1);
        gameController = new GameController(this);

        // Set a visible background immediately so Greenfoot doesn't show
        // a blank white canvas while waiting for the first act() call.
        GreenfootImage bg = new GreenfootImage(WORLD_W, WORLD_H);
        bg.setColor(new Color(0, 0, 0));
        bg.fill();
        setBackground(bg);
        showText("Loading... press Run to start", WORLD_W / 2, WORLD_H / 2);
    }

    // ----------------------------------------------------------------
    // act() — login / menu phase only; no game logic here
    // ----------------------------------------------------------------
    public void act()
    {
        delayCounter++;

        if (!loginStarted)
        {
            loginStarted = true;
            login();
            return;
        }

        if (waitingToSwitch && delayCounter > 120)
        {
            waitingToSwitch = false;
            loggedIn        = true;
            drawMenu();
            return;
        }

        if (loggedIn && Greenfoot.mouseClicked(null))
        {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null) handleMenuClick(mouse.getX(), mouse.getY());
        }
    }

    // ================================================================
    // UC1 — Login
    // GUI collects input; GameController (Indirection) delegates to Manager
    // ================================================================
    public void login()
    {
        String un   = Greenfoot.ask("Enter Username:");
        String pass = Greenfoot.ask("Enter Password:");
        String result = gameController.verifyPlayer(un, pass);

        // Clear the "Loading..." hint before showing the result
        showText("", WORLD_W / 2, WORLD_H / 2);
        showText(result, getWidth() / 2, 250);

        delayCounter = 0;
        if (result.equals("Login successful!"))
            waitingToSwitch = true;
        else
            loginStarted = false;
    }

    // ================================================================
    // Menu display
    // ================================================================
    private void drawMenu()
    {
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(new Color(0, 0, 0));
        bg.fill();
        setBackground(bg);

        showText("TOWER DEFENSE",   getWidth() / 2, 100);
        showText("Start Game",      getWidth() / 2, 200);
        showText("Map",             getWidth() / 2, 250);
        showText("Difficulty",      getWidth() / 2, 300);
        showText("Help",            getWidth() / 2, 350);
        showText("Leaderboard",     getWidth() / 2, 400);
        showText("Click an option", getWidth() / 2, 470);
    }

    private void handleMenuClick(int x, int y)
    {
        if (isWithin(x, y, 200))
        {
            startGame(mapType);
        }
        else if (isWithin(x, y, 250))
        {
            choosingMap = true;
            showMessage("Select Map:  [Map 1]  [Map 2]");
        }
        else if (isWithin(x, y, 300))
        {
            showMessage("Difficulty: Easy / Medium / Hard (placeholder)");
        }
        else if (isWithin(x, y, 350))
        {
            showMessage("Place towers to stop enemies!");
        }
        else if (isWithin(x, y, 400))
        {
            showMessage("Leaderboard coming soon!");
        }
        else if (choosingMap && y > 455 && y < 485)
        {
            int half = getWidth() / 2;
            if      (x > half - 60 && x < half)      { mapType = 0; showMessage("Map 1 selected"); choosingMap = false; }
            else if (x > half      && x < half + 60) { mapType = 1; showMessage("Map 2 selected"); choosingMap = false; }
        }
    }

    private boolean isWithin(int x, int y, int optionY)
    {
        return x > 150 && x < 400 && y > optionY - 15 && y < optionY + 15;
    }

    private void showMessage(String msg)
    {
        showText("", getWidth() / 2, 470);
        showText(msg, getWidth() / 2, 470);
    }

    // ================================================================
    // UC2 Step 2 — Start Game
    // GUI delegates initialisation to GameController (Controller pattern),
    // then switches the active world to Game.
    // ================================================================
    public void startGame(int mapType)
    {
        for (int yy : new int[]{100, 200, 250, 300, 350, 400, 470})
            showText("", getWidth() / 2, yy);

        gameController.initializeGame(mapType);
        Game game = gameController.getGame();

        Greenfoot.setWorld(game);
        refreshPanel(game);
    }

    // ================================================================
    // UC4 — prepareForReturn()
    // Called by Game.restartGame() before Greenfoot.setWorld(gui).
    // Clears stale GUI text and resets login state so act() triggers
    // a fresh login dialog on the very next frame.
    // ================================================================
    public void prepareForReturn()
    {
        int cx = getWidth() / 2;
        showText("", cx, WORLD_H / 2);
        showText("", cx, 250);
        for (int yy : new int[]{100, 200, 250, 300, 350, 400, 470})
            showText("", cx, yy);

        loginStarted    = false;
        loggedIn        = false;
        waitingToSwitch = false;
        choosingMap     = false;
        delayCounter    = 0;

        GreenfootImage bg = new GreenfootImage(WORLD_W, WORLD_H);
        bg.setColor(new Color(0, 0, 0));
        bg.fill();
        setBackground(bg);
        showText("Loading... press Run to start", cx, WORLD_H / 2);
    }

    // ================================================================
    // In-game panel rendering — GUI owns ALL layout decisions.
    // Game passes itself; GUI reads state via public getters only.
    // ================================================================

    /**
     * Redraws the right-hand side panel onto Game's background image.
     * Called by Game whenever state changes (pause, placement, upgrade…).
     */
    public void refreshPanel(Game game)
    {
        GreenfootImage bg = game.getBackground();
        if (bg == null)
        {
            bg = new GreenfootImage(WORLD_W, WORLD_H);
            bg.setColor(new Color(20, 20, 30));
            bg.fill();
            game.setBackground(bg);
        }

        bg.setColor(new Color(30, 30, 45));
        bg.fillRect(PANEL_X, 0, PANEL_W, WORLD_H);
        bg.setColor(new Color(90, 90, 120));
        bg.drawLine(PANEL_X, 0, PANEL_X, WORLD_H);

        if (game.isPaused())
            drawPausedPanel(bg, game);
        else
            drawNormalPanel(bg, game);

        game.setBackground(bg);
    }

    private void drawNormalPanel(GreenfootImage bg, Game game)
    {
        Player player = game.getPlayer();
        int px = PANEL_X + 4;

        bg.setColor(new Color(255, 220, 80));
        bg.drawString("Medals: " + player.getMedals(), px, 18);
        bg.setColor(new Color(100, 220, 100));
        bg.drawString("HP:     " + player.getHealth(), px, 34);
        bg.setColor(new Color(180, 180, 255));
        bg.drawString("Round:  " + player.getRound(),  px, 50);

        bg.setColor(new Color(200, 200, 200));
        bg.drawString("-- BUY TOWER --", px, 68);

        int pending = game.getPendingTowerType();
        drawBtn(bg, "Fast Tower  75g",  BTN_FAST_Y,   pending == 1, new Color(60, 100, 160));
        drawBtn(bg, "Long Range  50g",  BTN_LONG_Y,   pending == 2, new Color(60, 100, 160));
        drawBtn(bg, "Splash     125g",  BTN_SPLASH_Y, pending == 3, new Color(60, 100, 160));

        bg.setColor(new Color(80, 80, 110));
        bg.drawLine(PANEL_X + 4, 198, PANEL_X + PANEL_W - 4, 198);

        bg.setColor(new Color(200, 200, 200));
        bg.drawString("-- SELECTED --", px, 214);

        Tower sel = game.getSelectedTower();
        if (sel != null)
        {
            bg.setColor(new Color(255, 255, 180));
            bg.drawString(sel.getName(), px, 227);
            bg.setColor(new Color(200, 200, 200));
            bg.drawString("Level:  " + (sel.getUpgradeLevel() + 1), px, 240);
            bg.drawString("Damage: " + sel.getDamage(),             px, 254);
            bg.drawString("Range:  " + sel.getRange(),              px, 270);

            if (sel.canUpgrade())
                drawBtn(bg, "Upgrade " + sel.getNextUpgradeCost() + "g",
                        BTN_UPGRADE_Y, false, new Color(140, 90, 20));
            else
            {
                bg.setColor(new Color(120, 200, 120));
                bg.drawString("** MAX LEVEL **", px, BTN_UPGRADE_Y + 6);
            }
        }
        else
        {
            bg.setColor(new Color(130, 130, 130));
            bg.drawString("(click a tower)", px, 255);
        }

        bg.setColor(new Color(80, 80, 110));
        bg.drawLine(PANEL_X + 4, 320, PANEL_X + PANEL_W - 4, 320);

        drawBtn(bg, "  PAUSE", BTN_PAUSE_Y, false, new Color(160, 80, 30));
    }

    private void drawPausedPanel(GreenfootImage bg, Game game)
    {
        int px = PANEL_X + 4;
        bg.setColor(new Color(255, 120, 60));
        bg.drawString("** PAUSED **", px + 8, 160);

        drawBtn(bg, "  RESUME",       BTN_RESUME_Y,       true,              new Color(40, 140, 60));
        drawBtn(bg, "  HELP",         BTN_HELP_Y,         game.isShowHelp(), new Color(60, 80, 160));
        drawBtn(bg, " RESTART ROUND", BTN_RESTART_RND_Y,  false,             new Color(140, 100, 20));
        drawBtn(bg, " RESTART GAME",  BTN_RESTART_GAME_Y, false,             new Color(160, 40, 40));
    }

    // ================================================================
    // UC9 / UC5 / UC4 / UC6 / UC7 — In-game panel input routing
    // GUI receives the click, identifies which action it maps to,
    // then calls the appropriate method on Game.
    // GRASP Indirection: Player clicks -> GUI -> Game executes logic
    // ================================================================
    public void handlePanelClick(int y, Game game)
    {
        if (game.isPaused())
        {
            if      (inBtn(y, BTN_RESUME_Y))       game.doResume();
            else if (inBtn(y, BTN_HELP_Y))         game.toggleHelp();
            else if (inBtn(y, BTN_RESTART_RND_Y))  game.restartRound();
            else if (inBtn(y, BTN_RESTART_GAME_Y)) game.restartGame();
            return;
        }

        if      (inBtn(y, BTN_FAST_Y))    game.startPlacement(1);
        else if (inBtn(y, BTN_LONG_Y))    game.startPlacement(2);
        else if (inBtn(y, BTN_SPLASH_Y))  game.startPlacement(3);
        else if (inBtn(y, BTN_UPGRADE_Y)) game.handleUpgrade();
        else if (inBtn(y, BTN_PAUSE_Y))   game.doPause();
    }

    // ================================================================
    // Drawing helpers — all visual style lives here
    // ================================================================
    public void drawBtn(GreenfootImage bg, String label,
                        int centreY, boolean active, Color baseColor)
    {
        int bx = PANEL_X + 4;
        int by = centreY - BTN_H / 2;

        Color fill = active
            ? new Color(Math.min(baseColor.getRed()   + 60, 255),
                        Math.min(baseColor.getGreen() + 60, 255),
                        Math.min(baseColor.getBlue()  + 60, 255))
            : baseColor;

        bg.setColor(fill);
        bg.fillRect(bx, by, BTN_W, BTN_H);
        bg.setColor(new Color(200, 200, 200));
        bg.drawRect(bx, by, BTN_W, BTN_H);
        bg.drawString(label, bx + 5, centreY + 6);
    }

    public boolean inBtn(int y, int centreY)
    {
        return y >= centreY - BTN_H / 2 && y <= centreY + BTN_H / 2;
    }
}
