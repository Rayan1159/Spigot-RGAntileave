package org.rgantileave;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.plugin.java.JavaPlugin;
import org.rgantileave.listener.OnPlayerMoveListener;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Rgantileave extends JavaPlugin {

    public static Logger logger = Logger.getLogger(Rgantileave.class.getName());
    public static final StateFlag SPECIAL_ENTER_FLAG;

    static {
        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag flag = null;

        try {
            flag = new StateFlag("special-enter", false);
            flagRegistry.register(flag);
        } catch (FlagConflictException e) {
            Flag<?> existing = flagRegistry.get("special-enter");
            if (existing instanceof StateFlag) {
                flag = (StateFlag) existing;
            } else {
                throw e;
            }
        }
        SPECIAL_ENTER_FLAG = flag;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new OnPlayerMoveListener(), this);
        logger.setLevel(Level.FINE);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Map<String, ProtectedRegion> getAllRegions(World world) {
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
        if (regionManager == null) {
            logger.log(Level.WARNING, "RegionManager is null");
            return null;
        }
        return regionManager.getRegions();
    }
}
