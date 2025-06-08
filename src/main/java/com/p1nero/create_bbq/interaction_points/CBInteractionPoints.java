package com.p1nero.create_bbq.interaction_points;

import com.p1nero.create_bbq.CreateBBQMod;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CreateBBQMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CBInteractionPoints {
    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, ResourceLocation.fromNamespaceAndPath(CreateBBQMod.MODID, "grill"), new GrillArmInteractionPoint.Type());
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, ResourceLocation.fromNamespaceAndPath(CreateBBQMod.MODID, "basin"), new BasinlArmInteractionPoint.Type());
    }

}
