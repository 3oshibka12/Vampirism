package de.teamlapen.lib.lib.util;


import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

/**
 * Simple interface which provides ClientSetup, CommonSetup and LoadComplete.
 */
public interface IInitListener {
    default void onGatherData(GatherDataEvent event) {

    }

    default void onInitStep(Step step, ParallelDispatchEvent event) {
    }

    enum Step {
        CLIENT_SETUP, COMMON_SETUP, LOAD_COMPLETE, PROCESS_IMC, ENQUEUE_IMC
    }
}
