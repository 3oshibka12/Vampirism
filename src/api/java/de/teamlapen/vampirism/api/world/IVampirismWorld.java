package de.teamlapen.vampirism.api.world;


import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IVampirismWorld extends IGarlicChunkHandler {

    /**
     * Clear any caches upon world unload.
     */
    void clearCaches();

    /**
     * checks if the position is in a vampire village
     *
     * @param blockPos pos to check
     * @return true if in a vampire controlled village otherwise false
     */
    boolean isInsideArtificialVampireFogArea(BlockPos blockPos);

    /**
     * adds/updates/removes the bounding box of a fog generating block
     *
     * @param sourcePos position of the fog generating block
     * @param area      new bounding box of the fog protected area or null if the area should be removed
     */
    void updateArtificialFogBoundingBox(@Nonnull BlockPos sourcePos, @Nullable AABB area);

    /**
     * adds/updates/removes the bounding box of a temporary event
     *
     * @param sourcePos position of the fog generating event
     * @param area      new bounding box of the fog protected area or null if the area should be removed
     */
    void updateTemporaryArtificialFog(@Nonnull BlockPos sourcePos, @Nullable AABB area);
}
