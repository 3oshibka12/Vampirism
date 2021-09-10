package de.teamlapen.vampirism.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import javax.annotation.Nonnull;

public class TotemBaseBlock extends VampirismBlock {
    private static final VoxelShape shape = makeShape();
    private final static String regName = "totem_base";

    private static VoxelShape makeShape() {
        VoxelShape a = Block.box(1, 0, 1, 15, 1, 15);
        VoxelShape b = Block.box(2, 1, 2, 14, 2, 14);
        VoxelShape c = Block.box(3, 2, 3, 13, 3, 13);

        VoxelShape d1 = Block.box(4, 3, 4, 7, 16, 7);
        VoxelShape d2 = Block.box(9, 3, 4, 12, 16, 7);
        VoxelShape d3 = Block.box(4, 3, 9, 7, 16, 12);
        VoxelShape d4 = Block.box(9, 3, 9, 12, 16, 12);

        VoxelShape e = Block.box(5, 3, 5, 11, 16, 11);

        return Shapes.or(a, b, c, d1, d2, d3, d4, e);
    }

    public TotemBaseBlock() {
        super(regName, Properties.of(Material.STONE).strength(40, 2000).sound(SoundType.STONE).noOcclusion());

    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return shape;
    }


    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockPos up = pos.above();
        BlockState upState = world.getBlockState(pos.above());
        if (upState.getBlock() instanceof TotemTopBlock) {
            BlockEntity upTE = world.getBlockEntity(pos.above());
            if (!upState.getBlock().removedByPlayer(upState, world, pos.above(), player, willHarvest, fluid)) {
                return false;
            }
            if (willHarvest) {
                Block.dropResources(upState, world, up, upTE);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
