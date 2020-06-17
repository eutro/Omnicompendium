package eutros.omnicompendium.helper;

public class MouseHelper {

    public static boolean contains(int minX, int minY, int maxX, int maxY, int mouseX, int mouseY) {
        return mouseX >= minX
                && mouseX <= maxX
                && mouseY >= minY
                && mouseY <= maxY;
    }

    public static boolean isClicked(int x, int y, int width, int height, int mouseX, int mouseY) {
        return contains(x, y, x + width, y + height, mouseX, mouseY);
    }

}
