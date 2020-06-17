package eutros.omnicompendium.gui;

import eutros.omnicompendium.helper.MouseHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClickableComponent {

    private final List<int[]> bounds;

    private ClickFunction callback = null;
    private Supplier<? extends List<String>> tooltip = () -> null;

    protected ClickableComponent() {
        bounds = new ArrayList<>();
    }

    @Nonnull
    public static ClickableComponent empty() {
        return new ClickableComponent();
    }

    @Nonnull
    public static ClickableComponent bySize(int x, int y, int width, int height) {
        return new ClickableComponent().addArea(x, y, width, height);
    }

    @Nonnull
    public static ClickableComponent byBounds(int minX, int minY, int maxX, int maxY) {
        return new ClickableComponent().addBounds(minX, minY, maxX, maxY);
    }

    @Nonnull
    public ClickableComponent addArea(int x, int y, int width, int height) {
        return addBounds(x, y, x + width, y + height);
    }

    @Nonnull
    public ClickableComponent addBounds(int minX, int minY, int maxX, int maxY) {
        bounds.add(new int[] {
                minX,
                minY,
                maxX,
                maxY
        });
        return this;
    }

    @Nonnull
    public ClickableComponent withCallback(@Nonnull ClickFunction callback) {
        this.callback = callback;
        return this;
    }

    @Nonnull
    public ClickableComponent withTooltip(List<String> tooltip) {
        return this.withTooltip(() -> tooltip);
    }

    @Nonnull
    public ClickableComponent withTooltip(Supplier<? extends List<String>> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        for(int[] bound : bounds) {
            if(MouseHelper.contains(bound[0], bound[1], bound[2], bound[3], mouseX, mouseY)) return true;
        }
        return false;
    }

    public boolean onClick(int mouseX, int mouseY, int mouseButton) {
        if(callback == null)
            return false;

        return isHovered(mouseX, mouseY) && callback.click(mouseX, mouseY, mouseButton);
    }

    @Nullable
    public List<String> getTooltip() {
        return tooltip.get();
    }

    @FunctionalInterface
    public interface ClickFunction {

        boolean click(int mouseX, int mouseY, int mouseButton);

    }

}
