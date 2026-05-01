import greenfoot.*;

/**
 * GUI – login and main-menu world
 */
public class GUI extends World
{
    // Layout constants 
    public static final int MAP_W   = 546;
    public static final int PANEL_W = 156;
    public static final int WORLD_W = 660;
    public static final int WORLD_H = 504;
    public static final int PANEL_X = 504;

    static final int BTN_FAST_Y         =  95;
    static final int BTN_LONG_Y         = 135;
    static final int BTN_SPLASH_Y       = 175;
    static final int BTN_UPGRADE_Y      = 290;
    static final int BTN_START_WAVE_Y   = 360;
    static final int BTN_PAUSE_Y        = 480;
    static final int BTN_RESUME_Y       = 220;
    static final int BTN_HELP_Y         = 270;
    static final int BTN_RESTART_RND_Y  = 320;
    static final int BTN_RESTART_GAME_Y = 370;
    static final int BTN_W = PANEL_W - 8;   // 148
    static final int BTN_H = 28;

    // Login / menu state
    private boolean loginStarted    = false;
    private boolean loggedIn        = false;
    private boolean waitingToSwitch = false;
    private boolean choosingMap     = false;
    private int     delayCounter    = 0;
    private int     mapType         = 0;
    private Difficulty selectedDifficulty = new Difficulty("MEDIUM"); //default
    private boolean choosingDifficulty = false;
    private GameController gameController;
    
    
    private enum Screen {
        MENU,
        LEADERBOARD,
        GAMEOVER
    }
    
    private Screen currentScreen;

    // Constructor
    public GUI()
    {
        super(WORLD_W, WORLD_H, 1);
        gameController = new GameController(this);

    }

    public void act()
    {
        delayCounter++;
        if (!loginStarted && delayCounter > 120)
        {
            loginStarted = true;
            login();
            return;
        }

        if (waitingToSwitch && delayCounter > 120 && currentScreen != Screen.GAMEOVER)
        {
            waitingToSwitch = false;
            loggedIn        = true;
            drawMenu();
            return;
        }

        if (Greenfoot.mouseClicked(null) && (loggedIn || currentScreen == Screen.GAMEOVER))
        {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null) {
                int x = mouse.getX();
                int y = mouse.getY();
        
                if (currentScreen == Screen.MENU)
                    handleMenuClick(x, y);
                else if (currentScreen == Screen.LEADERBOARD)
                    handleLeaderboardClick(x, y);
                else if (currentScreen == Screen.GAMEOVER)
                    handleGameOverClick(x, y);
            }
        }
    }

    // Login
    public void login()
    {
        String un   = Greenfoot.ask("Enter Username:");
        String pass = Greenfoot.ask("Enter Password:");
        String result = gameController.verifyPlayer(un, pass);
        showText(result, getWidth() / 2, getHeight() / 2);

        delayCounter = 0;
        if (result.equals("Login successful!"))
            waitingToSwitch = true;
        else
            loginStarted = false;
    }
    
    // Leaderboard
    public void clickLeaderboard()
    {
        currentScreen = Screen.LEADERBOARD;
        String[][] stats = gameController.requestLeaderboard();
    
        clearMenuText();
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(new Color(26, 26, 46));
        bg.fill();
        setBackground(bg);
    
        // === HEADER ===
        bg.setColor(new Color(42, 42, 74));
        bg.fillRect(0, 0, getWidth(), 40);
        bg.setColor(new Color(85, 85, 120));
        bg.drawLine(0, 40, getWidth(), 40);
        bg.setColor(new Color(255, 215, 0));
        bg.drawString("** LEADERBOARD **", getWidth() / 2 - 60, 26);
    
        // === TOP 5 SECTION LABEL ===
        bg.setColor(new Color(150, 150, 180));
        bg.drawString("TOP 5:       Username              Round              Difficulty            Health               Medals", 20, 65);
        bg.drawLine(20, 68, getWidth() - 20, 68);
    
        // === TOP 5 ROWS ===
        Color[] rankColors = {
            new Color(255, 215, 0),   // gold
            new Color(192, 192, 192), // silver
            new Color(205, 127, 50),  // bronze
            new Color(150, 150, 150), // 4th
            new Color(150, 150, 150)  // 5th
        };
    
        for (int i = 0; i < 5; i++) {
            int rowY = 80 + i * 46;
    
            // Row background (alternate shading)
            bg.setColor(i % 2 == 0 ? new Color(38, 38, 62) : new Color(32, 32, 52));
            bg.fillRect(10, rowY, getWidth() - 20, 38);
            bg.setColor(new Color(60, 60, 90));
            bg.drawRect(10, rowY, getWidth() - 20, 38);
    
            // Rank number
            bg.setColor(rankColors[i]);
            bg.drawString(String.valueOf(i + 1), 20, rowY + 24);
    
            // Player name and score
            if (stats[0] != null && i < stats[0].length && stats[0][i] != null) {
                bg.setColor(new Color(220, 220, 240));
                
                bg.setColor(new Color(180, 200, 255));
                String[] splitUser = stats[0][i].split(",", 2);
                String[] splitStats = splitUser[1].split(";");
                bg.drawString(splitUser[0], 55, rowY + 24);
                for (int x = 1; x < splitStats.length + 1; x++ ) {
                    bg.drawString(splitStats[x-1], (x * 80) + 100, rowY + 24);
                }
            } else {
                bg.setColor(new Color(100, 100, 120));
                bg.drawString("---", 40, rowY + 14);
            }
        }
    
        // === DIVIDER ===
        int divY = 320;
        bg.setColor(new Color(85, 85, 120));
        bg.drawLine(10, divY, getWidth() - 10, divY);
    
        // === YOUR STATS SECTION ===
        bg.setColor(new Color(150, 150, 180));
        bg.drawString("YOUR STATS", 20, divY + 20);
    
        bg.setColor(new Color(42, 42, 74));
        bg.fillRect(10, divY + 28, getWidth() - 20, 100);
        bg.setColor(new Color(60, 60, 90));
        bg.drawRect(10, divY + 28, getWidth() - 20, 100);
    
        if (stats[1] != null) {
            int statY = divY + 46;
            for (int i = 0; i < stats[1].length && i < 5; i++) {
                bg.setColor(new Color(180, 200, 255));
                String[] splitStats = stats[1][i].split(";");
                bg.drawString(i + "    games ago" , 35, statY + i * 20);
                for (int x = 1; x < splitStats.length + 1; x++ ) {
                    bg.drawString(splitStats[x-1], (x * 80) + 100, statY + i * 20);
                }
            }
        }
    
        // === BACK BUTTON ===
        bg.setColor(new Color(80, 40, 30));
        bg.fillRect(10, getHeight() - 40, getWidth() - 20, 30);
        bg.setColor(new Color(160, 80, 50));
        bg.drawRect(10, getHeight() - 40, getWidth() - 20, 30);
        bg.setColor(new Color(240, 140, 100));
        bg.drawString("<- BACK TO MENU", 30, getHeight() - 20);
    
        setBackground(bg);
    }
    
    private void handleLeaderboardClick(int x, int y)
    {
        // Back button hitbox
        if (y > getHeight() - 40 && y < getHeight() - 10 && x > 10 && x < getWidth() - 10)
        {
            drawMenu();
        }
    }

    // Menu drawing
    private void drawMenu()
    {
        currentScreen = Screen.MENU;
        
        showText("", 273, 250);                    // clear overlay
        showText("", getWidth() / 2, WORLD_H / 2); // clear overlay
        getBackground().clear();
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
            choosingDifficulty = true;
            showMessage("Select Difficulty: [Easy] [Medium] [Hard]");
        }
        else if (isWithin(x, y, 350))
        {
            showMessage("Place towers to stop enemies!");
        }
        else if (isWithin(x, y, 400))
        {
            currentScreen = Screen.LEADERBOARD;
            clickLeaderboard();
        }
        else if (choosingMap && y > 455 && y < 485)
        {
            int half = getWidth() / 2;
            if      (x > half - 60 && x < half + 50) { mapType = 0; showMessage("Map 1 selected"); choosingMap = false; }
            else if (x > half + 50 && x < half + 120) { mapType = 1; showMessage("Map 2 selected"); choosingMap = false; }
        }
        else if (choosingDifficulty && y > 455 && y < 485)
        {
            int half = getWidth() / 2;

            if (x > half - 40 && x < half + 30)
            {
                selectedDifficulty = new Difficulty("EASY");
                gameController.updateDifficulty(selectedDifficulty);
                showMessage("Difficulty: EASY");
                choosingDifficulty = false;
            }
            else if (x > half + 30 && x < half + 110)
            {
                selectedDifficulty = new Difficulty("MEDIUM");
                gameController.updateDifficulty(selectedDifficulty);
                showMessage("Difficulty: MEDIUM");
                choosingDifficulty = false;
            }
            else if (x > half + 110 && x < half + 180)
            {
                selectedDifficulty = new Difficulty("HARD");
                gameController.updateDifficulty(selectedDifficulty);
                showMessage("Difficulty: HARD");
                choosingDifficulty = false;
            
            }
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

    public void startGame(int mapType)
    {
        loggedIn = false;
        clearMenuText();
        gameController.initializeGame(mapType);
    }
    
    private void clearMenuText() {
        showText("", getWidth()/2, 100);
        showText("", getWidth()/2, 200);
        showText("", getWidth()/2, 250);
        showText("", getWidth()/2, 300);
        showText("", getWidth()/2, 350);
        showText("", getWidth()/2, 400);
        showText("", getWidth()/2, 470);
    }

    public void prepareForReturn()
    {
        int cx = getWidth() / 2;
        showText("", cx, WORLD_H / 2);
        showText("", cx, 250);
        for (int yy : new int[]{100, 200, 250, 300, 350, 400, 470})
            showText("", cx, yy);

        loginStarted    = true;
        loggedIn        = false;
        waitingToSwitch = true;
        choosingMap     = false;
        delayCounter    = 0;

        GreenfootImage bg = new GreenfootImage(WORLD_W, WORLD_H);
        bg.setColor(new Color(0, 0, 0));
        bg.fill();
        setBackground(bg);
        gameOver();
    }
    
    public void gameOver() {
        currentScreen = Screen.GAMEOVER;
        int result = gameController.endGame();
        Player p = gameController.getCurrentPlayer();
    
        clearMenuText();
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(new Color(20, 20, 30));
        bg.fill();
        int cx = getWidth() / 2;
        
        bg.setColor(new Color(255, 80, 80));
        bg.drawString("GAME OVER", cx - 40, 80);
        String msg = result == 2 ? "You Won!" : result == 1 ? "You Lost!" : "You Quit!";
        bg.setColor(new Color(200, 200, 200));
        bg.drawString(msg, cx - 30, 140);
    
        bg.setColor(new Color(180, 180, 210));
        bg.drawString("Round:      " + p.getRound(),      cx - 45, 185);
        bg.drawString("Medals:     " + p.getMedals(),     cx - 45, 210);
        bg.drawString("Health:     " + p.getHealth(),     cx - 45, 235);    
        bg.setColor(new Color(120, 120, 160));
        bg.drawString("Click to return to menu", cx - 80, 340);
    
        setBackground(bg);
    }
    
    private void handleGameOverClick(int x, int y)
    {
        drawMenu();
        loggedIn = true;
    }

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

        // Wave button / status
        if (!game.isRoundActive())
        {
            drawBtn(bg, " START WAVE", BTN_START_WAVE_Y, false, new Color(40, 140, 60));
        }
        else
        {
            bg.setColor(new Color(120, 220, 120));
            bg.drawString("Wave active!", px, BTN_START_WAVE_Y - 6);
            bg.setColor(new Color(200, 200, 200));
            bg.drawString("Enemies: " + game.getEnemiesRemaining(), px, BTN_START_WAVE_Y + 10);
        }

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
        else if (!game.isRoundActive() && inBtn(y, BTN_START_WAVE_Y)) game.startWave();
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