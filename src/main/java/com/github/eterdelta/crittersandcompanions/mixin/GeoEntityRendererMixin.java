package com.github.eterdelta.crittersandcompanions.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Set;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends LivingEntity & IAnimatable> extends EntityRenderer<T> implements IGeoRenderer<T> {
    public GeoEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Unique
    private static void addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix4f, float p_174310_, float p_174311_, float p_174312_, int p_174313_, int p_174315_, int p_174316_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_, float gradient) {
        float f = (float) p_174321_ / 24.0F;
        int i = (int) Mth.lerp(f, (float) p_174313_, (float) 1);
        int j = (int) Mth.lerp(f, (float) p_174315_, (float) p_174316_);
        int k = LightTexture.pack(i, j);
        float f1 = p_174321_ % 2 == (p_174322_ ? 1 : 0) ? 0.7F : 1.0F;
        float r = 0.75F * f1 * gradient;
        float g = 0.72F * f1 * gradient;
        float b = 0.8F * f1 * gradient;
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        vertexConsumer.vertex(matrix4f, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(r, g, b, 1.0F).uv2(k).endVertex();
        vertexConsumer.vertex(matrix4f, f5 + p_174319_, f6 + (float) 0.025 - p_174318_, f7 - p_174320_).color(r, g, b, 1.0F).uv2(k).endVertex();
    }
}
