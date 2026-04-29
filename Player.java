import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;

/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player
{
    private int medals;
    private int health;
    private int highStatsIndex;
    private String username;
    private String password;
    private ArrayList<String> pastStats;
    /**
     * Act - do whatever the Player wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
    }
    
    public Player(String un, String pass) {
        this.username = un;
        this.password = pass;
        this.pastStats = new ArrayList<>();
        
        //addPastStats("5;Easy;100;2");
        //addPastStats("10;Medium;80;3");
        //addPastStats("8;Hard;60;4");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public void setMedals(int medals) {
        this.medals = medals;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMedals() {
        return medals;
    }
    
    public String getHighStats() {
        setHighStats();
        return pastStats.isEmpty() ? null : pastStats.get(highStatsIndex);
    }
    
    private void setHighStats() {
        if (pastStats.isEmpty()) {
            highStatsIndex = -1;
            return;
        }
        int highScoreIndex = 0;
        for (int i = 1; i < pastStats.size(); i++) {
            int[] current = parseStats(pastStats.get(i));
            int[] best = parseStats(pastStats.get(highScoreIndex));
            if (isBetter(current, best)) {
                highScoreIndex = i;
            }
        }
        highStatsIndex = highScoreIndex;
    }
    
    public void addPastStats(String stats) {
        pastStats.add(stats);
        setHighStats();
    }
    
    public ArrayList<String> getPastStats() {
        return pastStats;
    }
    
    private int[] parseStats(String stat) {
        // format: rounds|difficulty|health|medals
        String[] parts = stat.split("\\;");
        int rounds = Integer.parseInt(parts[0]);
        int difficulty = difficultyValue(parts[1]); // convert to number
        int health = Integer.parseInt(parts[2]);
        int medals = Integer.parseInt(parts[3]);
        return new int[]{rounds, difficulty, health, medals};
    }
    
    private int difficultyValue(String d) {
        if (d.equals("Hard")) return 3;
        if (d.equals("Medium")) return 2;
        return 1; // Easy
    }
    
    private boolean isBetter(int[] a, int[] b) {    
        if (a[0] != b[0]) return a[0] > b[0]; // rounds
        if (a[1] != b[1]) return a[1] > b[1]; // difficulty
        if (a[2] != b[2]) return a[2] > b[2]; // health
        return a[3] > b[3]; // medals
    }
}
