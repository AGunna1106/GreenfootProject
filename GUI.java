import greenfoot.*;

public class GUI extends World
{
    private GameController gameController;

    private boolean loginStarted = false;
    private boolean loggedIn = false;
    private boolean waitingToSwitch = false;
    private boolean choosingMap = false;
    private int delayCounter = 0;
    private int mapType = 0;

    public GUI() {
        super(546, 504, 1);
        gameController = new GameController();
    }

    public void act() {
        delayCounter++;
        if (!loginStarted && delayCounter > 120) {
            loginStarted = true;
            login();
            return;
        }

        if (waitingToSwitch && delayCounter > 120) {
            waitingToSwitch = false;
            loggedIn = true;
            drawMenu();
            return;
        }

        if (loggedIn && Greenfoot.mouseClicked(null)) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null) {
                handleMenuClick(mouse.getX(), mouse.getY());
            }
        }
    }

    public void login()
    {
        String un = Greenfoot.ask("Enter Username:");
        String pass = Greenfoot.ask("Enter Password:");
        boolean result = gameController.verifyPlayer(un, pass);
        
        delayCounter = 0;
        if (result) {
            waitingToSwitch = true;
            showText("Login successful!", 273, 250);
        } else {
            loginStarted = false;
            showText("Wrong password. Try again.", 273, 250);
        }
    }

    private void drawMenu() {
        getBackground().clear();

        GreenfootImage bg = new GreenfootImage(getWidth(), getHeight());
        bg.setColor(new Color(0, 0, 0));
        bg.fill();
        setBackground(bg);

        showText("TOWER DEFENSE", getWidth()/2, 100);

        showText("Start Game", getWidth()/2, 200);
        showText("Map", getWidth()/2, 250);
        showText("Difficulty", getWidth()/2, 300);
        showText("Help", getWidth()/2, 350);
        showText("Leaderboard", getWidth()/2, 400);

        showText("Click an option", getWidth()/2, 470);
    }

    private void handleMenuClick(int x, int y) {
        if (isWithin(x, y, 200)) {
            startGame(mapType);
        }
        else if (isWithin(x, y, 250)) {
            choosingMap = true;
            showMessage("Select Map: 1     2");
        }
        else if (isWithin(x, y, 300)) {
            showMessage("Difficulty: Easy / Medium / Hard");
        }
        else if (isWithin(x, y, 350)) {
            showMessage("Place towers to stop enemies!");
        }
        else if (isWithin(x, y, 400)) {
            showMessage("Leaderboard coming soon!");
        }
        else if (choosingMap && y > 455 && y < 485) {
            if (x > getWidth()/2 + 30 && x < getWidth()/2 + 60) {
                mapType = 0;
                showMessage("Map 1 selected");
                choosingMap = false;
            }
            else if (x > getWidth()/2 + 60 && x < getWidth()/2 + 90) {
                mapType = 1;
                showMessage("Map 2 selected");
                choosingMap = false;
            }
        }
    }

    private boolean isWithin(int x, int y, int optionY) {
        return x > 150 && x < 400 && y > optionY - 15 && y < optionY + 15;
    }

    private void showMessage(String msg) {
        showText("", getWidth()/2, 470); // clear
        showText(msg, getWidth()/2, 470);
    }
    
    public void startGame(int mapType) {
        Cell[][] cells = gameController.initializeGame(mapType);
        showText("", getWidth()/2, 100);
        showText("", getWidth()/2, 200);
        showText("", getWidth()/2, 250);
        showText("", getWidth()/2, 300);
        showText("", getWidth()/2, 350);
        showText("", getWidth()/2, 400);
        showText("", getWidth()/2, 470);
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[i].length; j++) {
                addObject(cells[j][i], 42 * i + 21, 42 * j + 21);
            }
        }
    }
}