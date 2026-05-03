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
                    if (newStats != null && currentStats != null && Player.isBetter(newStats, currentStats))
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
            if (topRanking[i] == null || (newStats != null && current != null && Player.isBetter(newStats, current)))
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
        if (entry == null) return null;
        String[] splitUser = entry.split(",", 2);
        if (splitUser.length < 2) return null;
        return Player.parseStats(splitUser[1]);
    }
}