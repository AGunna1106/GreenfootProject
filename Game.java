import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Game – the active Greenfoot world during gameplay; manages all game-session state.
 *
 * GRASP Roles (iter2.md):
 *   Creator         : creates Round (Game->Round); delegates Map->Cell chain to Map
 *   Indirection     : mediates between GUI input routing and domain objects
 *   Information Expert : owns game-session state (pause flag, towers list, round)
 *   Low Coupling / High Cohesion : ZERO display/layout code here;
 *                                  all rendering delegated back to GUI
 *
 * Responsibilities: player state, tower placement/upgrade, pause/resume,
 *                   round snapshot/restore, world-switch on restart.
 *
 * GUI is responsible for: all panel layout, button positions, colours, and
 *                          routing panel clicks back into Game's public API.
 *
 * UC2  Play Game  – Steps 2 (initMap), 4 (round), 8 (end)
 * UC4  Restart    – restartRound(), restartGame()
 * UC6  Place Tower
 * UC7  Upgrade Tower
 * UC9  Pause / Resume
 */
public class Game extends World
{
    // ----------------------------------------------------------------
    // Layout constants — owned here; GUI reads them via Game.*
    // ----------------------------------------------------------------
    public static final int MAP_W   = 504;   // 12 cells x 42 px
    public static final int MAP_H   = 504;
    public static final int CELL_SZ = 42;
    public static final int PANEL_W = 156;
    public static final int WORLD_W = MAP_W + PANEL_W;  // 660
    public static final int WORLD_H = 504;
    public static final int PANEL_X = MAP_W;             // 504

    // ----------------------------------------------------------------
    // Domain objects
    // Creator chain (iter2.md): Controller->Game, Game->Round, Map->Cell
    // ----------------------------------------------------------------
    private Map    map;
    private Player player;
    private Round  round;
    private GUI    gui;   // reference only for restartGame() world-switch

    // ----------------------------------------------------------------
    // UC9: Pause state
    // Information Expert: Game manages isPaused; Round/Enemy/Tower manage own behavior
    // ----------------------------------------------------------------
    private boolean isPaused = false;
    private boolean showHelp = false;

    // ----------------------------------------------------------------
    // UC6: Placement state
    // ----------------------------------------------------------------
    private int   pendingTowerType = 0;
    private Tower pendingTower     = null;

    // UC7: Selected tower for upgrade
    private Tower selectedTower = null;

    // All placed towers (needed for round snapshot/restore)
    private List<Tower> towers = new ArrayList<>();

    // ----------------------------------------------------------------
    // Constructor — called by GameController.initializeGame()
    // iter2.md: Controller creates Game (Creator)
    // ----------------------------------------------------------------
    public Game(Player player, GUI gui)
    {
        super(WORLD_W, WORLD_H, 1);
        this.player = player;
        this.gui    = gui;
        this.round  = new Round();    // Creator: Game creates Round
        this.map    = new Map(this);  // Creator: Game creates Map

        GreenfootImage bg = new GreenfootImage(WORLD_W, WORLD_H);
        bg.setColor(new Color(20, 20, 30));
        bg.fill();
        setBackground(bg);
    }

    // ----------------------------------------------------------------
    // UC2 Step 2 — Initialize Game
    // Delegates cell creation to Map (Map creates Cells); adds them to this world.
    // Called once by GameController after construction.
    // ----------------------------------------------------------------
    public void initMap(int mapType)
    {
        Cell[][] cells = map.addCells(mapType);

        for (int row = 0; row < cells.length; row++)
            for (int col = 0; col < cells[row].length; col++)
                addObject(cells[row][col],
                          CELL_SZ * col + CELL_SZ / 2,
                          CELL_SZ * row + CELL_SZ / 2);

        round.snapshotRoundStart(player, towers, CELL_SZ);
    }

    // ----------------------------------------------------------------
    // act() — Greenfoot calls this every frame while Game is active world.
    // Detects input and forwards to the appropriate handler.
    // All panel rendering is delegated to GUI; Game only manages state.
    // ----------------------------------------------------------------
    public void act()
    {
        // ESC cancels pending placement
        if (Greenfoot.isKeyDown("escape") && pendingTowerType != 0)
        {
            cancelPlacement();
            showStatusMessage("Placement cancelled.");
            gui.refreshPanel(this);
            return;
        }

        // Hover highlight during placement (map area only)
        if (!isPaused && pendingTowerType != 0)
            updatePlacementHighlight();

        // Route mouse clicks — GUI handles panel layout decisions
        if (Greenfoot.mouseClicked(null))
        {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null)
            {
                int x = mouse.getX();
                int y = mouse.getY();

                if (x >= PANEL_X)
                    gui.handlePanelClick(y, this);  // GUI routes panel input to Game
                else
                    handleMapClick(x, y);           // Game handles map-area logic directly
            }
        }
    }

    // ----------------------------------------------------------------
    // Map area click handler (not a display concern — stays in Game)
    // ----------------------------------------------------------------
    private void handleMapClick(int x, int y)
    {
        if (isPaused) return;
        if (pendingTowerType != 0)
            attemptPlacement(x, y);  // UC6
        else
            selectTowerAt(x, y);     // UC7
    }

    // ----------------------------------------------------------------
    // UC6: Placement highlight — runs every frame while pendingTowerType != 0
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // UC9 Step 2: Pause — Game sets state; GUI redraws panel
    // ----------------------------------------------------------------
    public void doPause()
    {
        isPaused = true;
        showHelp = false;
        gui.refreshPanel(this);
    }

    // ----------------------------------------------------------------
    // UC9 Step 6: Resume — Game restores state; GUI redraws panel
    // ----------------------------------------------------------------
    public void doResume()
    {
        isPaused = false;
        showHelp = false;
        clearHelpText();
        gui.refreshPanel(this);
    }

    // ----------------------------------------------------------------
    // UC5: Help overlay toggle
    // GUI draws the panel buttons; Game drives which showText lines appear
    // (showText is a World method — must be called on the active world)
    // ----------------------------------------------------------------
    public void toggleHelp()
    {
        showHelp = !showHelp;
        applyHelpText();
        gui.refreshPanel(this);
    }

    /** Writes or clears help overlay text in the map area. */
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

    // ----------------------------------------------------------------
    // UC4: Restart Round
    // Game restores domain state (player stats, tower grid) from snapshot.
    // Information Expert: Round knows the snapshot; Player knows its own state.
    // ----------------------------------------------------------------
    public void restartRound()
    {
        for (Tower t : towers) removeObject(t);
        towers.clear();
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

    // ----------------------------------------------------------------
    // UC4: Restart Game
    // Switching worlds discards this Game world entirely — all showText
    // state is gone automatically. GUI.prepareForReturn() clears only
    // GUI's own stale menu text before the login dialog appears.
    // ----------------------------------------------------------------
    public void restartGame()
    {
        gui.prepareForReturn();
        Greenfoot.setWorld(gui);
    }

    // ----------------------------------------------------------------
    // UC6: Tower placement
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // UC7: Tower selection and upgrade
    // Information Expert: Player and Tower manage their own state
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // UC2 Step 4: Begin next round — snapshot new state for restart
    // ----------------------------------------------------------------
    public void beginNewRound()
    {
        player.nextRound();
        round.snapshotRoundStart(player, towers, CELL_SZ);
    }

    // ----------------------------------------------------------------
    // Status bar text — World.showText must be called on the active world
    // ----------------------------------------------------------------
    public void showStatusMessage(String msg)
    {
        showText(msg, MAP_W / 2, WORLD_H - 15);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // Getters — GUI reads state through these; never accesses fields directly
    // ----------------------------------------------------------------
    public boolean isPaused()            { return isPaused; }
    public boolean isShowHelp()          { return showHelp; }
    public Player  getPlayer()           { return player; }
    public Tower   getSelectedTower()    { return selectedTower; }
    public int     getPendingTowerType() { return pendingTowerType; }
}
