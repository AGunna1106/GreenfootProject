import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Game here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Game extends World
{
    private Round round;
    private Map map;
    private Player player;
    private Difficulty difficulty;
    private boolean isPaused;
    private boolean isMenu;
    /**
     * Constructor for objects of class Game.
     * 
     */
    public Game(int mapType)
    {    
        // Create a new world with 546x504 cells with a cell size of 1x1 pixels.
        super(546, 504, 1);
        
        if (mapType == -1) { // main menu
            drawMenu();
            isMenu = true;
        } else {
            map = new Map(this);
            map.addCells(mapType);
            map.displayMap();
            Tower tower = createTower(1);
            prepare();
        }
    }
    
    public void act() {
        if (isMenu && Greenfoot.mouseClicked(null)) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            int x = mouse.getX();
            int y = mouse.getY();
            handleMenuClick(x, y);
        }
    }

    private void drawMenu() {
        getBackground().clear();
        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(new Color(0, 0, 0));
        bg.fill();
        setBackground(bg);
 
        showText("TOWER DEFENSE", getWidth()/2, 100);
        // Menu options
        showText("Start Game", getWidth()/2, 200);
        showText("Map", getWidth()/2, 250);
        showText("Difficulty", getWidth()/2, 300);
        showText("Help", getWidth()/2, 350);
        showText("Leaderboard", getWidth()/2, 400);
        showText("Click an option", getWidth()/2, 470);
    }

    private void handleMenuClick(int x, int y) {
        if (isWithin(x, y, 200)) {
            Greenfoot.setWorld(new Game(0)); // default map
        }
        else if (isWithin(x, y, 250)) {
            showText("Select Map: 1   2", 273, 470);
            // you can store mapType here
        }
        else if (isWithin(x, y, 300)) {
            showText("Difficulty selected (placeholder)", 273, 470);
            // update difficulty object here
        }
        else if (isWithin(x, y, 350)) {
            showText("Place towers to stop enemies!", 273, 470);
        }
        else if (isWithin(x, y, 400)) {
            showText("View Leaderboard.", 273, 470);
        }
    }

    private boolean isWithin(int x, int y, int optionY) {
        return x > 150 && x < 400 && y > optionY - 15 && y < optionY + 15;
    }

    public static Tower createTower(int type) {
        switch(type) {
            case 1: return new FastTower();
            case 2: return new LongRangeTower();
            default: return new SplashTower();
        }
    }
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        FastTower fastTower = new FastTower();
        addObject(fastTower,563,93);
        SplashTower splashTower = new SplashTower();
        addObject(splashTower,579,315);
        LongRangeTower longRangeTower = new LongRangeTower();
        addObject(longRangeTower,548,196);
        fastTower.setLocation(568,97);
    }
}