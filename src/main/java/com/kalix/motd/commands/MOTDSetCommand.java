package com.kalix.motd.commands;

import com.kalix.motd.KalixMOTD;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MOTD ayarlama komut sınıfı
 * 
 * @author Kalix
 */
public class MOTDSetCommand implements CommandExecutor, TabCompleter {
    
    private final KalixMOTD plugin;
    
    public MOTDSetCommand(KalixMOTD plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfigManager().getString("messages.prefix", "§6§l[KalixMOTD] §r");
        
        if (!sender.hasPermission("kalixmotd.set")) {
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.no-permission", 
                "§cBu işlem için yetkiniz yok!"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(prefix + "§cKullanım: /motdset <line1|line2> <message>");
            return true;
        }
        
        String line = args[0].toLowerCase();
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        if (!line.equals("line1") && !line.equals("line2")) {
            sender.sendMessage(prefix + "§cGeçersiz satır! Kullanım: line1 veya line2");
            return true;
        }
        
        if (message.isEmpty()) {
            sender.sendMessage(prefix + "§cMesaj boş olamaz!");
            return true;
        }
        
        try {
            // Config'e kaydet
            plugin.getConfigManager().set("motd.messages." + line, message);
            plugin.getConfigManager().saveConfig();
            
            // MOTD'yi yeniden yükle
            plugin.getMOTDManager().reload();
            
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.set", 
                "§aMOTD başarıyla güncellendi!"));
            sender.sendMessage(prefix + "§7Yeni " + line + ": §f" + message);
            
            // Debug modunda log
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug(sender.getName() + " MOTD " + line + " ayarladı: " + message);
            }
            
        } catch (Exception e) {
            sender.sendMessage(prefix + "§cMOTD güncellenirken hata oluştu: " + e.getMessage());
            plugin.getPluginLogger().error("MOTD set hatası: " + e.getMessage());
            
            if (plugin.getConfigManager().isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // line1/line2 öner
            completions.add("line1");
            completions.add("line2");
        }
        
        return completions;
    }
}