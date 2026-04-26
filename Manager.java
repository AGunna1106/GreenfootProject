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
                if (parts.length == 2) {
                    Player p = new Player(parts[0], parts[1]);
                    players.put(p.getUsername(), p);
                }
            }
        } catch (IOException e) {
        }
    }

    public void savePlayer(Player player) {
        players.put(player.getUsername(), player);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("PlayerDatabase.txt", true))) {
            bw.write(player.getUsername() + "," + player.getPassword());
            bw.newLine();
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
