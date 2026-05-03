package de.dice;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DicePlugin extends JavaPlugin implements CommandExecutor {

    private final Random random = new Random();
    private final Map<Integer, String> textures = new HashMap<>();

    @Override
    public void onEnable() {
        // Die Textur-Strings für die Würfelaugen 1-6
        textures.put(1, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Y1Mzk2Zjg2ZWM1ZGRjYTkxYTM5MDkyMzgxM2Q3NjY2MWVlZDU1YzVkYmI0MGViMjhhZWFjZmU3MzM0NGE2In19fQ==");
        textures.put(2, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI2M2Q1MDY3ODljNWE3ZWVlY2YwZTUyYzFkODMxNmE5YTE5YTM0ZWIzMmFiYjFjYmY1ZDhkMDdmZGMzZDM3OCJ9fX0=");
        textures.put(3, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmVmOTg0ZGUxMzg2Mjg3ZGFjMDRiZTExZmNlY2U3ODdhY2I5NDljZjg3M2U0YTg5MWNmZDI0MWQ2MTM3MGM0In19fQ==");
        textures.put(4, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JlYzk1Y2RiYWRhYTZmOWYxNmY2M2JjYmFkNmVjYmI4YjkzZmVlYWNlYmI1NGYyMTY5MWFkYTZlYmNlIn19fQ==");
        textures.put(5, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTJlNDFlZTU0MmRkZDU1OTFmN2VjNDM5YThmNDc1ZWI4YzU0N2YyNDYwYWU2YmFkY2I4ZWI0N2UxZWFhYzEifX19");
        textures.put(6, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTI2ZDc0NDg3YzUyZTRlZTMzOTg2MTk2ZjA5NDc0YjcxYmZlZTRlYzNkNTBiZTY5YjYyZGRiZDc1YjFhIn19fQ==");

        getCommand("dice").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können würfeln!");
            return true;
        }

        int count = 1;
        if (args.length > 0) {
            try {
                // Maximale Anzahl auf 9 begrenzen
                count = Math.max(1, Math.min(Integer.parseInt(args[0]), 9));
            } catch (NumberFormatException e) {
                player.sendMessage("§cNutze /dice [1-9]");
                return true;
            }
        }

        // Berechnet die Stelle vor dem Spieler
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.5));

        for (int i = 0; i < count; i++) {
            int result = random.nextInt(6) + 1;
            // Würfel leicht versetzt nebeneinander spawnen
            Location diceLoc = loc.clone().add(i * 0.4, -0.5, 0);
            spawnDice(diceLoc, result);
            player.sendMessage("§8[§6Würfel§8] §7Ergebnis: §e" + result);
        }

        return true;
    }

    private void spawnDice(Location loc, int value) {
        ArmorStand dice = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        
        dice.setVisible(false);
        dice.setGravity(false);
        dice.setMarker(true); // Macht den Kopf unbesiegbar und verhindert Hitboxen (kein Abbauen möglich)
        dice.setPersistent(false);
        dice.setInvulnerable(true);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        if (meta != null) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", textures.get(value)));
            meta.setPlayerProfile(profile);
            head.setItemMeta(meta);
        }

        dice.getEquipment().setHelmet(head);

        // Entfernt den Würfel nach 100 Ticks (5 Sekunden)
        Bukkit.getScheduler().runTaskLater(this, dice::remove, 100L);
    }
}
