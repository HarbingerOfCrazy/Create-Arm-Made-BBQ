package com.p1nero.create_bbq.interaction_points;

import com.mao.barbequesdelight.content.block.BasinBlockEntity;
import com.mao.barbequesdelight.content.recipe.SkeweringRecipe;
import com.mao.barbequesdelight.init.registrate.BBQDRecipes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class BasinlArmInteractionPoint extends ArmInteractionPoint {
    public BasinlArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        return Vec3.upFromBottomCenterOf(pos, .125);
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    /**
     * 如果能合成则返回串好的串串，两个槽都试试
     * 如果有空槽位则会添加食物，并且两边不会是相同物品
     */
    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (level.getBlockEntity(pos) instanceof BasinBlockEntity be) {
            ItemStack input = stack.copy();
            if(input.is(Items.STICK)) {
                ItemStack stack0 = be.getStack(0);
                ItemStack stack1 = be.getStack(1);
                for (SimpleContainer cont : Arrays.asList(new SimpleContainer(input, stack0, stack1), new SimpleContainer(input, stack1, stack0))) {
                    Optional<SkeweringRecipe<?>> optional = this.level.getRecipeManager().getRecipeFor(BBQDRecipes.RT_SKR.get(), cont, this.level);
                    if(optional.isPresent()) {
                        if(simulate) {
                            input.shrink(1);
                            return input;
                        }
                        ItemStack result = optional.get().assemble(cont, this.level.registryAccess());
                        be.notifyTile();
                        return result;
                    }
                }
                return stack;
            } else {
                if(be.getStack(0).isEmpty() || ItemStack.isSameItemSameTags(be.getStack(0), input) && !ItemStack.isSameItemSameTags(be.getStack(1), input)) {
                    if(!simulate) {
                        be.insert(level, 0, input, true);
                        be.notifyTile();
                    } else {
                        input.shrink(1);
                    }
                    return input;
                }

                if(be.getStack(1).isEmpty() || ItemStack.isSameItemSameTags(be.getStack(1), input) && !ItemStack.isSameItemSameTags(be.getStack(0), input)) {
                    if(!simulate) {
                        be.insert(level, 1, input, true);
                        be.notifyTile();
                    } else {
                        input.shrink(1);
                    }
                    return input;
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack extract(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return level.getBlockEntity(pos) instanceof BasinBlockEntity;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new BasinlArmInteractionPoint(this, level, pos, state);
        }

    }
}
