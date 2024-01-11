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

        ApplicableRegionSet currentRegions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(playerLocation);

        Location previousLocation = BukkitAdapter.adapt(event.getFrom());
        ApplicableRegionSet previousRegions = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(previousLocation);
        for (ProtectedRegion region : previousRegions) {
            if (regions.containsKey(region.getId()) && !this.containsRegion(currentRegions, region)) {
                StateFlag.State exit = region.getFlag(Flags.EXIT);
                if (exit != null && exit.equals(StateFlag.State.DENY)) {
                    player.teleport(player.getWorld().getSpawnLocation());
                    player.sendMessage("You are not allowed to leave this region");
                }
            }
        }
    }

    public World getWorldEditWorld(Player player) {
        return BukkitAdapter.adapt(player.getWorld());
    }

    public boolean containsRegion(ApplicableRegionSet regionSet, ProtectedRegion targetRegion) {
        for (ProtectedRegion region : regionSet) {
            if (region.getId().equals(targetRegion.getId())) {
                return true;
            }
        }
        return false;
    }
}
