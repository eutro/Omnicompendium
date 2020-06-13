package eutros.omnicompendium.loader;

import eutros.omnicompendium.Omnicompendium;
import eutros.omnicompendium.helper.FileHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
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

    public static void load() {
        clear();
        List<File> images = FileHelper.getPNGs();
        IntBuffer intBuf = ByteBuffer.allocateDirect(images.size() * 4).asIntBuffer();
        GL11.glGenTextures(intBuf);
        intBuf.rewind();

        for(File image : images) {
            BufferedImage im;
            try {
                im = ImageIO.read(image);
            } catch(IOException e) {
                Omnicompendium.LOGGER.warn(String.format("Failed to load image: %s", image), e);
                GL11.glDeleteTextures(intBuf.get());
                continue;
            }

            textureMap.put(image, new Image(intBuf.get(), im));
        }
    }

    private static void clear() {
        for(Image im : textureMap.values()) {
            GL11.glDeleteTextures(im.tex);
        }
        textureMap.clear();
    }

    @Nullable
    public static Image get(File file) {
        return textureMap.get(file);
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
