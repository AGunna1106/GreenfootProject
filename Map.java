import greenfoot.*;

/**
 * Map – builds the cell grid for a given map type.

 * Cell array layout: cells[row][col]
 *   addObject(cells[row][col], col*SZ+half, row*SZ+half)
 */
public class Map
{
    private Cell[][] cells;   // [row][col]
    private World world;

    public Map(World world)
    {
        this.world = world;
        int mapLength = 12;
        cells = new Cell[mapLength][mapLength];    }

    public Cell[][] addCells(int mapType)
    {
        int[][] map = mapSelection(mapType);
        // map[row][col]
        for (int row = 0; row < cells.length; row++)
        {
            for (int col = 0; col < cells[row].length; col++)
            {
                int v = map[row][col];
                if (v == 0)      // enemy path
                {
                    cells[row][col] = new Cell(new Color(160, 140, 100));
                    cells[row][col].setIsValid(false);
                    cells[row][col].setIsPath(true);
                }
                else if (v == 1) // water
                {
                    cells[row][col] = new Cell(new Color(100, 140, 180));
                    cells[row][col].setIsValid(false);
                }
                else             // land (v==2)
                {
                    cells[row][col] = new Cell(new Color(120, 170, 120));
                    cells[row][col].setIsValid(true);
                }
            }
        }
        return cells;
    }

    public int[][] mapSelection(int mapType)
    {
        int[][][] maps = {
            // Map 0 — rows top→bottom, cols left→right
            {{1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1},
             {1, 1, 0, 0, 0, 2, 0, 0, 0, 2, 1, 1},
             {2, 2, 0, 1, 0, 2, 0, 2, 0, 2, 1, 1},
             {2, 2, 0, 1, 0, 2, 0, 2, 0, 2, 1, 1},
             {2, 2, 0, 1, 0, 2, 0, 2, 0, 2, 2, 2},
             {1, 1, 0, 2, 0, 2, 0, 2, 0, 1, 1, 1},
             {2, 2, 0, 2, 0, 2, 0, 2, 0, 1, 0, 0},
             {2, 2, 0, 2, 0, 2, 0, 2, 0, 1, 0, 2},
             {2, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2},
             {1, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2},
             {2, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 1},
             {2, 2, 0, 2, 0, 0, 0, 1, 2, 2, 2, 1}},
            // Map 1
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
             {2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}}
        };
        return mapType == 0 ? maps[0] : maps[1];
    }
}
