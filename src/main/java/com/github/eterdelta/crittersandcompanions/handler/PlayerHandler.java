package com.github.eterdelta.crittersandcompanions.handler;

import com.github.eterdelta.crittersandcompanions.CrittersAndCompanions;
import com.github.eterdelta.crittersandcompanions.capability.CACCapabilities;
import com.github.eterdelta.crittersandcompanions.capability.IBubbleStateCapability;
import com.github.eterdelta.crittersandcompanions.entity.DumboOctopusEntity;
import com.github.eterdelta.crittersandcompanions.entity.KoiFishEntity;
import com.github.eterdelta.crittersandcompanions.network.CACPacketHandler;
import com.github.eterdelta.crittersandcompanions.network.ClientboundBubbleStatePacket;
import com.github.eterdelta.crittersandcompanions.registry.CACItems;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber(modid = CrittersAndCompanions.MODID)
public class PlayerHandler {

    public static InteractionHand getOppositeHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.side.isServer()) {
                List<KoiFishEntity> nearKoiFishes = event.player.getLevel().getEntitiesOfClass(KoiFishEntity.class, event.player.getBoundingBox().inflate(10.0D), EntitySelector.ENTITY_STILL_ALIVE);

                if (nearKoiFishes.size() >= 3) {
                    event.player.addEffect(new MobEffectInstance(MobEffects.LUCK, 210, 0, false, false));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player trackedPlayer) {
            LazyOptional<IBubbleStateCapability> bubbleCap = trackedPlayer.getCapability(CACCapabilities.BUBBLE_STATE);

            bubbleCap.ifPresent(trackedState -> {
                CACPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
                        new ClientboundBubbleStatePacket(trackedState.isActive(), trackedPlayer.getId()));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerStopTracking(PlayerEvent.StopTracking event) {
        if (event.getTarget() instanceof DumboOctopusEntity dumboOctopus) {
            if (dumboOctopus.getBubbledPlayer() == event.getEntity()) {
                dumboOctopus.sendBubble((ServerPlayer) event.getEntity(), false);
            }
        }
    }
}
