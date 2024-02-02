package com.github.eterdelta.crittersandcompanions.mixin;

import com.github.eterdelta.crittersandcompanions.capability.CACCapabilities;
import com.github.eterdelta.crittersandcompanions.item.PearlNecklaceItem;
import com.github.eterdelta.crittersandcompanions.network.CACPacketHandler;
import com.github.eterdelta.crittersandcompanions.registry.CACBlocks;
import com.github.eterdelta.crittersandcompanions.registry.CACItems;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private final Pair<Set<UUID>, Set<UUID>> savedLeashState = new ObjectObjectImmutablePair<>(new HashSet<>(), new HashSet<>());

    @Unique
    private boolean needsLeashStateLoad;

    @Unique
    private int leashStateLoadDelay;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/world/entity/LivingEntity.isInWater()Z"), method = "travel(Lnet/minecraft/world/phys/Vec3;)V")
    private boolean redirectIsInWater(LivingEntity entity) {
        return this.isInWater() || this.getFeetBlockState().is(CACBlocks.SEA_BUNNY_SLIME_BLOCK.get());
    }

    @ModifyVariable(at = @At(value = "LOAD"), method = "aiStep()V", ordinal = 0)
    private boolean modifyWaterFlag(boolean flag) {
        return flag || this.getFeetBlockState().is(CACBlocks.SEA_BUNNY_SLIME_BLOCK.get());
    }

    @ModifyVariable(at = @At(value = "LOAD", ordinal = 3), method = "travel(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 1)
    private float modifySwimSpeed(float swimSpeed) {
        if (((Entity) this) instanceof Player player) {
            Inventory inventory = player.getInventory();

            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack stack = inventory.getItem(i);
                if (stack.getItem() instanceof PearlNecklaceItem pearlNecklaceItem) {
                    return swimSpeed + (swimSpeed * ((pearlNecklaceItem.getLevel() * 20) / 100.0F));
                }
            }
        }
        return swimSpeed;
    }
}
