package com.kalix.motd.managers;

import com.kalix.motd.KalixMOTD;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Config dosyasını yöneten sınıf
 * 
 * @author Kalix
 */
public class ConfigManager {
    
    private final KalixMOTD plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigManager(KalixMOTD plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Config dosyasını yükler
     */
    public void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Config'i güncelle
        updateConfig();
    }
    
    /**
     * Config dosyasını yeniden yükler
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        updateConfig();
    }
    
    /**
     * Config dosyasını günceller
     */
    private void updateConfig() {
        // Config versiyonunu kontrol et
        int currentVersion = getInt("config-version", 1);
        
        if (currentVersion < 2) {
            // Yeni ayarları ekle
            setDefault("settings.debug", false);
            setDefault("settings.check-updates", true);
            setDefault("settings.placeholderapi-support", true);
            setDefault("settings.vault-support", true);
            
            setDefault("motd.messages.line1", "§6§lKalixMOTD §8» §fHoş geldiniz!");
            setDefault("motd.messages.line2", "§7Sunucu sürümü: §a1.8-1.21");
            
            setDefault("motd.random.enabled", true);
            setDefault("motd.random.interval", 30);
            
            setDefault("motd.hover.enabled", true);
            setDefault("motd.click.enabled", true);
            setDefault("motd.click.type", "OPEN_URL");
            setDefault("motd.click.value", "https://github.com/kalix/KalixMOTD");
            
            setDefault("server-list.player-count.enabled", true);
            setDefault("server-list.player-count.format", "§a{online}§7/§c{max}");
            setDefault("server-list.player-count.fake-online", false);
            setDefault("server-list.player-count.fake-max", false);
            
            setDefault("messages.prefix", "§6§l[KalixMOTD] §r");
            setDefault("messages.reload", "§aPlugin başarıyla yeniden yüklendi!");
            setDefault("messages.set", "§aMOTD başarıyla güncellendi!");
            setDefault("messages.no-permission", "§cBu işlem için yetkiniz yok!");
            setDefault("messages.invalid-command", "§cGeçersiz komut! Kullanım: §f{usage}");
            
            setDefault("performance.cache.enabled", true);
            setDefault("performance.cache.duration", 300);
            setDefault("performance.async.enabled", true);
            setDefault("performance.async.thread-pool-size", 2);
            
            setDefault("advanced.json-support", true);
            setDefault("advanced.gradient-support", true);
            setDefault("advanced.hex-support", true);
            setDefault("advanced.minimessage-support", false);
            setDefault("advanced.legacy-support", true);
            
            setDefault("logging.enabled", true);
            setDefault("logging.level", "INFO");
            setDefault("logging.file", "logs/kalixmotd.log");
            setDefault("logging.max-size", "10MB");
            setDefault("logging.max-files", 5);
            
            // Config versiyonunu güncelle
            set("config-version", 2);
        }
        
        saveConfig();
    }
    
    /**
     * Varsayılan değer ayarlar
     */
    private void setDefault(String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
    }
    
    /**
     * Config dosyasını kaydeder
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getPluginLogger().error("Config dosyası kaydedilemedi: " + e.getMessage());
        }
    }
    
    /**
     * String değer alır
     */
    public String getString(String path) {
        return config.getString(path, "");
    }
    
    /**
     * String değer alır (varsayılan değerle)
     */
    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }
    
    /**
     * Integer değer alır
     */
    public int getInt(String path) {
        return config.getInt(path, 0);
    }
    
    /**
     * Integer değer alır (varsayılan değerle)
     */
    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }
    
    /**
     * Boolean değer alır
     */
    public boolean getBoolean(String path) {
        return config.getBoolean(path, false);
    }
    
    /**
     * Boolean değer alır (varsayılan değerle)
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }
    
    /**
     * List değer alır
     */
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }
    
    /**
     * Değer ayarlar
     */
    public void set(String path, Object value) {
        config.set(path, value);
    }
    
    /**
     * Config dosyasını döndürür
     */
    public FileConfiguration getConfig() {
        return config;
    }
    
    /**
     * Debug modunun açık olup olmadığını kontrol eder
     */
    public boolean isDebugEnabled() {
        return getBoolean("settings.debug", false);
    }
    
    /**
     * Güncelleme kontrolünün açık olup olmadığını kontrol eder
     */
    public boolean isUpdateCheckEnabled() {
        return getBoolean("settings.check-updates", true);
    }
    
    /**
     * PlaceholderAPI desteğinin açık olup olmadığını kontrol eder
     */
    public boolean isPlaceholderAPISupportEnabled() {
        return getBoolean("settings.placeholderapi-support", true);
    }
    
    /**
     * Vault desteğinin açık olup olmadığını kontrol eder
     */
    public boolean isVaultSupportEnabled() {
        return getBoolean("settings.vault-support", true);
    }
}