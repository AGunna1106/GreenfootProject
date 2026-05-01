import greenfoot.*;

public class RangeCircle extends Actor
{
    private static final Color SELECTED_FILL  = new Color(255, 230,   0, 55);
    private static final Color SELECTED_RING  = new Color(255, 230,   0, 220);
    private static final Color PLACEMENT_FILL = new Color(255, 230,   0, 55);
    private static final Color PLACEMENT_RING = new Color(255, 230,   0, 220);
    private static final Color RING_BORDER    = new Color(  0,   0,   0, 180);

    public final int     radius;
    public final boolean isPlacement;

    public RangeCircle(int radius, boolean isPlacement)
    {
        this.radius      = radius;
        this.isPlacement = isPlacement;

        // Image is exactly diameter x diameter so Greenfoot centres it correctly
        int size = radius * 2;
        GreenfootImage img = new GreenfootImage(size, size);

        Color fill = isPlacement ? PLACEMENT_FILL : SELECTED_FILL;
        Color ring = isPlacement ? PLACEMENT_RING : SELECTED_RING;

        img.setColor(fill);
        img.fillOval(0, 0, size - 1, size - 1);

        img.setColor(RING_BORDER);
        img.drawOval(0, 0, size - 1, size - 1);

        img.setColor(ring);
        img.drawOval(1, 1, size - 3, size - 3);
        img.drawOval(2, 2, size - 5, size - 5);

        img.setColor(RING_BORDER);
        img.drawOval(3, 3, size - 7, size - 7);

        setImage(img);
    }

    public void act() { }
}