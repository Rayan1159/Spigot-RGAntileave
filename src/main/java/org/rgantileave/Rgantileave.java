package org.rgantileave;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.plugin.java.JavaPlugin;
import org.rgantileave.listener.OnPlayerMoveListener;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Rgantileave extends JavaPlugin {

    public static Logger logger = Logger.getLogger(Rgantileave.class.getName());

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
