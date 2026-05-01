import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Leaderboard here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Leaderboard
{
    private String[] topRanking;
    /**
     * Constructor for objects of class Leaderboard.
     * 
     */
    public Leaderboard()
    {    
        topRanking = new String[5];
    }
    
    public void setTopRanking(Player[] players) {
        for (Player p : players)
        {
            String stat = p.getHighStats();
            if (stat != null)
            {
                insert(p.getUsername() + "," + stat);
            }
        }
    }
    
    public void addCurrentPlayerStats(Player player) {
        String stat = player.getHighStats();
        if (stat != null)
        {
            insert(player.getUsername() + "," + stat);
        }
    }
    
    public String[] getTopRanking() {
        return topRanking;
    }
    
    private void insert(String entry)
    {
        String username = entry.split(",", 2)[0];
        int[] newStats = parse(entry);
    
        for (int i = 0; i < topRanking.length; i++)
        {
            if (topRanking[i] != null)
            {
                String existingUser = topRanking[i].split(",", 2)[0];
                if (existingUser.equals(username))
                {
                    int[] currentStats = parse(topRanking[i]);
                    if (newStats != null && currentStats != null && isBetter(newStats, currentStats))
                    {
                        removeAt(i);
                        break;
                    }else
                    {
                        return;
                    }
                }
            }
        }
        
        for (int i = 0; i < topRanking.length; i++)
        {
            int[] current = parse(topRanking[i]);
            if (topRanking[i] == null || (newStats != null && current != null && isBetter(newStats, current)))
            {
                shiftDown(i);
                topRanking[i] = entry;
                break;
            }
        }
    }
    
    private void removeAt(int index)
    {
        for (int i = index; i < topRanking.length - 1; i++)
        {
            topRanking[i] = topRanking[i + 1];
        }
        topRanking[topRanking.length - 1] = null;
    }
    
    private void shiftDown(int index)
    {
        for (int i = topRanking.length - 1; i > index; i--)
        {
            topRanking[i] = topRanking[i - 1];
        }
    }
    
    private int[] parse(String entry)
    {
        try {
            String[] splitUser = entry.split(",", 2);
            if (splitUser.length < 2) return null;

            String[] parts = splitUser[1].split(";");
            if (parts.length < 4) return null;
    
            int rounds = Integer.parseInt(parts[0]);
            int difficulty = difficultyValue(parts[1]);
            int health = Integer.parseInt(parts[2]);
            int medals = Integer.parseInt(parts[3]);
    
            return new int[]{rounds, difficulty, health, medals};
        } catch (Exception e) {
            return null;
        }
    }

    private int difficultyValue(String d)
    {
        if (d.equals("HARD")) return 3;
        if (d.equals("MEDIUM")) return 2;
        return 1;
    }

    private boolean isBetter(int[] a, int[] b)
    {
        if (a[0] != b[0]) return a[0] > b[0]; // rounds
        if (a[1] != b[1]) return a[1] > b[1]; // difficulty
        if (a[2] != b[2]) return a[2] > b[2]; // health
        return a[3] > b[3]; // medals
    }
}
