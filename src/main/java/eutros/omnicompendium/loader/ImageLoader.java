package eutros.omnicompendium.loader;

import eutros.omnicompendium.Omnicompendium;
import eutros.omnicompendium.helper.FileHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageLoader {

    private static Map<File, Image> textureMap = new HashMap<>();

    public static Image missing = null;

    public static void load() {
        List<Pair<File, BufferedImage>> images = FileHelper.getImages();
        Minecraft.getMinecraft().addScheduledTask(() -> {
            clear();
            Omnicompendium.LOGGER.info("Loading images.");
            IntBuffer intBuf = ByteBuffer.allocateDirect(images.size() * 4).asIntBuffer();
            GL11.glGenTextures(intBuf);
            intBuf.rewind();

            try {
                BufferedImage image = ImageIO.read(
                        Minecraft.getMinecraft()
                                .getResourceManager()
                                .getResource(new ResourceLocation(Omnicompendium.MOD_ID, "textures/gui/missing_image.png"))
                                .getInputStream()
                );
                missing = new Image(GlStateManager.generateTexture(), image);
            } catch(IOException e) {
                Omnicompendium.LOGGER.warn("Failed to load missing image texture.", e);
            }

            for(Pair<File, BufferedImage> pair : images) {
                textureMap.put(pair.getLeft(), new Image(intBuf.get(), pair.getRight()));
            }
            Omnicompendium.LOGGER.info("Finished loading images.");
        });
    }

    private static void clear() {
        for(Image im : textureMap.values()) {
            GL11.glDeleteTextures(im.tex);
        }
        textureMap.clear();
    }

    public static Image get(File file) {
        return textureMap.getOrDefault(file, missing);
    }

    public static class Image {

        private final int tex;
        private final int width;
        private final int height;

        public Image(int tex, BufferedImage image) {
            this.tex = tex;
            width = image.getWidth();
            height = image.getHeight();

            TextureUtil.uploadTextureImage(tex, image);
        }

        public int[] draw(int x, int y, int maxWidth) {
            GlStateManager.bindTexture(tex);

            Tessellator tes = Tessellator.getInstance();
            BufferBuilder buf = tes.getBuffer();

            final int z = 0;
            int width = this.width;
            int height = this.height;
            if(maxWidth < width) {
                height = height * maxWidth / width;
                width = maxWidth;
            }

            GlStateManager.color(1, 1, 1, 1);

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            buf.pos(x, y, z).tex(0, 0).endVertex();
            buf.pos(x, y + height, z).tex(0, 1).endVertex();
            buf.pos(x + width, y + height, z).tex(1, 1).endVertex();
            buf.pos(x + width, y, z).tex(1, 0).endVertex();

            tes.draw();

            return new int[] {width, height};
        }

    }

}
