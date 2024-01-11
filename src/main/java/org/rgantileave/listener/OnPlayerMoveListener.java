package org.rgantileave.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.rgantileave.Rgantileave;

import java.util.Map;

public class OnPlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Map<String, ProtectedRegion> regions = Rgantileave.getAllRegions(getWorldEditWorld(player));
        Location playerLocation = BukkitAdapter.adapt(event.getTo());

        ApplicableRegionSet currentRegions = getApplicableCurrentRegions(playerLocation);

        Location previousLocation = BukkitAdapter.adapt(event.getFrom());
        ApplicableRegionSet previousRegions = getApplicablePreviousRegions(previousLocation);

        for (ProtectedRegion region : previousRegions) {
            if (regions == null) return;
            if (regions.containsKey(region.getId()) && !this.containsRegion(currentRegions, region) || !player.isOp()) {
                StateFlag.State exit = region.getFlag(Flags.EXIT);
                if (exit != null && exit.equals(StateFlag.State.DENY)) {
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.sendMessage("You are not allowed to leave this region");
                }
            }
        }
    }

    private World getWorldEditWorld(Player player) {
        return BukkitAdapter.adapt(player.getWorld());
    }

    private boolean containsRegion(ApplicableRegionSet regionSet, ProtectedRegion targetRegion) {
        for (ProtectedRegion region : regionSet) {
            if (region.getId().equals(targetRegion.getId())) {
                return true;
            }
        }
        return false;
    }

    private ApplicableRegionSet getApplicableCurrentRegions(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(location);
    }

    private ApplicableRegionSet getApplicablePreviousRegions(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(location);
    }
}
