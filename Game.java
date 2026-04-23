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
    /**
     * Constructor for objects of class Game.
     * 
     */
    public Game()
    {    
        // Create a new world with 546x504 cells with a cell size of 1x1 pixels.
        super(546, 504, 1);

        map = new Map(this);
        map.addCells(1);
        map.displayMap();
        Tower tower = createTower(1);
        prepare();
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