package com.kalix.motd;

import com.kalix.motd.commands.MOTDCommand;
import com.kalix.motd.commands.MOTDSetCommand;
import com.kalix.motd.listeners.ServerListPingListener;
import com.kalix.motd.managers.ConfigManager;
import com.kalix.motd.managers.MOTDManager;
import com.kalix.motd.utils.Logger;
import com.kalix.motd.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * KalixMOTD - Ultra profesyonel MOTD plugin for Minecraft 1.8-1.21
 * 
 * @author Kalix
 * @version 1.0.0
 */
public class KalixMOTD extends JavaPlugin {
    
    private static KalixMOTD instance;
    private ConfigManager configManager;
    private MOTDManager motdManager;
    private Logger logger;
    private boolean placeholderAPI = false;
    private boolean vault = false;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Logger başlatma
        logger = new Logger(this);
        logger.info("KalixMOTD başlatılıyor...");
        
        // Config yöneticisi başlatma
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // MOTD yöneticisi başlatma
        motdManager = new MOTDManager(this);
        
        // Plugin bağımlılıklarını kontrol et
        checkDependencies();
        
        // Komutları kaydet
        registerCommands();
        
        // Event listener'ları kaydet
        registerListeners();
        
        // Sürüm bilgilerini logla
        logVersionInfo();
        
        logger.info("KalixMOTD başarıyla etkinleştirildi!");
        logger.info("Desteklenen sürümler: 1.8 - 1.21");
    }
    
    @Override
    public void onDisable() {
        logger.info("KalixMOTD devre dışı bırakılıyor...");
        
        // MOTD yöneticisini temizle
        if (motdManager != null) {
            motdManager.cleanup();
        }
        
        logger.info("KalixMOTD başarıyla devre dışı bırakıldı!");
    }
    
    /**
     * Plugin bağımlılıklarını kontrol eder
     */
    private void checkDependencies() {
        // PlaceholderAPI kontrolü
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderAPI = true;
            logger.info("PlaceholderAPI bulundu ve etkinleştirildi!");
        } else {
            logger.warn("PlaceholderAPI bulunamadı! Placeholder desteği devre dışı.");
        }
        
        // Vault kontrolü
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            vault = true;
            logger.info("Vault bulundu ve etkinleştirildi!");
        } else {
            logger.warn("Vault bulunamadı! Vault desteği devre dışı.");
        }
    }
    
    /**
     * Komutları kaydeder
     */
    private void registerCommands() {
        getCommand("motd").setExecutor(new MOTDCommand(this));
        getCommand("motdset").setExecutor(new MOTDSetCommand(this));
    }
    
    /**
     * Event listener'ları kaydeder
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ServerListPingListener(this), this);
    }
    
    /**
     * Sürüm bilgilerini loglar
     */
    private void logVersionInfo() {
        String serverVersion = Bukkit.getVersion();
        String bukkitVersion = Bukkit.getBukkitVersion();
        String javaVersion = System.getProperty("java.version");
        
        logger.info("=== Sürüm Bilgileri ===");
        logger.info("Server Version: " + serverVersion);
        logger.info("Bukkit Version: " + bukkitVersion);
        logger.info("Java Version: " + javaVersion);
        logger.info("Plugin Version: " + getDescription().getVersion());
        logger.info("API Version: 1.13");
        logger.info("========================");
    }
    
    /**
     * Plugin instance'ını döndürür
     * 
     * @return Plugin instance
     */
    public static KalixMOTD getInstance() {
        return instance;
    }
    
    /**
     * Config yöneticisini döndürür
     * 
     * @return Config yöneticisi
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * MOTD yöneticisini döndürür
     * 
     * @return MOTD yöneticisi
     */
    public MOTDManager getMOTDManager() {
        return motdManager;
    }
    
    /**
     * Logger'ı döndürür
     * 
     * @return Logger
     */
    public Logger getPluginLogger() {
        return logger;
    }
    
    /**
     * PlaceholderAPI'nin mevcut olup olmadığını kontrol eder
     * 
     * @return PlaceholderAPI mevcut mu
     */
    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPI;
    }
    
    /**
     * Vault'un mevcut olup olmadığını kontrol eder
     * 
     * @return Vault mevcut mu
     */
    public boolean isVaultEnabled() {
        return vault;
    }
    
    /**
     * Mevcut Minecraft sürümünü döndürür
     * 
     * @return Minecraft sürümü
     */
    public String getMinecraftVersion() {
        return VersionUtils.getMinecraftVersion();
    }
    
    /**
     * Plugin'in yeniden yüklenmesi
     */
    public void reload() {
        logger.info("Plugin yeniden yükleniyor...");
        
        // Config'i yeniden yükle
        configManager.reloadConfig();
        
        // MOTD yöneticisini yeniden başlat
        motdManager.reload();
        
        logger.info("Plugin başarıyla yeniden yüklendi!");
    }
}