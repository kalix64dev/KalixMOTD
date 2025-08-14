package com.kalix.motd.managers;

import com.kalix.motd.KalixMOTD;
import com.kalix.motd.utils.ColorUtils;
import com.kalix.motd.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MOTD işlemlerini yöneten sınıf
 * 
 * @author Kalix
 */
public class MOTDManager {
    
    private final KalixMOTD plugin;
    private final Random random;
    private BukkitTask randomTask;
    private List<MOTDMessage> randomMessages;
    private MOTDMessage currentMessage;
    private int currentIndex = 0;
    
    public MOTDManager(KalixMOTD plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.randomMessages = new ArrayList<>();
        
        loadMOTDMessages();
        startRandomMOTD();
    }
    
    /**
     * MOTD mesajlarını yükler
     */
    private void loadMOTDMessages() {
        randomMessages.clear();
        
        ConfigurationSection randomSection = plugin.getConfigManager().getConfig().getConfigurationSection("motd.random.messages");
        if (randomSection != null) {
            for (String key : randomSection.getKeys(false)) {
                ConfigurationSection messageSection = randomSection.getConfigurationSection(key);
                if (messageSection != null) {
                    String line1 = messageSection.getString("line1", "");
                    String line2 = messageSection.getString("line2", "");
                    
                    if (!line1.isEmpty() || !line2.isEmpty()) {
                        randomMessages.add(new MOTDMessage(line1, line2));
                    }
                }
            }
        }
        
        // Varsayılan mesajları ekle
        if (randomMessages.isEmpty()) {
            randomMessages.add(new MOTDMessage(
                "§6§lKalixMOTD §8» §fUltra profesyonel MOTD!",
                "§7Desteklenen sürümler: §a1.8-1.21"
            ));
        }
        
        // İlk mesajı ayarla
        if (!randomMessages.isEmpty()) {
            currentMessage = randomMessages.get(0);
        }
    }
    
    /**
     * Rastgele MOTD sistemini başlatır
     */
    private void startRandomMOTD() {
        if (!plugin.getConfigManager().getBoolean("motd.random.enabled", true)) {
            return;
        }
        
        int interval = plugin.getConfigManager().getInt("motd.random.interval", 30);
        
        randomTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (randomMessages.size() > 1) {
                    // Rastgele mesaj seç
                    int newIndex;
                    do {
                        newIndex = random.nextInt(randomMessages.size());
                    } while (newIndex == currentIndex && randomMessages.size() > 1);
                    
                    currentIndex = newIndex;
                    currentMessage = randomMessages.get(currentIndex);
                    
                    if (plugin.getConfigManager().isDebugEnabled()) {
                        plugin.getPluginLogger().debug("MOTD değiştirildi: " + currentMessage.getLine1());
                    }
                }
            }
        }.runTaskTimer(plugin, interval * 20L, interval * 20L);
    }
    
    /**
     * MOTD mesajını alır
     */
    public String getMOTDLine1() {
        String line1 = plugin.getConfigManager().getString("motd.messages.line1", "§6§lKalixMOTD §8» §fHoş geldiniz!");
        
        // Rastgele MOTD aktifse onu kullan
        if (plugin.getConfigManager().getBoolean("motd.random.enabled", true) && currentMessage != null) {
            line1 = currentMessage.getLine1();
        }
        
        return processMOTD(line1);
    }
    
    /**
     * MOTD mesajını alır (ikinci satır)
     */
    public String getMOTDLine2() {
        String line2 = plugin.getConfigManager().getString("motd.messages.line2", "§7Sunucu sürümü: §a1.8-1.21");
        
        // Rastgele MOTD aktifse onu kullan
        if (plugin.getConfigManager().getBoolean("motd.random.enabled", true) && currentMessage != null) {
            line2 = currentMessage.getLine2();
        }
        
        return processMOTD(line2);
    }
    
    /**
     * MOTD mesajını işler
     */
    private String processMOTD(String message) {
        // Placeholder'ları işle
        if (plugin.isPlaceholderAPIEnabled()) {
            message = PlaceholderUtils.replacePlaceholders(message);
        }
        
        // Renk kodlarını işle
        message = ColorUtils.colorize(message);
        
        // Özel placeholder'ları işle
        message = replaceCustomPlaceholders(message);
        
        return message;
    }
    
    /**
     * Özel placeholder'ları değiştirir
     */
    private String replaceCustomPlaceholders(String message) {
        message = message.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        message = message.replace("{max}", String.valueOf(Bukkit.getMaxPlayers()));
        message = message.replace("{server_name}", plugin.getConfigManager().getString("placeholders.custom.server_name", "KalixMOTD"));
        message = message.replace("{server_version}", plugin.getConfigManager().getString("placeholders.custom.server_version", "1.8-1.21"));
        message = message.replace("{plugin_version}", plugin.getDescription().getVersion());
        message = message.replace("{minecraft_version}", plugin.getMinecraftVersion());
        
        return message;
    }
    
    /**
     * Hover mesajlarını alır
     */
    public List<String> getHoverMessages() {
        List<String> hoverMessages = plugin.getConfigManager().getStringList("motd.hover.messages");
        
        if (hoverMessages.isEmpty()) {
            hoverMessages.add("§6§lKalixMOTD §8» §fUltra profesyonel MOTD plugin");
            hoverMessages.add("§7Desteklenen sürümler: §a1.8-1.21");
            hoverMessages.add("§7Özellikler: §aRastgele MOTD, Hover, Click");
            hoverMessages.add("§7Yazar: §aKalix");
        }
        
        // Placeholder'ları işle
        List<String> processedMessages = new ArrayList<>();
        for (String message : hoverMessages) {
            if (plugin.isPlaceholderAPIEnabled()) {
                message = PlaceholderUtils.replacePlaceholders(message);
            }
            message = ColorUtils.colorize(message);
            message = replaceCustomPlaceholders(message);
            processedMessages.add(message);
        }
        
        return processedMessages;
    }
    
    /**
     * Click mesaj tipini alır
     */
    public String getClickType() {
        return plugin.getConfigManager().getString("motd.click.type", "OPEN_URL");
    }
    
    /**
     * Click mesaj değerini alır
     */
    public String getClickValue() {
        return plugin.getConfigManager().getString("motd.click.value", "https://github.com/kalix/KalixMOTD");
    }
    
    /**
     * Oyuncu sayısı formatını alır
     */
    public String getPlayerCountFormat() {
        String format = plugin.getConfigManager().getString("server-list.player-count.format", "§a{online}§7/§c{max}");
        return replaceCustomPlaceholders(format);
    }
    
    /**
     * Sahte oyuncu sayısı kullanılıp kullanılmadığını kontrol eder
     */
    public boolean isFakeOnlineEnabled() {
        return plugin.getConfigManager().getBoolean("server-list.player-count.fake-online", false);
    }
    
    /**
     * Sahte maksimum oyuncu sayısı kullanılıp kullanılmadığını kontrol eder
     */
    public boolean isFakeMaxEnabled() {
        return plugin.getConfigManager().getBoolean("server-list.player-count.fake-max", false);
    }
    
    /**
     * MOTD'yi yeniden yükler
     */
    public void reload() {
        // Mevcut görevi durdur
        if (randomTask != null) {
            randomTask.cancel();
            randomTask = null;
        }
        
        // Mesajları yeniden yükle
        loadMOTDMessages();
        
        // Rastgele MOTD'yi yeniden başlat
        startRandomMOTD();
    }
    
    /**
     * Temizlik işlemleri
     */
    public void cleanup() {
        if (randomTask != null) {
            randomTask.cancel();
            randomTask = null;
        }
    }
    
    /**
     * MOTD mesaj sınıfı
     */
    public static class MOTDMessage {
        private final String line1;
        private final String line2;
        
        public MOTDMessage(String line1, String line2) {
            this.line1 = line1;
            this.line2 = line2;
        }
        
        public String getLine1() {
            return line1;
        }
        
        public String getLine2() {
            return line2;
        }
    }
}