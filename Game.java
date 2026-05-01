import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

//World is 660 wide: 504px map (12x42 grid) + 156px side panel.

public class Game extends World
{
    // Layout constants
    public static final int MAP_W   = 504;   // 12 cells x 42 px
    public static final int MAP_H   = 504;
    public static final int CELL_SZ = 42;
    public static final int PANEL_W = 156;
    public static final int WORLD_W = MAP_W + PANEL_W;  // 660
    public static final int WORLD_H = 504;
    public static final int PANEL_X = MAP_W;             // 504

    private Map    map;
    private Player player;
    private Round  round;
    private GUI    gui;   // reference only for restartGame() world-switch

    private int mapType = 0;

    private boolean isPaused = false;
    private boolean showHelp = false;

    // Wave / spawning state
    private boolean roundActive   = false;
    private int     spawnQueue    = 0;
    private int     spawnCooldown = 0;
    private static final int SPAWN_INTERVAL = 60; // ~1 s at 60 fps

    private int   pendingTowerType = 0; // 0=none 1=Fast 2=Long 3=Splash
    private Tower pendingTower     = null;

    private Tower selectedTower = null;
    private RangeCircle rangeCircle   = null;

    private List<Tower> towers = new ArrayList<>();

    // Constructor
    public Game(Player player, GUI gui)
    {
        super(WORLD_W, WORLD_H, 1);
        this.player = player;
        this.gui    = gui;
        this.round  = new Round();
        this.map    = new Map(this);
    }

    public Cell[][] setMap(int mapType)
    {
        this.mapType = mapType;
        Cell[][] cells = map.addCells(mapType);

        for (int row = 0; row < cells.length; row++)
            for (int col = 0; col < cells[row].length; col++)
                addObject(cells[row][col],
                          CELL_SZ * col + CELL_SZ / 2,
                          CELL_SZ * row + CELL_SZ / 2);

        round.snapshotRoundStart(player, towers, CELL_SZ);
        return cells;
    }

    public void act()
    {
        gui.refreshPanel(this);
        updateRangeCircle();   
        // ESC cancels pending placement
        if (Greenfoot.isKeyDown("escape") && pendingTowerType != 0)
        {
            cancelPlacement();
            showStatusMessage("Placement cancelled.");
            gui.refreshPanel(this);
            return;
        }

        // Hover highlight during placement
        if (!isPaused && pendingTowerType != 0)
            updatePlacementHighlight();

        if (Greenfoot.mouseClicked(null))
        {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null)
            {
                int x = mouse.getX();
                int y = mouse.getY();

                if (x >= PANEL_X)
                    gui.handlePanelClick(y, this); 
                else
                    handleMapClick(x, y);
            }
        }

        if (!isPaused)
        {
            tickSpawner();
            checkRoundEnd();
            if (!player.isAlive()) handleGameOver();
        }
    }

    private void handleMapClick(int x, int y)
    {
        if (isPaused) return;
        if (pendingTowerType != 0)
            attemptPlacement(x, y);  // UC6
        else
            selectTowerAt(x, y);     // UC7
    }

    private void updatePlacementHighlight()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        for (Cell c : getObjects(Cell.class)) c.clearHighlight();

        if (mouse != null && mouse.getX() < MAP_W)
        {
            int col = mouse.getX() / CELL_SZ;
            int row = mouse.getY() / CELL_SZ;
            Cell cell = getCellAt(col, row);
            if (cell != null) cell.highlight(cell.isValid());
        }
    }

    public void doPause()
    {
        isPaused = true;
        showHelp = false;
        gui.refreshPanel(this);
    }

    public void doResume()
    {
        isPaused = false;
        showHelp = false;
        clearHelpText();
        gui.refreshPanel(this);
    }

    public void toggleHelp()
    {
        showHelp = !showHelp;
        applyHelpText();
        gui.refreshPanel(this);
    }

    private void applyHelpText()
    {
        int cx = MAP_W / 2;
        if (showHelp)
        {
            showText("---- HOW TO PLAY ----",         cx, 130);
            showText("Buy towers from right panel.",  cx, 165);
            showText("Click a GREEN tile to place.",  cx, 190);
            showText("Click a tower to select it.",   cx, 215);
            showText("Press UPGRADE to level it up.", cx, 240);
            showText("ESC cancels placement.",        cx, 265);
            showText("Enemies lower your HP.",        cx, 300);
            showText("Kill enemies to earn medals.",  cx, 325);
            showText("---------------------",         cx, 355);
        }
        else
        {
            clearHelpText();
        }
    }

    private void clearHelpText()
    {
        int cx = MAP_W / 2;
        for (int yy : new int[]{130, 165, 190, 215, 240, 265, 300, 325, 355})
            showText("", cx, yy);
    }

    // ── Wave / Round Progression ─────────────────────────────────────
    /** Called by the panel "Start Wave" button. */
    public void startWave()
    {
        if (roundActive) return;
        int r = player.getRound();
        spawnQueue    = 5 + (r - 1) * 2;   // 5, 7, 9, 11 …
        spawnCooldown = 0;
        roundActive   = true;
        showStatusMessage("Round " + r + " — " + spawnQueue + " enemies incoming!");
        gui.refreshPanel(this);
    }

    private void tickSpawner()
    {
        if (!roundActive || spawnQueue <= 0) return;
        if (spawnCooldown > 0) { spawnCooldown--; return; }
        spawnEnemy();
        spawnQueue--;
        spawnCooldown = SPAWN_INTERVAL;
    }

    private void spawnEnemy()
    {
        int r   = player.getRound();
        int hp  = 3  + (r - 1) * 2;
        int dmg = 1  + (r - 1);
        int spd = 1   + Math.min((r - 1) / 3, 2); // 1→2→3, capped at 3
        Enemy e = new Enemy(hp, dmg, spd, mapType, player);
        // Map 1 path starts at (100,400); Map 0 at (60,480)
        int[] start = (mapType == 0) ? new int[]{100, 400} : new int[]{60, 480};
        addObject(e, start[0], start[1]);
    }

    private void checkRoundEnd()
    {
        if (!roundActive) return;
        if (spawnQueue > 0) return;
        if (!getObjects(Enemy.class).isEmpty()) return;

        roundActive = false;
        int bonus = 50 + player.getRound() * 10;
        player.addMedals(bonus);
        showStatusMessage("Round " + player.getRound() + " complete!  +" + bonus + " medal bonus");
        beginNewRound();
        gui.refreshPanel(this);
    }

    private void handleGameOver()
    {
        for (Enemy e : getObjects(Enemy.class)) removeObject(e);
        roundActive = false;
        gui.prepareForReturn();
        Greenfoot.setWorld(gui);
    }

    /** Total enemies still alive or yet to spawn this wave. */
    public int getEnemiesRemaining()
    {
        return spawnQueue + getObjects(Enemy.class).size();
    }

    public boolean isRoundActive() { return roundActive; }

    // UC4: Restart Round
    public void restartRound()
    {
        for (Tower t : towers) removeObject(t);
        towers.clear();
        for (Enemy e : getObjects(Enemy.class)) removeObject(e);
        roundActive   = false;
        spawnQueue    = 0;
        spawnCooldown = 0;
        selectedTower    = null;
        pendingTowerType = 0;
        if (pendingTower != null) { removeObject(pendingTower); pendingTower = null; }

        for (Cell c : getObjects(Cell.class)) c.setOccupied(false);

        round.restorePlayerToSnapshot(player);

        for (Round.TowerRecord rec : round.getSnapshotTowers())
        {
            Tower t = createTower(rec.type);
            t.applyUpgradeLevelDirectly(rec.upgradeLevel);
            t.place();
            addObject(t, rec.col * CELL_SZ + CELL_SZ / 2,
                         rec.row * CELL_SZ + CELL_SZ / 2);
            towers.add(t);
            Cell cell = getCellAt(rec.col, rec.row);
            if (cell != null) cell.setOccupied(true);
        }

        isPaused = false;
        showHelp = false;
        clearHelpText();
        showStatusMessage("Round restarted!");
        gui.refreshPanel(this);
    }

    // UC4: Restart Game
    public void restartGame()
    {
        gui.prepareForReturn();
        Greenfoot.setWorld(gui);
    }

    // UC6: Tower placement
    public void startPlacement(int type)
    {
        Tower probe = createTower(type);
        if (!player.canAfford(probe.getCost()))
        {
            showStatusMessage("Need " + probe.getCost() + " medals!");
            return;
        }
        cancelPlacement();
        pendingTowerType = type;
        pendingTower     = probe;
        addObject(pendingTower, MAP_W - 21, 21);
        showStatusMessage("Click a green cell to place  |  ESC = cancel");
        gui.refreshPanel(this);
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
            player.adjustMedals(-t.getCost());
            t.place();
            addObject(t, col * CELL_SZ + CELL_SZ / 2,
                         row * CELL_SZ + CELL_SZ / 2);
            towers.add(t);
            cell.setOccupied(true);

            pendingTowerType = 0;
            pendingTower     = null;

            showStatusMessage("Tower placed!");
            gui.refreshPanel(this);
        }
        else
        {
            showStatusMessage("Invalid tile – pick a green cell.");
        }
    }

    private void cancelPlacement()
    {
        if (pendingTower != null) { removeObject(pendingTower); pendingTower = null; }
        pendingTowerType = 0;
    }

    // UC7: Tower selection and upgrade
    private void selectTowerAt(int x, int y)
    {
        for (Tower t : towers)
        {
            if (Math.abs(t.getX() - x) <= CELL_SZ / 2
             && Math.abs(t.getY() - y) <= CELL_SZ / 2)
            {
                selectedTower = t;
                gui.refreshPanel(this);
                return;
            }
        }
        selectedTower = null;
        gui.refreshPanel(this);
    }

    public void handleUpgrade()
    {
        if (selectedTower == null) return;
        if (!selectedTower.canUpgrade())
        {
            showStatusMessage("Already max level!");
            return;
        }
        int cost = selectedTower.getNextUpgradeCost();
        if (!player.canAfford(cost))
        {
            showStatusMessage("Need " + cost + " medals to upgrade!");
            return;
        }
        selectedTower.doUpgrade(player);
        showStatusMessage("Upgraded to level " + (selectedTower.getUpgradeLevel() + 1) + "!");
        gui.refreshPanel(this);
    }

    public void beginNewRound()
    {
        player.nextRound();
        round.snapshotRoundStart(player, towers, CELL_SZ);
    }


    public void showStatusMessage(String msg)
    {
        showText(msg, MAP_W / 2, WORLD_H - 15);
    }

    private Cell getCellAt(int col, int row)
    {
        int px = col * CELL_SZ + CELL_SZ / 2;
        int py = row * CELL_SZ + CELL_SZ / 2;
        List<Cell> list = getObjectsAt(px, py, Cell.class);
        return list.isEmpty() ? null : list.get(0);
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
    
    

    public boolean isPaused()            { return isPaused; }
    public boolean isShowHelp()          { return showHelp; }
    public Player  getPlayer()           { return player; }
    public Tower   getSelectedTower()    { return selectedTower; }
    public int     getPendingTowerType() { return pendingTowerType; }
    
    private void updateRangeCircle()
{
    int cx = -1, cy = -1, radius = -1;
    boolean isPlacement = false;

    if (pendingTowerType != 0)
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null && mouse.getX() < MAP_W)
        {
            int col = mouse.getX() / CELL_SZ;
            int row = mouse.getY() / CELL_SZ;
            cx = col * CELL_SZ + CELL_SZ / 2;
            cy = row * CELL_SZ + CELL_SZ / 2;
            radius = createTower(pendingTowerType).getRange();
            isPlacement = true;
        }
    }
    else if (selectedTower != null)
    {
        cx = selectedTower.getX();
        cy = selectedTower.getY();
        radius = selectedTower.getRange();
        isPlacement = false;
    }

    if (radius < 0)
    {
        if (rangeCircle != null) { removeObject(rangeCircle); rangeCircle = null; }
        return;
    }

    // Recreate if missing, type changed, or radius changed
    if (rangeCircle == null
            || rangeCircle.isPlacement != isPlacement
            || rangeCircle.radius != radius)
    {
        if (rangeCircle != null) removeObject(rangeCircle);
        rangeCircle = new RangeCircle(radius, isPlacement);
        addObject(rangeCircle, cx, cy);
    }
    else
    {
        rangeCircle.setLocation(cx, cy);
    }
}

}   