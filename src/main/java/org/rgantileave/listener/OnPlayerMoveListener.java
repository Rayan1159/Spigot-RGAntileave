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

    public boolean isPlayerFlying = false;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Map<String, ProtectedRegion> regions = Rgantileave.getAllRegions(getWorldEditWorld(player));
        Location playerLocation = BukkitAdapter.adapt(event.getTo());
        Location previousLocation = BukkitAdapter.adapt(event.getFrom());

        ApplicableRegionSet currentRegions = getApplicableRegions(playerLocation);
        ApplicableRegionSet previousRegions = getApplicableRegions(previousLocation);

        for (ProtectedRegion region : previousRegions) {
            handleLeaveRegion(region, regions, currentRegions, player);
        }
        for (ProtectedRegion region : currentRegions) {
            handleEnterRegion(region, regions, currentRegions, player);
        }
    }

    private void handleEnterRegion(ProtectedRegion enteredRegion, Map<String, ProtectedRegion> regions, ApplicableRegionSet currentRegions, Player player) {
        if (enteredRegion == null) return;
        if (regions.containsKey(enteredRegion.getId()) && this.containsRegion(currentRegions, enteredRegion) && !player.isOp()) {
            StateFlag.State entry = enteredRegion.getFlag(Flags.ENTRY);
            if (entry != null && entry.equals(StateFlag.State.DENY)) {
                StateFlag.State flyZone = enteredRegion.getFlag(org.rgantileave.flags.Flags.ALLOW_FLY_ZONE);
                if (flyZone != null && flyZone.equals(StateFlag.State.ALLOW)) {
                    player.setFlying(!isPlayerFlying);
                } else {
                    player.setFlying(!isPlayerFlying);
                }
            }
        }
    }

    private void handleLeaveRegion(ProtectedRegion previousRegion, Map<String, ProtectedRegion> regions, ApplicableRegionSet currentRegions, Player player) {
        if (previousRegion == null) return;
        if (regions.containsKey(previousRegion.getId()) && !this.containsRegion(currentRegions, previousRegion) && !player.isOp()) {
            StateFlag.State exit = previousRegion.getFlag(Flags.EXIT);
            if (exit != null && exit.equals(StateFlag.State.DENY)) {
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage("You are not allowed to leave this region");
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

    private ApplicableRegionSet getApplicableRegions(Location location) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(location);
    }
}
