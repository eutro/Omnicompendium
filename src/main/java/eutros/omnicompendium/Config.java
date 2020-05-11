package eutros.omnicompendium;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class Config {

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
    }

    public static void registerConfig(FMLPreInitializationEvent evt) {
        File dir = evt.getModConfigurationDirectory();
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        init(new File(dir, Omnicompendium.MOD_ID + ".cfg"));
    }

}
