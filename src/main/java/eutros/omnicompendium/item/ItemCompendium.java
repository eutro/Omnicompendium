package eutros.omnicompendium.item;

import eutros.omnicompendium.Omnicompendium;
import eutros.omnicompendium.gui.GuiCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class ItemCompendium extends Item {

    public ItemCompendium() {
        super();
        setMaxStackSize(1);
        setCreativeTab(new CreativeTabs("Omnicompendium") {
            @Nonnull
            @Override
            public ItemStack createIcon() {
                return new ItemStack(ItemCompendium.this);
            }
        });
        setRegistryName(Omnicompendium.MOD_ID, "compendium");
        setTranslationKey(Omnicompendium.MOD_ID + ".compendium");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if(world.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiCompendium());
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @SubscribeEvent
    public void registerModel(ModelRegistryEvent evt) {
        ModelLoader.setCustomModelResourceLocation(this,
                0,
                new ModelResourceLocation(Objects.requireNonNull(getRegistryName()),
                        "inventory"));
    }

}
