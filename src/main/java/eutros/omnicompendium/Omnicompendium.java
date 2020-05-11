package eutros.omnicompendium;

import eutros.omnicompendium.item.ModItems;
import eutros.omnicompendium.loader.GitLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Omnicompendium.MOD_ID,
     name = Omnicompendium.NAME,
     version = Omnicompendium.VERSION)
public class Omnicompendium {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "omnicompendium";
    public static final String NAME = "Omnicompendium";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new ModItems());

        if(evt.getSide().isServer()) {
            return;
        }
        Config.registerConfig(evt);

        GitLoader.syncRepo();
    }

}
