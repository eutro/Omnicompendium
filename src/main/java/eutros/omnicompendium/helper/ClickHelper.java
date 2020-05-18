package eutros.omnicompendium.helper;

import javax.annotation.Nullable;
import java.util.List;

public class ClickHelper {

    public static boolean contains(int minX, int minY, int maxX, int maxY, int mouseX, int mouseY) {
        return mouseX >= minX
                && mouseX <= maxX
                && mouseY >= minY
                && mouseY <= maxY;
    }

    public static boolean isClicked(int x, int y, int width, int height, int mouseX, int mouseY) {
        return contains(x, y, x + width, y + height, mouseX, mouseY);
    }

    public static class ClickableComponent {

        private final int minX;
        private final int minY;
        private final int maxX;
        private final int maxY;

        private ClickFunction callback = null;
        private List<String> tooltip = null;

        protected ClickableComponent(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public static ClickableComponent bySize(int x, int y, int width, int height) {
            return new ClickableComponent(x, y, x + width, y + height);
        }

        public static ClickableComponent byBounds(int minX, int minY, int maxX, int maxY) {
            return new ClickableComponent(minX, minY, maxX, maxY);
        }

        public ClickableComponent withCallback(ClickFunction callback) {
            this.callback = callback;
            return this;
        }

        public ClickableComponent withTooltip(List<String> tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public boolean isHovered(int mouseX, int mouseY) {
            return ClickHelper.contains(minX, minY, maxX, maxY, mouseX, mouseY);
        }

        public boolean onClick(int mouseX, int mouseY, int mouseButton) {
            if(callback == null)
                return false;

            return isHovered(mouseX, mouseY)
                    && callback.click(mouseX - minX, mouseY - minY, mouseButton);
        }

        @Nullable
        public List<String> getTooltip() {
            return tooltip;
        }

    }

    @FunctionalInterface
    public interface ClickFunction {

        boolean click(int mouseX, int mouseY, int mouseButton);

    }

}
