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
import org.rgantileave.flags.Flags;
import org.rgantileave.listener.OnPlayerMoveListener;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Rgantileave extends JavaPlugin {

    public static Logger logger = Logger.getLogger(Rgantileave.class.getName());

    //TODO Implement this flag further
    static {
        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag flag1 = null;
        StateFlag flag2 = null;

        try {
            flag1 = new StateFlag("special-enter", false);
            flag2 = new StateFlag("special-fly-zone", false);
            flagRegistry.register(flag1);
            flagRegistry.register(flag2);
            logger.log(Level.INFO, "Registered flag: " + flag1.getName());
            logger.log(Level.INFO, "Registered flag: " + flag2.getName());
        } catch (FlagConflictException e) {
            Flag<?> existing = flagRegistry.get("special-enter");
            Flag<?> existing2 = flagRegistry.get("special-fly-zone");
            if (existing instanceof StateFlag && existing2 instanceof StateFlag) {
                flag1 = (StateFlag) existing;
                flag2 = (StateFlag) existing2;
                logger.log(Level.INFO, "Flag 'special-enter' already exists. Reusing existing flag.");
            } else {
                logger.log(Level.SEVERE, "Failed to register flag 'special-enter'.", e);
                throw new RuntimeException("Failed to register flag 'special-enter'.", e);
            }
        }
        Flags.SPECIAL_ENTER_FLAG = flag1;
        Flags.ALLOW_FLY_ZONE = flag2;
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
