import greenfoot.*;

public class Map
{
    private Cell[][] cells;

    public Map()
    {
        int mapLength = 12;
        cells = new Cell[mapLength][mapLength];
    }

    public Cell[][] addCells(int mapType)
    {
        int[][] map = mapSelection(mapType);
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
