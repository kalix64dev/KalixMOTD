package com.kalix.motd.utils;

import com.kalix.motd.KalixMOTD;
// PlaceholderAPI import'u - sadece plugin mevcut olduğunda kullanılır
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI entegrasyonu için utility sınıfı
 * 
 * @author Kalix
 */
public class PlaceholderUtils {
    
    private static KalixMOTD plugin;
    
    /**
     * Plugin'i ayarlar
     */
    public static void setPlugin(KalixMOTD pluginInstance) {
        plugin = pluginInstance;
    }
    
    /**
     * Placeholder'ları değiştirir
     */
    public static String replacePlaceholders(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        if (plugin == null || !plugin.isPlaceholderAPIEnabled()) {
            return message;
        }
        
        try {
            // PlaceholderAPI ile değiştir (reflection kullanarak)
            Class<?> placeholderAPIClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Object result = placeholderAPIClass.getMethod("setPlaceholders", org.bukkit.entity.Player.class, String.class)
                .invoke(null, (org.bukkit.entity.Player) null, message);
            return result != null ? result.toString() : message;
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Placeholder hatası: " + e.getMessage());
            }
            return message;
        }
    }
    
    /**
     * Oyuncu için placeholder'ları değiştirir
     */
    public static String replacePlaceholders(Player player, String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        if (plugin == null || !plugin.isPlaceholderAPIEnabled()) {
            return message;
        }
        
        try {
            // PlaceholderAPI ile değiştir (reflection kullanarak)
            Class<?> placeholderAPIClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Object result = placeholderAPIClass.getMethod("setPlaceholders", org.bukkit.entity.Player.class, String.class)
                .invoke(null, player, message);
            return result != null ? result.toString() : message;
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Placeholder hatası (player): " + e.getMessage());
            }
            return message;
        }
    }
    
    /**
     * Vault placeholder'larını değiştirir
     */
    public static String replaceVaultPlaceholders(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        if (plugin == null || !plugin.isVaultEnabled()) {
            return message;
        }
        
        try {
            // Vault placeholder'larını işle
            message = replaceVaultBalance(message);
            message = replaceVaultGroup(message);
            message = replaceVaultPrefix(message);
            message = replaceVaultSuffix(message);
            
            return message;
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Vault placeholder hatası: " + e.getMessage());
            }
            return message;
        }
    }
    
    /**
     * Vault bakiye placeholder'ını değiştirir
     */
    private static String replaceVaultBalance(String message) {
        if (!message.contains("{vault_balance}")) {
            return message;
        }
        
        try {
            // Vault economy servisini al (reflection kullanarak)
            Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
            Object economy = Bukkit.getServicesManager()
                .getRegistration(economyClass)
                .getProvider();
            
            if (economy != null) {
                double balance = (double) economyClass.getMethod("getBalance", String.class).invoke(economy, "SERVER");
                message = message.replace("{vault_balance}", String.format("%.2f", balance));
            }
        } catch (Exception e) {
            message = message.replace("{vault_balance}", "0.00");
        }
        
        return message;
    }
    
    /**
     * Vault grup placeholder'ını değiştirir
     */
    private static String replaceVaultGroup(String message) {
        if (!message.contains("{vault_group}")) {
            return message;
        }
        
        try {
            // Vault permission servisini al (reflection kullanarak)
            Class<?> permissionClass = Class.forName("net.milkbowl.vault.permission.Permission");
            Object permission = Bukkit.getServicesManager()
                .getRegistration(permissionClass)
                .getProvider();
            
            if (permission != null) {
                String group = (String) permissionClass.getMethod("getPrimaryGroup", String.class, String.class).invoke(permission, "world", "SERVER");
                message = message.replace("{vault_group}", group);
            }
        } catch (Exception e) {
            message = message.replace("{vault_group}", "default");
        }
        
        return message;
    }
    
    /**
     * Vault prefix placeholder'ını değiştirir
     */
    private static String replaceVaultPrefix(String message) {
        if (!message.contains("{vault_prefix}")) {
            return message;
        }
        
        try {
            // Vault chat servisini al (reflection kullanarak)
            Class<?> chatClass = Class.forName("net.milkbowl.vault.chat.Chat");
            Object chat = Bukkit.getServicesManager()
                .getRegistration(chatClass)
                .getProvider();
            
            if (chat != null) {
                String prefix = (String) chatClass.getMethod("getPlayerPrefix", String.class, String.class).invoke(chat, "world", "SERVER");
                if (prefix == null) {
                    prefix = "";
                }
                message = message.replace("{vault_prefix}", prefix);
            }
        } catch (Exception e) {
            message = message.replace("{vault_prefix}", "");
        }
        
        return message;
    }
    
    /**
     * Vault suffix placeholder'ını değiştirir
     */
    private static String replaceVaultSuffix(String message) {
        if (!message.contains("{vault_suffix}")) {
            return message;
        }
        
        try {
            // Vault chat servisini al (reflection kullanarak)
            Class<?> chatClass = Class.forName("net.milkbowl.vault.chat.Chat");
            Object chat = Bukkit.getServicesManager()
                .getRegistration(chatClass)
                .getProvider();
            
            if (chat != null) {
                String suffix = (String) chatClass.getMethod("getPlayerSuffix", String.class, String.class).invoke(chat, "world", "SERVER");
                if (suffix == null) {
                    suffix = "";
                }
                message = message.replace("{vault_suffix}", suffix);
            }
        } catch (Exception e) {
            message = message.replace("{vault_suffix}", "");
        }
        
        return message;
    }
    
    /**
     * Tüm placeholder'ları değiştirir
     */
    public static String replaceAllPlaceholders(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        // Önce Vault placeholder'larını işle
        message = replaceVaultPlaceholders(message);
        
        // Sonra PlaceholderAPI placeholder'larını işle
        message = replacePlaceholders(message);
        
        return message;
    }
    
    /**
     * PlaceholderAPI'nin mevcut olup olmadığını kontrol eder
     */
    public static boolean isPlaceholderAPIAvailable() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
    
    /**
     * Vault'un mevcut olup olmadığını kontrol eder
     */
    public static boolean isVaultAvailable() {
        return Bukkit.getPluginManager().getPlugin("Vault") != null;
    }
}