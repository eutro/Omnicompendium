package eutros.omnicompendium.config;

import eutros.omnicompendium.Omnicompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new Gui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public static class Gui extends GuiConfig {

        public Gui(GuiScreen parentScreen) {
            super(parentScreen, getConfigElements(), Omnicompendium.MOD_ID, false, false, Omnicompendium.NAME + " Configuration");
        }

        private static List<IConfigElement> getConfigElements() {
            return OmCConfig.config.getCategoryNames().stream()
                    .map(OmCConfig.config::getCategory)
                    .map(ConfigElement::new)
                    .collect(Collectors.toList());
        }

    }

}
