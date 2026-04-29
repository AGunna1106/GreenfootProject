import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

/**
 * World is 660 wide: 504px map (12x42 grid) + 156px side panel.
 */
public class Game extends World
{
    // ----------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------
    private static final int MAP_W    = 504;   // 12 cells * 42px
    private static final int PANEL_W  = 156;   // wide enough for readable buttons
    private static final int WORLD_W  = MAP_W + PANEL_W;  // 660
    private static final int WORLD_H  = 504;
    private static final int PANEL_X  = MAP_W; // 504 – left edge of side panel
    private static final int CELL_SZ  = 42;

    // Button Y positions
    private static final int BTN_FAST_Y      =  95;
    private static final int BTN_LONG_Y      = 135;
    private static final int BTN_SPLASH_Y    = 175;
    private static final int BTN_UPGRADE_Y   = 290;
    private static final int BTN_PAUSE_Y     = 480;
    private static final int BTN_RESUME_Y    = 220;
    private static final int BTN_HELP_Y      = 270;
    private static final int BTN_RESTART_RND_Y   = 320;
    private static final int BTN_RESTART_GAME_Y  = 370;

    // Button dimensions
    private static final int BTN_W  = PANEL_W - 8;  // 148
    private static final int BTN_H  = 28;

    // ----------------------------------------------------------------
    // State
    // ----------------------------------------------------------------
    private Map      map;
    private Player   player;
    private Difficulty difficulty;
    private Round    round;
    private int        mapType;   // stored for restart

    private boolean isPaused   = false;
    private boolean showHelp   = false;  // help overlay visible (only while paused)

    // Placement state
    private int   pendingTowerType = 0;    // 0=none 1=Fast 2=Long 3=Splash
    private Tower pendingTower     = null;

    // Selected placed tower
    private Tower selectedTower = null;

    // All placed towers
    private List<Tower> towers = new ArrayList<>();

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Game(Player player)
    {
        super(WORLD_W, WORLD_H, 1);
        this.player = player;

        map = new Map(this);
    }
    
    public Cell[][] setMap(int mapType) {
        drawSidePanel();
        return map.addCells(mapType);
    }

    // ----------------------------------------------------------------
    // Act
    // ----------------------------------------------------------------
    public void act()
    {
        // ESC always cancels placement regardless of pause state
        if (Greenfoot.isKeyDown("escape") && pendingTowerType != 0)
        {
            cancelPlacement();
            showText("Placement cancelled.", MAP_W / 2, WORLD_H - 15);
            drawSidePanel();
            return;
        }
        
        // Hover Highlighting during placement
        if (!isPaused && pendingTowerType != 0)
        {
            updatePlacementHighlight();
        }
        
        // Mouse clicks are processed whether paused or not
        // (so Resume / Help buttons work while paused)
        if (Greenfoot.mouseClicked(null))
        {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null) handleClick(mouse.getX(), mouse.getY());
        }

        // Game-logic updates only when NOT paused
        if (isPaused) return;
        // future: enemy movement, round progression, etc.
    }

    // ----------------------------------------------------------------
    // Click dispatcher
    // ----------------------------------------------------------------
    private void handleClick(int x, int y)
    {
        if (x >= PANEL_X)
        {
            handlePanelClick(y);
            return;
        }

        // Map clicks only active when NOT paused
        if (isPaused) return;

        if (pendingTowerType != 0)
            attemptPlacement(x, y);
        else
            selectTowerAt(x, y);
    }
    
    // Highlight Logic
    private void updatePlacementHighlight()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        
        // 1. Clear all current highlights first
        List<Cell> allCells = getObjects(Cell.class);
        for (Cell c : allCells) {
            c.clearHighlight();
        }
    
        // 2. If mouse is over the map, highlight the specific cell
        if (mouse != null && mouse.getX() < MAP_W) 
        {
            int col = mouse.getX() / CELL_SZ;
            int row = mouse.getY() / CELL_SZ;
            Cell currentCell = getCellAt(col, row);
            
            if (currentCell != null) 
            {
                // Highlight green if valid AND not occupied, otherwise red
                currentCell.highlight(currentCell.isValid());
            }
        }
    }
    
    // ----------------------------------------------------------------
    // Side-panel interaction
    // ----------------------------------------------------------------
    private void handlePanelClick(int y)
    {
        if (isPaused)
        {
            if      (inBtn(y, BTN_RESUME_Y))       doResume();
            else if (inBtn(y, BTN_HELP_Y))         toggleHelp();
            else if (inBtn(y, BTN_RESTART_RND_Y))  restartRound();
            else if (inBtn(y, BTN_RESTART_GAME_Y)) restartGame();
            return;
        }

        if      (inBtn(y, BTN_FAST_Y))    startPlacement(1);
        else if (inBtn(y, BTN_LONG_Y))    startPlacement(2);
        else if (inBtn(y, BTN_SPLASH_Y))  startPlacement(3);
        else if (inBtn(y, BTN_UPGRADE_Y)) handleUpgrade();
        else if (inBtn(y, BTN_PAUSE_Y))   doPause();
    }

    // ----------------------------------------------------------------
    // Pause / Resume / Restart
    // ----------------------------------------------------------------
    private void doPause()
    {
        isPaused = true;
        showHelp  = false;
        drawSidePanel();
    }

    private void doResume()
    {
        isPaused = false;
        showHelp  = false;
        clearHelpOverlay();
        drawSidePanel();
    }

    private void toggleHelp()
    {
        showHelp = !showHelp;
        if (showHelp) drawHelpOverlay(); else clearHelpOverlay();
        drawSidePanel();
    }

    /**
     * UC4 – Restart Round: reset current-round enemies & towers,
     * keep player medals/health and overall round number.
     */
    private void restartRound()
    {
        // Remove all placed towers from the world
        for (Tower t : towers) removeObject(t);
        towers.clear();
        selectedTower    = null;
        pendingTowerType = 0;
        if (pendingTower != null) { removeObject(pendingTower); pendingTower = null; }

        // Re-mark all cells as unoccupied
        List<Cell> cells = getObjects(Cell.class);
        for (Cell c : cells) c.setOccupied(false);

        // Restore starting medals for the round (200 base)
        player.addMedals(200 - player.getMedals()); // reset to 200

        isPaused = false;
        showHelp  = false;
        clearHelpOverlay();
        showText("Round restarted!", MAP_W / 2, WORLD_H - 15);
        drawSidePanel();
    }

    /**
     * UC4 – Restart Game: full reset – new Game world, starting stats.
     */
    private void restartGame()
    {
        Player fresh = new Player(player.getUsername(), player.getPassword());
        Greenfoot.setWorld(new Game(fresh));
    }

    // ----------------------------------------------------------------
    // Placement logic
    // ----------------------------------------------------------------
    private void startPlacement(int type)
    {
        Tower t = createTower(type);
        if (!player.canAfford(t.getCost()))
        {
            showText("Need " + t.getCost() + " medals!", MAP_W / 2, WORLD_H - 15);
            return;
        }

        cancelPlacement();
        pendingTowerType = type;
        pendingTower     = t;
        addObject(pendingTower, PANEL_X - 21, 21);

        showText("Click a green cell to place  |  ESC = cancel", MAP_W / 2, WORLD_H - 15);
        drawSidePanel();
    }

    private void attemptPlacement(int x, int y)
    {
        int col = x / CELL_SZ;
        int row = y / CELL_SZ;
        Cell cell = getCellAt(col, row);

        if (cell != null && cell.isValid())
        {
            if (pendingTower != null) removeObject(pendingTower);

            Tower t = createTower(pendingTowerType);
            player.spendMedals(t.getCost());
            t.place();
            addObject(t, col * CELL_SZ + CELL_SZ / 2, row * CELL_SZ + CELL_SZ / 2);
            towers.add(t);
            cell.setOccupied(true);

            pendingTowerType = 0;
            pendingTower     = null;

            showText("Tower placed!", MAP_W / 2, WORLD_H - 15);
            drawSidePanel();
        }
        else
        {
            showText("Invalid tile – pick a green cell.", MAP_W / 2, WORLD_H - 15);
        }
    }

    private void cancelPlacement()
    {
        if (pendingTower != null) { removeObject(pendingTower); pendingTower = null; }
        pendingTowerType = 0;
    }

    // ----------------------------------------------------------------
    // Tower selection
    // ----------------------------------------------------------------
    private void selectTowerAt(int x, int y)
    {
        for (Tower t : towers)
        {
            if (Math.abs(t.getX() - x) <= CELL_SZ / 2
             && Math.abs(t.getY() - y) <= CELL_SZ / 2)
            {
                selectedTower = t;
                drawSidePanel();
                return;
            }
        }
        selectedTower = null;
        drawSidePanel();
    }

    // ----------------------------------------------------------------
    // Upgrade
    // ----------------------------------------------------------------
    private void handleUpgrade()
    {
        if (selectedTower == null) return;
        if (!selectedTower.canUpgrade())
        {
            showText("Already max level!", MAP_W / 2, WORLD_H - 15);
            return;
        }
        int cost = selectedTower.getNextUpgradeCost();
        if (!player.canAfford(cost))
        {
            showText("Need " + cost + " medals to upgrade!", MAP_W / 2, WORLD_H - 15);
            return;
        }
        selectedTower.doUpgrade(player);
        showText("Upgraded to level " + (selectedTower.getUpgradeLevel() + 1) + "!",
                 MAP_W / 2, WORLD_H - 15);
        drawSidePanel();
    }

    // ----------------------------------------------------------------
    // Help overlay (map area, only while paused)
    // ----------------------------------------------------------------
    private void drawHelpOverlay()
    {
        int cx = MAP_W / 2;
        showText("---- HOW TO PLAY ----",           cx, 130);
        showText("Buy towers from the right panel.", cx, 165);
        showText("Click a GREEN tile to place.",     cx, 190);
        showText("Click a tower to select it.",      cx, 215);
        showText("Press UPGRADE to level it up.",    cx, 240);
        showText("ESC cancels placement.",           cx, 265);
        showText("Enemies lower your HP.",           cx, 300);
        showText("Earn medals by killing enemies.",  cx, 325);
        showText("---------------------",           cx, 355);
    }

    private void clearHelpOverlay()
    {
        int cx = MAP_W / 2;
        int[] ys = {130, 165, 190, 215, 240, 265, 300, 325, 355};
        for (int iy : ys) showText("", cx, iy);
    }

    // ----------------------------------------------------------------
    // Side-panel drawing
    // ----------------------------------------------------------------
    private void drawSidePanel()
    {
        GreenfootImage bg = getBackground();

        // Panel background
        bg.setColor(new Color(30, 30, 45));
        bg.fillRect(PANEL_X, 0, PANEL_W, WORLD_H);

        // Vertical border line
        bg.setColor(new Color(90, 90, 120));
        bg.drawLine(PANEL_X, 0, PANEL_X, WORLD_H);

        if (isPaused)
        {
            drawPausedPanel(bg);
        }
        else
        {
            drawNormalPanel(bg);
        }

        setBackground(bg);
    }

    private void drawNormalPanel(GreenfootImage bg)
    {
        int px = PANEL_X + 4;

        // --- Stats ---
        bg.setColor(new Color(255, 220, 80));
        bg.drawString("Medals: " + player.getMedals(), px, 18);
        bg.setColor(new Color(100, 220, 100));
        bg.drawString("HP:     " + player.getHealth(),  px, 34);
        bg.setColor(new Color(180, 180, 255));
        bg.drawString("Round:  " + player.getRound(),   px, 50);

        // --- Shop header ---
        bg.setColor(new Color(200, 200, 200));
        bg.drawString("-- BUY TOWER --", px, 68);

        // Shop buttons
        drawBtn(bg, "Fast Tower  75g",  BTN_FAST_Y,   pendingTowerType == 1,
                new Color(60, 100, 160));
        drawBtn(bg, "Long Range  50g",  BTN_LONG_Y,   pendingTowerType == 2,
                new Color(60, 100, 160));
        drawBtn(bg, "Splash     125g",  BTN_SPLASH_Y, pendingTowerType == 3,
                new Color(60, 100, 160));

        // --- Separator ---
        bg.setColor(new Color(80, 80, 110));
        bg.drawLine(PANEL_X + 4, 198, PANEL_X + PANEL_W - 4, 198);

        // --- Selected tower info ---
        bg.setColor(new Color(200, 200, 200));
        bg.drawString("-- SELECTED --", px, 214);

        if (selectedTower != null)
        {
            bg.setColor(new Color(255, 255, 180));
            bg.drawString(selectedTower.getName(),                        px, 227);
            bg.setColor(new Color(200, 200, 200));
            bg.drawString("Level:  " + (selectedTower.getUpgradeLevel() + 1), px, 240);
            bg.drawString("Damage: " + selectedTower.getDamage(),         px, 254);
            bg.drawString("Range:  " + selectedTower.getRange(),          px, 270);

            if (selectedTower.canUpgrade())
                drawBtn(bg, "Upgrade " + selectedTower.getNextUpgradeCost() + "g",
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

        // --- Separator ---
        bg.setColor(new Color(80, 80, 110));
        bg.drawLine(PANEL_X + 4, 320, PANEL_X + PANEL_W - 4, 320);

        // --- Pause button ---
        drawBtn(bg, "  PAUSE", BTN_PAUSE_Y, false, new Color(160, 80, 30));
    }

    private void drawPausedPanel(GreenfootImage bg)
    {
        int px = PANEL_X + 4;

        bg.setColor(new Color(255, 120, 60));
        bg.drawString("** PAUSED **", px + 8, 160);

        drawBtn(bg, "  RESUME", BTN_RESUME_Y, true,  new Color(40, 140, 60));
        drawBtn(bg, "  HELP",   BTN_HELP_Y,   showHelp, new Color(60, 80, 160));
        drawBtn(bg, " RESTART ROUND",   BTN_RESTART_RND_Y,  false, new Color(140, 100, 20));
        drawBtn(bg, " RESTART GAME",    BTN_RESTART_GAME_Y, false, new Color(160, 40, 40));

        if (showHelp)
        {
            bg.setColor(new Color(160, 220, 255));
            bg.drawString("(Click help again to remove)", px + 0, 300);
        }
    }

    // ----------------------------------------------------------------
    // Button helper
    // ----------------------------------------------------------------
    private void drawBtn(GreenfootImage bg, String label,
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

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    /** Returns true if y is within the hit-zone of a button centred at centreY. */
    private boolean inBtn(int y, int centreY)
    {
        return y >= centreY - BTN_H / 2 && y <= centreY + BTN_H / 2;
    }

    private Cell getCellAt(int col, int row)
    {
        int px = col * CELL_SZ + CELL_SZ / 2;
        int py = row * CELL_SZ + CELL_SZ / 2;
        List<Cell> cells = getObjectsAt(px, py, Cell.class);
        return cells.isEmpty() ? null : cells.get(0);
    }

    public static Tower createTower(int type)
    {
        switch (type)
        {
            case 1:  return new FastTower();
            case 2:  return new LongRangeTower();
            default: return new SplashTower();
        }
    }

    public boolean isPaused() { return isPaused; }
    public Player  getPlayer() { return player; }
}
