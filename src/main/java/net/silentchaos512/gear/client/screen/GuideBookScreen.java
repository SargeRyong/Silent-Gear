package net.silentchaos512.gear.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.SilentGear;

public class GuideBookScreen extends Screen {
    public static final ResourceLocation TEXTURE = SilentGear.getId("textures/gui/guide_book.png");

    public GuideBookScreen(Component titleIn) {
        super(titleIn);
    }

    @Override
    protected void init() {
        super.init();
        // buttons, links?
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        drawBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void drawBackgroundLayer(PoseStack matrixStack, float partialTicks, int x, int y) {
        if (minecraft == null) return;

        RenderSystem.clearColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int posX = (this.width - 176) / 2;
        int posY = (this.height - 166) / 2;
        blit(matrixStack, posX, posY, 0, 0, 176, 166);
    }
}
