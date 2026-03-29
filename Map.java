import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Map
{
    private Cell[][] cells;
    private World world;
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public Map(World world)
    {    
        this.world = world;
        int mapLength = 12;
        cells = new Cell[mapLength][mapLength];
    }
    
    public void addCells(int mapType) {
        int[][] map = mapSelection(mapType); //select map 0 or 1
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[i].length; j++) {
                if(map[j][i] == 0) { //enemy path
                    cells[j][i] = new Cell(new Color(160, 140, 100));
                    cells[j][i].setIsValid(false);
                    cells[j][i].setIsPath(true);
                } else if(map[j][i] == 1) { //water
                    cells[j][i] = new Cell(new Color(100, 140, 180));
                    cells[j][i].setIsValid(false);
                } else if(map[j][i] == 2) { //land
                    cells[j][i] = new Cell(new Color(120, 170, 120));
                    cells[j][i].setIsValid(true);
                }
            }
        }
    }
    
    public int[][] mapSelection(int mapType) {
        int[][][] maps =   {{{1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1},
                             {1, 1, 0, 0, 0, 2, 0, 0, 0, 2, 1, 1},
                             {2, 2, 0, 1, 0, 2, 0, 2, 0, 2, 1, 1},
                             {2, 2, 0, 1, 0, 2, 0, 2, 0, 2, 1, 1},
                             {2, 2, 0, 1, 0, 2, 0, 2, 0, 2, 2, 2},
                             {1, 1, 0, 2, 0, 2, 0, 2, 0, 1, 1, 1},
                             {2, 2, 0, 2, 0, 2, 0, 2, 0, 1, 0, 0},
                             {2, 2, 0, 2, 0, 2, 0, 2, 0, 1, 0, 2},
                             {2, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2},
                             {0, 0, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2},
                             {2, 2, 2, 2, 0, 2, 0, 2, 0, 0, 0, 1},
                             {2, 2, 2, 2, 0, 0, 0, 1, 2, 2, 2, 1}},
                            {{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                             {1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0},
                             {2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0},
                             {2, 0, 2, 0, 2, 2, 2, 2, 2, 0, 2, 0},
                             {2, 0, 2, 0, 2, 0, 0, 0, 2, 0, 2, 0},
                             {2, 0, 2, 0, 2, 0, 1, 0, 2, 0, 2, 0},
                             {2, 0, 2, 0, 2, 0, 1, 0, 2, 0, 2, 0},
                             {2, 0, 2, 0, 2, 0, 2, 2, 2, 0, 2, 0},
                             {2, 0, 2, 0, 2, 0, 0, 0, 0, 0, 2, 0},
                             {2, 0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 0},
                             {2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                             {0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}}};
        return mapType == 0 ? maps[0] : maps[1];
    }
    
    public void displayMap() {
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[i].length; j++) {
                world.addObject(cells[j][i], 42 * i + 21, 42 * j + 21);
            }
        }
    }
}
