package org.hello.spawnpt2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Spawnpt2 extends JavaPlugin implements Listener {

    private int worldRadius;

    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        config.addDefault("worldRadius", 1000);
        config.options().copyDefaults(true);
        saveConfig();
        worldRadius = config.getInt("worldRadius");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setworldspawn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (args.length == 1 && args[0].equalsIgnoreCase("center")) {
                World world = player.getWorld();
                world.setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                sender.sendMessage("World spawn set to your current location.");
            } else {
                sender.sendMessage("Usage: /setworldspawn center");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("setworldradius")) {
            if (args.length == 1) {
                try {
                    int newRadius = Integer.parseInt(args[0]);
                    worldRadius = newRadius;
                    getConfig().set("worldRadius", newRadius);
                    saveConfig();
                    sender.sendMessage("World radius set to " + newRadius);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Invalid radius. Please provide a valid number.");
                }
            } else {
                sender.sendMessage("Usage: /setworldradius <radius>");
            }
            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        // Check if the player has a bed
        if (player.getBedSpawnLocation() != null) {
            return;
        }

        // Get a random location within the specified radius
        int x = player.getLocation().getBlockX() + getRandomOffset();
        int z = player.getLocation().getBlockZ() + getRandomOffset();

        // Make sure the new spawn location is within the world boundaries
        x = Math.max(-worldRadius, Math.min(worldRadius, x));
        z = Math.max(-worldRadius, Math.min(worldRadius, z));

        Location newSpawnLocation = new Location(world, x, world.getHighestBlockYAt(x, z), z);
        event.setRespawnLocation(newSpawnLocation);
    }

    private int getRandomOffset() {
        Random random = new Random();
        return random.nextInt(worldRadius * 2 + 1) - worldRadius;
    }
}
