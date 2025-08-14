package com.kalix.motd.commands;

import com.kalix.motd.KalixMOTD;
import com.kalix.motd.utils.ProxyUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ana MOTD komut sınıfı
 * 
 * @author Kalix
 */
public class MOTDCommand implements CommandExecutor, TabCompleter {
    
    private final KalixMOTD plugin;
    private final List<String> subCommands = Arrays.asList("reload", "set", "list", "info", "help");
    
    public MOTDCommand(KalixMOTD plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfigManager().getString("messages.prefix", "§6§l[KalixMOTD] §r");
        
        if (args.length == 0) {
            // Ana komut - yardım göster
            showHelp(sender, prefix);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                return handleReload(sender, prefix);
            case "set":
                return handleSet(sender, args, prefix);
            case "list":
                return handleList(sender, prefix);
            case "info":
                return handleInfo(sender, prefix);
            case "help":
                showHelp(sender, prefix);
                return true;
            default:
                sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.invalid-command", 
                    "§cGeçersiz komut! Kullanım: §f{usage}").replace("{usage}", command.getUsage()));
                return true;
        }
    }
    
    /**
     * Reload komutunu işler
     */
    private boolean handleReload(CommandSender sender, String prefix) {
        if (!sender.hasPermission("kalixmotd.reload")) {
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.no-permission", 
                "§cBu işlem için yetkiniz yok!"));
            return true;
        }
        
        try {
            plugin.reload();
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.reload", 
                "§aPlugin başarıyla yeniden yüklendi!"));
        } catch (Exception e) {
            sender.sendMessage(prefix + "§cPlugin yeniden yüklenirken hata oluştu: " + e.getMessage());
            plugin.getPluginLogger().error("Reload hatası: " + e.getMessage());
        }
        
        return true;
    }
    
    /**
     * Set komutunu işler
     */
    private boolean handleSet(CommandSender sender, String[] args, String prefix) {
        if (!sender.hasPermission("kalixmotd.set")) {
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.no-permission", 
                "§cBu işlem için yetkiniz yok!"));
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(prefix + "§cKullanım: /motd set <line1|line2> <message>");
            return true;
        }
        
        String line = args[1].toLowerCase();
        String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        
        if (!line.equals("line1") && !line.equals("line2")) {
            sender.sendMessage(prefix + "§cGeçersiz satır! Kullanım: line1 veya line2");
            return true;
        }
        
        try {
            plugin.getConfigManager().set("motd.messages." + line, message);
            plugin.getConfigManager().saveConfig();
            
            // MOTD'yi yeniden yükle
            plugin.getMOTDManager().reload();
            
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.set", 
                "§aMOTD başarıyla güncellendi!"));
            sender.sendMessage(prefix + "§7Yeni " + line + ": §f" + message);
        } catch (Exception e) {
            sender.sendMessage(prefix + "§cMOTD güncellenirken hata oluştu: " + e.getMessage());
            plugin.getPluginLogger().error("MOTD set hatası: " + e.getMessage());
        }
        
        return true;
    }
    
    /**
     * List komutunu işler
     */
    private boolean handleList(CommandSender sender, String prefix) {
        if (!sender.hasPermission("kalixmotd.admin")) {
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.no-permission", 
                "§cBu işlem için yetkiniz yok!"));
            return true;
        }
        
        List<String> listMessages = plugin.getConfigManager().getStringList("messages.list");
        if (listMessages.isEmpty()) {
            listMessages.add("§6§lMOTD Ayarları:");
            listMessages.add("§7Line 1: §f{line1}");
            listMessages.add("§7Line 2: §f{line2}");
            listMessages.add("§7Rastgele MOTD: §f{random}");
            listMessages.add("§7Hover: §f{hover}");
            listMessages.add("§7Click: §f{click}");
            listMessages.add("§7Protokol: §f{protocol}");
            listMessages.add("§7Oyuncu Sayısı: §f{player_count}");
            listMessages.add("§7Sunucu İkonu: §f{icon}");
        }
        
        for (String message : listMessages) {
            message = message.replace("{line1}", plugin.getConfigManager().getString("motd.messages.line1", ""));
            message = message.replace("{line2}", plugin.getConfigManager().getString("motd.messages.line2", ""));
            message = message.replace("{random}", plugin.getConfigManager().getBoolean("motd.random.enabled", true) ? "§aAçık" : "§cKapalı");
            message = message.replace("{hover}", plugin.getConfigManager().getBoolean("motd.hover.enabled", true) ? "§aAçık" : "§cKapalı");
            message = message.replace("{click}", plugin.getConfigManager().getBoolean("motd.click.enabled", true) ? "§aAçık" : "§cKapalı");
            message = message.replace("{protocol}", plugin.getConfigManager().getBoolean("server-list.protocol.enabled", true) ? "§aAçık" : "§cKapalı");
            message = message.replace("{player_count}", plugin.getConfigManager().getBoolean("server-list.player-count.enabled", true) ? "§aAçık" : "§cKapalı");
            message = message.replace("{icon}", plugin.getConfigManager().getBoolean("server-list.icon.enabled", true) ? "§aAçık" : "§cKapalı");
            
            sender.sendMessage(prefix + message);
        }
        
        return true;
    }
    
    /**
     * Info komutunu işler
     */
    private boolean handleInfo(CommandSender sender, String prefix) {
        if (!sender.hasPermission("kalixmotd.info")) {
            sender.sendMessage(prefix + plugin.getConfigManager().getString("messages.no-permission", 
                "§cBu işlem için yetkiniz yok!"));
            return true;
        }
        
        List<String> infoMessages = plugin.getConfigManager().getStringList("messages.info");
        if (infoMessages.isEmpty()) {
            infoMessages.add("§6§lKalixMOTD Bilgileri");
            infoMessages.add("§7Sürüm: §a{version}");
            infoMessages.add("§7Yazar: §aKalix");
            infoMessages.add("§7Desteklenen sürümler: §a1.8-1.21");
            infoMessages.add("§7API Sürümü: §a1.13");
            infoMessages.add("§7Proxy Desteği: §a{proxy}");
            infoMessages.add("§7Folia Desteği: §a{folia}");
            infoMessages.add("§7PlaceholderAPI: §a{papi}");
            infoMessages.add("§7Vault: §a{vault}");
        }
        
        for (String message : infoMessages) {
            message = message.replace("{version}", plugin.getDescription().getVersion());
            message = message.replace("{server_type}", ProxyUtils.getServerType());
            message = message.replace("{proxy}", (plugin.isBungeeCordEnabled() || plugin.isVelocityEnabled()) ? "§aAktif" : "§cPasif");
            message = message.replace("{folia}", plugin.isFoliaEnabled() ? "§aAktif (PaperMC fork)" : "§cPasif");
            message = message.replace("{papi}", plugin.isPlaceholderAPIEnabled() ? "§aAktif" : "§cPasif");
            message = message.replace("{vault}", plugin.isVaultEnabled() ? "§aAktif" : "§cPasif");
            sender.sendMessage(prefix + message);
        }
        
        return true;
    }
    
    /**
     * Yardım mesajını gösterir
     */
    private void showHelp(CommandSender sender, String prefix) {
        sender.sendMessage(prefix + "§6§lKalixMOTD Komutları:");
        sender.sendMessage(prefix + "§7/motd reload §8- §fPlugin'i yeniden yükle");
        sender.sendMessage(prefix + "§7/motd set <line1|line2> <message> §8- §fMOTD ayarla");
        sender.sendMessage(prefix + "§7/motd list §8- §fMOTD ayarlarını listele");
        sender.sendMessage(prefix + "§7/motd info §8- §fPlugin bilgilerini göster");
        sender.sendMessage(prefix + "§7/motd help §8- §fBu yardım mesajını göster");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Alt komutları öner
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            // Set komutu için line1/line2 öner
            completions.add("line1");
            completions.add("line2");
        }
        
        return completions;
    }
}