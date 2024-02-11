package de.teamlapen.vampirism.modcompat;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.neoforged.fml.ModList;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IntegrationsNotifier {

    private final static String[] available_compats = new String[]{"waila", "consecration", "bloodmagic", "biomesoplenty", "toughasnails", "evilcraft"};

    /**
     * Check if there should be a notification about the integrations mods.
     * Checks if it is installed, if there are any mods with potential compat installed and if there already was a notification about these mods
     * Only call after pre-init
     *
     * @return Empty list if no notification. Otherwise, list of installed mod ids with potential compat
     */
    public static @NotNull List<String> shouldNotifyAboutIntegrations() {
        if (!ModList.get().isLoaded(REFERENCE.INTEGRATIONS_MODID)) {
            List<String> installedMods = Lists.newArrayList();
            for (String s : available_compats) {
                if (ModList.get().isLoaded(s)) {
                    installedMods.add(s);
                }
            }
            if (!installedMods.isEmpty()) {
                if (!checkAndUpdateAlreadyNotified(installedMods)) {
                    return installedMods;
                }
            }

        }
        return Collections.emptyList();
    }

    /**
     * Check if a notification regarding all the given mods has been displayed already.
     * If not add the given mods to the already notified mod list
     *
     * @return If already notified
     */
    private static boolean checkAndUpdateAlreadyNotified(@NotNull List<String> mods) {
        String saved = VampirismConfig.COMMON.integrationsNotifier.get();
        if ("never".equals(saved) || "'never'".equals(saved)) {
            return true;
        }
        String[] previous = saved.split(":");
        List<String> missing = new ArrayList<>(mods);
        missing.removeAll(Arrays.asList(previous));
        if (missing.isEmpty()) {
            return true;
        }
        Collections.addAll(missing, previous);
        VampirismConfig.COMMON.integrationsNotifier.set(StringUtils.join(missing, ":"));
        return false;
    }
}
