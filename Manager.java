/**
 * Write a description of class Manager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Manager  
{
    private Map<String, Player> players = new HashMap<>();
    
    public Manager() {
        try (BufferedReader br = new BufferedReader(new FileReader("PlayerDatabase.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    Player p = new Player(parts[0], parts[1]);
                    if (parts.length == 3) {
                        String[] stats = parts[2].split("\\|");
                        for (String s : stats) {
                            if (!s.isEmpty()) {
                                p.addPastStats(s);
                            }
                        }
                    }
                    players.put(p.getUsername(), p);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading PlayerDatabase.txt");
            e.printStackTrace();
        }
    }

    public void savePlayer(Player player) {
        players.put(player.getUsername(), player);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("PlayerDatabase.txt"))) {
            for (Player p : players.values()) {
                StringBuilder stats = new StringBuilder();
                if (!p.getPastStats().isEmpty()) {
                    for (String s : p.getPastStats()) {
                        stats.append(s).append("|");
                    }
                    stats.deleteCharAt(stats.length() - 1);
                    bw.write(p.getUsername() + "," + p.getPassword() + "," + stats.toString());
                } else {
                    bw.write(p.getUsername() + "," + p.getPassword());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer(String un, String pass) {
        Player p = players.get(un);
        if (p != null && p.getPassword().equals(pass)) {
            return p;
        } else if (p == null) {
            p = new Player(un, pass);
            savePlayer(p);
            return p;
        } else {
            return null; //Username found but wrong password
        }
    }
    
    public Player[] getAllPlayers() {
        return players.values().toArray(new Player[0]);
    }
}
