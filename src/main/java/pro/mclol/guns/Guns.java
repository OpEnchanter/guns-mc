package pro.mclol.guns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.util.Vector;

public final class Guns extends JavaPlugin implements Listener, CommandExecutor {

    private ItemStack gun = new ItemStack(Material.IRON_HOE);

    private Double distanceBetweenPoints(Location point1, Location point2){
        Location subLocation = point2.subtract(point1);
        Double distance = Math.sqrt(subLocation.getX()*subLocation.getX() + subLocation.getY()*subLocation.getY() + subLocation.getZ()*subLocation.getZ());
        return distance;
    }

    @Override
    public void onEnable() {

        ItemMeta gunMeta = gun.getItemMeta();
        gunMeta.setDisplayName(ChatColor.WHITE + "Gun");
        List<String> gunLore = new ArrayList<String>();
        gunLore.add("KILL THEM");
        gunLore.add("KILL THEM ALL");
        gunMeta.setLore(gunLore);
        gun.setItemMeta(gunMeta);

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("gun").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) { return true; }
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("gun")){
            player.getInventory().addItem(gun);
        }

        return true;
    }

    @EventHandler
    private void playerClickListener(PlayerInteractEvent event){
        Double distance = 50.00;
        Double damage = 10.00;

        Player player = event.getPlayer();

        if(!player.getInventory().getItemInMainHand().getItemMeta().hasLore()) { return; }

        if (player.getInventory().getItemInMainHand().getItemMeta().getLore().equals(gun.getItemMeta().getLore())){
            event.setCancelled(true);
            Location playerHeadLocation = player.getEyeLocation();
            Vector playerHeadRotation = playerHeadLocation.getDirection();
            RayTraceResult entityTrace = player.getWorld().rayTraceEntities(playerHeadLocation.add(playerHeadRotation), playerHeadRotation, distance);
            RayTraceResult blockTrace = player.getWorld().rayTraceBlocks(playerHeadLocation, playerHeadRotation, distance);

            if (entityTrace == null) { return; }


            Double blockDist = null;
            Double entityDist = distanceBetweenPoints(playerHeadLocation, entityTrace.getHitEntity().getLocation());

            if (blockTrace != null) {
                blockDist = distanceBetweenPoints(playerHeadLocation, blockTrace.getHitBlock().getLocation());
            }

            if (blockTrace == null || entityDist < blockDist){
                LivingEntity hitEntity = (LivingEntity) entityTrace.getHitEntity();
                hitEntity.damage(damage);
            }
        }
    }
}
