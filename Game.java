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

    private boolean isPaused = false;
    private boolean showHelp = false;

    private int   pendingTowerType = 0; // 0=none 1=Fast 2=Long 3=Splash
    private Tower pendingTower     = null;

    private Tower selectedTower = null;

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

    // UC4: Restart Round
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
}