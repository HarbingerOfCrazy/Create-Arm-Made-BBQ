package com.p1nero.create_bbq.interaction_points;

import com.mao.barbequesdelight.content.block.GrillBlockEntity;
import com.mao.barbequesdelight.content.item.SeasoningItem;
import com.mao.barbequesdelight.init.food.BBQSeasoning;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GrillArmInteractionPoint extends ArmInteractionPoint {
    public GrillArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        return Vec3.upFromBottomCenterOf(pos, 1);
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    /**
     * 往锅里加菜或拿着盘子的时候取菜
     *
     * @return 剩余物品
     */
    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (level.getBlockEntity(pos) instanceof GrillBlockEntity be) {
            ItemStack input = stack.copy();

            if (!input.isEmpty()) {
                if (be.isHeated()) {
                    //尝试下料
                    if (input.getItem() instanceof SeasoningItem seasoningItem) {
                        for (GrillBlockEntity.ItemEntry itemEntry : be.entries) {
                            if (seasoningItem.canSprinkle(itemEntry.stack) && itemEntry.flipped && !itemEntry.burnt) {
                                if(simulate) {
                                    input.shrink(1);
                                    return input;
                                }
                                sprinkle(stack, itemEntry.stack, seasoningItem.getSeasoning());
                                be.inventoryChanged();
                                return stack;
                            }
                        }
                    }

                    //尝试下锅
                    int slot = -1;
                    int emptyCount = 0;
                    for (int i = 0; i < be.size(); i++) {
                        if (be.getStack(i).isEmpty()) {
                            slot = i;
                            emptyCount ++;
                        }
                    }
                    if (slot != -1) {
                        if (simulate) {
                            input.shrink(1);
                            return input;
                        } else if (be.addItem(slot, stack)) {
                            return stack;
                        }
                    }
                    if(emptyCount == 0) {
                        for (GrillBlockEntity.ItemEntry itemEntry : be.entries) {
                            if (itemEntry.canFlip()) {
                                //能翻面则翻面
                                if (!simulate) {
                                    itemEntry.flip(be);
                                } else {
                                    input.shrink(1);
                                }
                                return input;
                            }
                        }
                    }
                }
                return stack;
            }

        }
        return stack;
    }

    public void sprinkle(ItemStack self, ItemStack skewer, BBQSeasoning seasoning) {
        if (level instanceof ServerLevel sl) {
            skewer.getOrCreateTag().putString("seasoning", seasoning.name());
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5 , SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            Integer color = seasoning.color.getColor();
            int col = color == null ? 0 : color;
            sl.sendParticles(new DustParticleOptions(Vec3.fromRGB24(col).toVector3f(), 1.0F), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 8, 0.0, 0.0, 0.0, 1.0);
            self.shrink(1);
        }
    }

    @Override
    public ItemStack extract(int slot, int amount, boolean simulate) {
        if (level.getBlockEntity(pos) instanceof GrillBlockEntity be) {
            for (GrillBlockEntity.ItemEntry itemEntry : be.entries) {
                if (itemEntry.time > itemEntry.duration) {
                    //熟了就拿走
                    if (!simulate) {
                        ItemStack toReturn = itemEntry.stack.copyAndClear();
                        be.inventoryChanged();
                        return toReturn;
                    } else {
                        return itemEntry.stack.copy();
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return level.getBlockEntity(pos) instanceof GrillBlockEntity;
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new GrillArmInteractionPoint(this, level, pos, state);
        }

    }
}
