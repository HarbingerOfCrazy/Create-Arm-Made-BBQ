package com.p1nero.create_bbq.ponder.scene;

import com.mao.barbequesdelight.content.block.BasinBlockEntity;
import com.mao.barbequesdelight.content.block.GrillBlockEntity;
import com.mao.barbequesdelight.init.food.BBQSeasoning;
import com.mao.barbequesdelight.init.food.BBQSkewers;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.xkmc.cuisinedelight.content.block.CuisineSkilletBlockEntity;
import dev.xkmc.cuisinedelight.content.logic.CookingData;
import dev.xkmc.cuisinedelight.init.registrate.CDItems;
import dev.xkmc.cuisinedelight.init.registrate.PlateFood;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.ParticleEmitter;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.Objects;

public class CBScenes {
    public static void grill(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("grill", "Mechanical Arm & BBQ");

        BlockPos armPos = util.grid().at(2, 1, 3);
        Selection armSel = util.select().position(armPos);
        BlockPos inputDepot = util.grid().at(0, 1, 1);
        Selection input = util.select().position(inputDepot);
        BlockPos outputDepot = util.grid().at(0, 1, 3);
        BlockPos basinPos = util.grid().at(4, 2, 1);
        BlockPos grillPos = util.grid().at(2, 1, 1);
        Selection grillSel = util.select().position(grillPos);
        Selection output = util.select().position(outputDepot);
        Vec3 grillSurface = util.vector().blockSurface(grillPos, Direction.NORTH);
        Vec3 inputDepotSurface = util.vector().blockSurface(inputDepot, Direction.NORTH);

        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(20);

        scene.world().setKineticSpeed(armSel, 0);
        scene.world().showSection(armSel, Direction.DOWN);
        scene.world().showSection(grillSel, Direction.DOWN);
        scene.world().showSection(input, Direction.DOWN);
        scene.idle(20);

        //放鸡肉串上去
        ItemStack chickenSkewer = new ItemStack(BBQSkewers.CHICKEN.skewer);
        scene.world().createItemOnBeltLike(inputDepot, Direction.NORTH, chickenSkewer);
        scene.idle(20);
        scene.world().setKineticSpeed(armSel, -48);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().removeItemsFromBelt(inputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, chickenSkewer, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(grillPos, GrillBlockEntity.class, (be -> {
            be.entries[0].duration = 114514;
            be.entries[0].stack = chickenSkewer.copy();//客户端
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Mechanical Arm can put foods into heated grill.")
                .pointAt(inputDepotSurface)
                .placeNearTarget();
        scene.idle(80);

        //翻面
        scene.world().createItemOnBeltLike(inputDepot, Direction.NORTH, chickenSkewer);
        scene.idle(20);
        scene.world().setKineticSpeed(armSel, -48);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().removeItemsFromBelt(inputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, chickenSkewer, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(grillPos, GrillBlockEntity.class, (be -> {
            be.entries[0].flipped = true;
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, chickenSkewer, -1);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Mechanical Arm can flip the skewer on the grill if the skewer can be flipped.")
                .pointAt(inputDepotSurface)
                .placeNearTarget();
        scene.idle(80);

        //下料
        scene.world().hideSection(input, Direction.UP);
        scene.idle(20);
        scene.world().showSection(input, Direction.DOWN);
        scene.idle(20);

        ItemStack cumin = BBQSeasoning.CUMIN.item.get().getDefaultInstance();
        scene.world().createItemOnBeltLike(inputDepot, Direction.NORTH, cumin);
        scene.idle(20);
        scene.world().setKineticSpeed(armSel, -48);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().removeItemsFromBelt(inputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, cumin, 0);
        scene.idle(24);
        Integer color = BBQSeasoning.CUMIN.color.getColor();
        int col = color == null ? 0 : color;
        ParticleEmitter blockSpace =
                scene.effects().particleEmitterWithinBlockSpace(new DustParticleOptions(Vec3.fromRGB24(col).toVector3f(), 1.0F), grillSurface);
        scene.effects().emitParticles(grillSurface.add(0, 1.2, 0), blockSpace, 8, 2);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("If the skewer is flipped, the mechanical arm can add seasoning to it.")
                .pointAt(inputDepotSurface)
                .placeNearTarget();
        scene.idle(80);

        //取出
        ItemStack chickenCooked = new ItemStack(BBQSkewers.CHICKEN.item);
        scene.world().modifyBlockEntity(grillPos, GrillBlockEntity.class, (be -> {
            be.entries[0].stack = chickenCooked.copy();
        }));
        scene.world().hideSection(input, Direction.UP);
        scene.idle(20);
        scene.world().showSection(input, Direction.DOWN);
        scene.world().showSection(output, Direction.DOWN);
        scene.idle(20);
        scene.world().setKineticSpeed(armSel, -48);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(basinPos, GrillBlockEntity.class, (be -> {
            be.entries[0].stack = ItemStack.EMPTY;
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, chickenCooked, 1);
        scene.idle(24);
        scene.world().createItemOnBeltLike(outputDepot, Direction.NORTH, chickenCooked);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("When the grill is input side and the food is cooked, the mechanical arm can take the food.")
                .pointAt(inputDepotSurface)
                .placeNearTarget();
        scene.idle(80);
    }


    public static void basin(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("basin", "Mechanical Arm & Basin");

        BlockPos armPos = util.grid().at(2, 1, 3);
        Selection armSel = util.select().position(armPos);
        BlockPos inputDepot = util.grid().at(0, 1, 1);
        Selection input = util.select().position(inputDepot);
        BlockPos outputDepot = util.grid().at(0, 1, 3);
        BlockPos basinPos = util.grid().at(4, 2, 1);
        BlockPos grillPos = util.grid().at(2, 1, 1);
        Selection grillSel = util.select().position(grillPos);
        Selection output = util.select().position(outputDepot);
        Vec3 grillSurface = util.vector().blockSurface(grillPos, Direction.NORTH);
        Vec3 inputDepotSurface = util.vector().blockSurface(inputDepot, Direction.NORTH);

        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.idle(20);

        scene.world().setKineticSpeed(armSel, 0);
        scene.world().showSection(armSel, Direction.DOWN);
        scene.world().showSection(grillSel, Direction.DOWN);
        scene.world().showSection(util.select().fromTo(4, 1, 1, 4, 2, 1), Direction.DOWN);
        scene.world().showSection(input, Direction.DOWN);
        scene.world().showSection(output, Direction.DOWN);
        scene.idle(20);

        //放材料进去
        ItemStack chicken = new ItemStack(Items.CHICKEN);
        ItemStack onion = new ItemStack(ModItems.ONION.get());
        scene.world().createItemOnBeltLike(inputDepot, Direction.NORTH, chicken);
        scene.world().createItemOnBeltLike(outputDepot, Direction.NORTH, onion);
        scene.idle(20);
        scene.world().setKineticSpeed(armSel, -48);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().removeItemsFromBelt(inputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, chicken, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(basinPos, BasinBlockEntity.class, (be -> {
            be.items.setItem(0, chicken);
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 1);
        scene.idle(24);
        scene.world().removeItemsFromBelt(outputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, onion, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(basinPos, BasinBlockEntity.class, (be -> {
            be.items.setItem(1, onion);
        }));

        scene.world().createItemOnBeltLike(inputDepot, Direction.NORTH, chicken);
        scene.world().createItemOnBeltLike(outputDepot, Direction.NORTH, onion);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().removeItemsFromBelt(inputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, chicken, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(basinPos, BasinBlockEntity.class, (be -> {
            be.items.setItem(0, chicken.copyWithCount(2));
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 1);
        scene.idle(24);
        scene.world().removeItemsFromBelt(outputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, onion, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(basinPos, BasinBlockEntity.class, (be -> {
            be.items.setItem(1, onion.copyWithCount(2));
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Mechanical Arm can put foods into the basin. Each slot can only put one kind of food. It can also put the same item into the existing food slot.")
                .pointAt(inputDepotSurface)
                .placeNearTarget();
        scene.idle(80);

        //串串
        scene.world().hideSection(input, Direction.UP);
        scene.idle(20);
        scene.world().showSection(input, Direction.DOWN);
        scene.idle(20);
        ItemStack stick = new ItemStack(Items.STICK);
        ItemStack chickenSkewer = new ItemStack(BBQSkewers.CHICKEN.skewer);
        scene.world().removeItemsFromBelt(inputDepot);
        scene.world().createItemOnBeltLike(inputDepot, Direction.NORTH, stick);
        scene.idle(20);
        scene.world().setKineticSpeed(armSel, -48);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().removeItemsFromBelt(inputDepot);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, stick, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(basinPos, BasinBlockEntity.class, (be -> {
            be.items.setItem(0, ItemStack.EMPTY);
            be.items.setItem(1, ItemStack.EMPTY);
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, chickenSkewer, 1);
        scene.idle(24);
        scene.world().modifyBlockEntity(grillPos, GrillBlockEntity.class, (be -> {
            be.entries[0].duration = 142857;
            be.entries[0].stack = chickenSkewer;
        }));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("When holding stick, mechanical Arm can craft the skewer.")
                .pointAt(inputDepotSurface)
                .placeNearTarget();
        scene.idle(80);
    }

}
