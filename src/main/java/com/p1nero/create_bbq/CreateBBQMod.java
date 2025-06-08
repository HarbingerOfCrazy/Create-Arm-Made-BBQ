package com.p1nero.create_bbq;

import com.mao.barbequesdelight.content.block.GrillBlockItem;
import com.mao.barbequesdelight.init.registrate.BBQDBlocks;
import com.mojang.logging.LogUtils;
import com.p1nero.create_bbq.ponder.scene.CBScenes;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.catnip.platform.ForgeRegisteredObjectsHelper;
import net.createmod.ponder.api.registration.MultiTagBuilder;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateBBQMod.MODID)
public class CreateBBQMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_arm_made_bbq";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateBBQMod() {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    private static class ClientEvents{

        @SubscribeEvent
        public static void registerClient(FMLClientSetupEvent event) {
            PonderIndex.addPlugin(new PonderPlugin() {
                @Override
                public @NotNull String getModId() {
                    return CreateBBQMod.MODID;
                }

                @Override
                public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
                    ForgeRegisteredObjectsHelper forgeRegisteredObjectsHelper = new ForgeRegisteredObjectsHelper();
                    PonderSceneRegistrationHelper<Item> entryHelper = helper.withKeyFunction(forgeRegisteredObjectsHelper::getKeyOrThrow);
                    ForgeRegistries.ITEMS
                            .getValues()
                            .stream()
                            .filter(item -> item instanceof GrillBlockItem)
                            .forEach(item -> entryHelper.forComponents(item).addStoryBoard("grill_ponder", CBScenes::grill, AllCreatePonderTags.ARM_TARGETS));
                    entryHelper.forComponents(BBQDBlocks.BASIN.asItem()).addStoryBoard("basin_ponder", CBScenes::basin, AllCreatePonderTags.ARM_TARGETS);

                }

                @Override
                public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
                    ForgeRegisteredObjectsHelper forgeRegisteredObjectsHelper = new ForgeRegisteredObjectsHelper();
                    MultiTagBuilder.Tag<Item> builder = helper.withKeyFunction((Item s) -> forgeRegisteredObjectsHelper.getKeyOrThrow(s)).addToTag(AllCreatePonderTags.ARM_TARGETS);
                    ForgeRegistries.ITEMS
                            .getValues()
                            .stream()
                            .filter(item -> item instanceof GrillBlockItem || BBQDBlocks.BASIN.is(item))
                            .forEach(builder::add);
                }
            });

        }
    }

}
