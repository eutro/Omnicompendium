package eutros.omnicompendium.config;

import eutros.omnicompendium.Omnicompendium;
import eutros.omnicompendium.gui.entry.CompendiumEntries;
import eutros.omnicompendium.loader.GitLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@Mod.EventBusSubscriber(modid = Omnicompendium.MOD_ID)
public class OmCConfig {

    public static Configuration config;
    public static String url;
    public static String branch;

    public static void init(File file) {
        config = new Configuration(file);

        refresh();
    }

    public static void refresh() {
        String categoryName;
        String propertyName;

        categoryName = "Git";
        config.addCustomCategoryComment(categoryName, "Git repository configurations.");

        propertyName = "URL";
        url = config.getString(propertyName, categoryName, "https://github.com/OmnifactoryDevs/OmnifactoryGuides", "A URL pointing to the Git repository containing compendium entries.");

        propertyName = "branch";
        branch = config.getString(propertyName, categoryName, "HEAD", "The branch of the repository to use.");

        config.save();

        new Thread(() -> {
            GitLoader.syncRepo();
            CompendiumEntries.setLinkChecker(url);
        }).start();
    }

    public static void registerConfig(FMLPreInitializationEvent evt) {
        File dir = evt.getModConfigurationDirectory();
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        init(new File(dir, Omnicompendium.MOD_ID + ".cfg"));
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
        if(evt.getModID().equals(Omnicompendium.MOD_ID)) {
            refresh();
        }
    }

}
