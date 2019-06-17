package de.teamlapen.vampirism.biome.features;

import de.teamlapen.vampirism.biome.VampirismBiome;
import de.teamlapen.vampirism.biome.config.VampireDungeonConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.tileentity.TileAltarInspiration;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import de.teamlapen.vampirism.world.VampirismWorldData;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;


public class FeatureVampireDungeon extends Feature<VampireDungeonConfig> {

    private static final Logger LOGGER = LogManager.getLogger(FeatureVampireDungeon.class);

    @Override
    public boolean place(IWorld worldIn, IChunkGenerator generator, Random rand, BlockPos position, VampireDungeonConfig config) {
        int sizeX = rand.nextInt(2) + 2;
        int lx = -sizeX - 1;//Lowest x offset
        int hx = sizeX + 1; //Highest x offset
        int sizeZ = rand.nextInt(2) + 2;
        int lz = -sizeZ - 1;//Lowest z offset
        int hz = sizeZ + 1;//Highest z offset
        int airSpaces = 0;

        //Check if ceiling and floor is solid
        //Count two high air spaces at the border of the area
        for (int ax = lx; ax <= hx; ++ax) {
            for (int ay = -1; ay <= 4; ++ay) {
                for (int az = lz; az <= hz; ++az) {
                    BlockPos blockpos = position.add(ax, ay, az);
                    Material material = worldIn.getBlockState(blockpos).getMaterial();
                    boolean flag = material.isSolid();

                    if (ay == -1 && !flag) {
                        return false;
                    }

                    if (ay == 4 && !flag) {
                        return false;
                    }

                    if ((ax == lx || ax == hx || az == lz || az == hz) && ay == 0 && worldIn.isAirBlock(blockpos) && worldIn.isAirBlock(blockpos.up())) {
                        ++airSpaces;
                    }
                }
            }
        }

        if (airSpaces >= 1 && airSpaces <= 5) {
            for (int ax = lx; ax <= hx; ++ax) {
                for (int ay = 4; ay >= -1; --ay) {
                    for (int az = lz; az <= hz; ++az) {
                        BlockPos blockpos1 = position.add(ax, ay, az);

                        if (ax != lx && ay != -1 && az != lz && ax != hx && ay != 4 && az != hz) {
                            if (worldIn.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                                worldIn.removeBlock(blockpos1);
                            }
                        } else if (blockpos1.getY() >= 0 && !worldIn.getBlockState(blockpos1.down()).getMaterial().isSolid()) {
                            worldIn.removeBlock(blockpos1);
                        } else if (worldIn.getBlockState(blockpos1).getMaterial().isSolid() && worldIn.getBlockState(blockpos1).getBlock() != Blocks.CHEST) {
                            if (ay == -1 || ay == 4) {
                                if (rand.nextInt(40) == 0) {
                                    worldIn.setBlockState(blockpos1, ModBlocks.castle_block_dark_brick_bloody.getDefaultState(), 2);
                                } else {
                                    worldIn.setBlockState(blockpos1, ModBlocks.castle_block_dark_brick.getDefaultState(), 2);
                                }
                            } else {
                                worldIn.setBlockState(blockpos1, Blocks.SPRUCE_PLANKS.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 3; ++j) {
                    int l4 = position.getX() + rand.nextInt(sizeX * 2 + 1) - sizeX;
                    int i5 = position.getY();
                    int j5 = position.getZ() + rand.nextInt(sizeZ * 2 + 1) - sizeZ;
                    BlockPos blockpos2 = new BlockPos(l4, i5, j5);

                    if (worldIn.isAirBlock(blockpos2)) {
                        int solidSides = 0;

                        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                            if (worldIn.getBlockState(blockpos2.offset(enumfacing)).getMaterial().isSolid()) {
                                ++solidSides;
                            }
                        }

                        if (solidSides == 1) {
                            worldIn.setBlockState(blockpos2, Blocks.CHEST.correctFacing(worldIn, blockpos2, Blocks.CHEST.getDefaultState()), 2);
                            TileEntity tileentity1 = worldIn.getTileEntity(blockpos2);

                            if (tileentity1 instanceof TileEntityChest) {
                                ((TileEntityChest) tileentity1).setLootTable(LootHandler.STRUCTURE_VAMPIRE_DUNGEON, rand.nextLong());
                            } else {
                                LOGGER.warn("Failed to generate dungeon chest at (%s)", VampirismBiome.debug ? blockpos2 : "hidden");
                            }

                            break;
                        }
                    }
                }
                for (int j = 0; j < 3; ++j) {
                    int l4 = position.getX() + rand.nextInt(sizeX * 2 + 1) - sizeX;
                    int i5 = position.getY();
                    int j5 = position.getZ() + rand.nextInt(sizeZ * 2 + 1) - sizeZ;
                    BlockPos blockpos2 = new BlockPos(l4, i5, j5);

                    if (worldIn.isAirBlock(blockpos2)) {
                        int solidSides = 0;

                        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                            if (worldIn.getBlockState(blockpos2.offset(enumfacing)).getMaterial().isSolid()) {
                                ++solidSides;
                            }
                        }

                        if (solidSides == 2) {
                            worldIn.setBlockState(blockpos2, ModBlocks.blood_container.getDefaultState(), 2);

                            TileEntity tileentity1 = worldIn.getTileEntity(blockpos2);

                            if (tileentity1 instanceof TileBloodContainer) {
                                ((TileBloodContainer) tileentity1).setFluidStack(new FluidStack(ModFluids.blood, BloodBottleFluidHandler.getAdjustedAmount((int) (TileBloodContainer.CAPACITY * rand.nextFloat()))));
                            } else {
                                LOGGER.warn("Failed to generate blood container in dungeon at (%s)", VampirismBiome.debug ? blockpos2 : "hidden");
                            }

                            break;
                        }
                    }
                }
                for (int j = 0; j < 7; ++j) {
                    int l4 = position.getX() + rand.nextInt(sizeX * 2 + 1) - sizeX;
                    int i5 = position.getY();
                    int j5 = position.getZ() + rand.nextInt(sizeZ * 2 + 1) - sizeZ;
                    BlockPos blockpos2 = new BlockPos(l4, i5, j5);

                    if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, worldIn, blockpos2)) {
                        EntityAdvancedVampire vampire = new EntityAdvancedVampire(worldIn.getWorld());
                        vampire.setPosition(l4, i5 + 0.3, j5);
                        if (vampire.getCanSpawnHere()) {
                            worldIn.spawnEntity(vampire);
                            break;
                        } else {
                            vampire.remove();
                        }
                    }
                }
            }
            worldIn.setBlockState(position, ModBlocks.altar_inspiration.getDefaultState(), 2);
            TileEntity tileentity = worldIn.getTileEntity(position);

            if (tileentity instanceof TileAltarInspiration && tileentity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                tileentity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).fill(new FluidStack(ModFluids.blood, (int) (TileAltarInspiration.CAPACITY * rand.nextFloat())), true);
                VampirismWorldData.get(worldIn.getWorld()).addNewVampireDungeon(position);
            } else {
                LOGGER.warn("Failed to generate altar of inspiration in dungeon at (%s)", VampirismBiome.debug ? position : "hidden");
            }

            if (VampirismBiome.debug) LOGGER.info("Generated vampire dungeon at %s", position);
            return true;
        } else {
            return false;
        }
    }
}