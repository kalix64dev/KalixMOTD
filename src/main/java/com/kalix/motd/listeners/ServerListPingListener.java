package com.kalix.motd.listeners;

import com.kalix.motd.KalixMOTD;
import com.kalix.motd.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.io.File;
import java.util.List;

/**
 * Sunucu listesi ping olaylarını dinleyen sınıf
 * 
 * @author Kalix
 */
public class ServerListPingListener implements Listener {
    
    private final KalixMOTD plugin;
    
    public ServerListPingListener(KalixMOTD plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPing(ServerListPingEvent event) {
        try {
            // MOTD'yi ayarla
            setMOTD(event);
            
            // Oyuncu sayısını ayarla
            setPlayerCount(event);
            
            // Sunucu ikonunu ayarla
            setServerIcon(event);
            
            // Hover ve Click mesajlarını ayarla (1.16+)
            if (VersionUtils.isVersionAtLeast("1.16")) {
                setHoverAndClick(event);
            }
            
            // Protokol ayarlarını yap
            setProtocolSettings(event);
            
            // Sunucu türü ayarlarını yap
            setServerTypeSettings(event);
            
        } catch (Exception e) {
            plugin.getPluginLogger().error("ServerListPing olayında hata: " + e.getMessage());
            if (plugin.getConfigManager().isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * MOTD'yi ayarlar
     */
    private void setMOTD(ServerListPingEvent event) {
        String line1 = plugin.getMOTDManager().getMOTDLine1();
        String line2 = plugin.getMOTDManager().getMOTDLine2();
        
        // MOTD'yi birleştir
        String motd = line1;
        if (!line2.isEmpty()) {
            motd += "\n" + line2;
        }
        
        event.setMotd(motd);
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getPluginLogger().debug("MOTD ayarlandı: " + motd);
        }
    }
    
    /**
     * Oyuncu sayısını ayarlar
     */
    private void setPlayerCount(ServerListPingEvent event) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        
        // Sahte oyuncu sayısı kontrolü
        if (plugin.getMOTDManager().isFakeOnlineEnabled()) {
            // Config'den sahte online sayısını al
            int fakeOnline = plugin.getConfigManager().getInt("server-list.player-count.fake-online-value", onlinePlayers);
            onlinePlayers = fakeOnline;
        }
        
        if (plugin.getMOTDManager().isFakeMaxEnabled()) {
            // Config'den sahte max sayısını al
            int fakeMax = plugin.getConfigManager().getInt("server-list.player-count.fake-max-value", maxPlayers);
            maxPlayers = fakeMax;
        }
        
        // setNumPlayers metodu 1.8'de mevcut değil, reflection kullanarak ayarla
        try {
            event.getClass().getMethod("setNumPlayers", int.class).invoke(event, onlinePlayers);
        } catch (Exception e) {
            // Fallback - oyuncu sayısını değiştiremezsek varsayılan değeri kullan
        }
        event.setMaxPlayers(maxPlayers);
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getPluginLogger().debug("Oyuncu sayısı ayarlandı: " + onlinePlayers + "/" + maxPlayers);
        }
    }
    
    /**
     * Sunucu ikonunu ayarlar
     */
    private void setServerIcon(ServerListPingEvent event) {
        if (!plugin.getConfigManager().getBoolean("server-list.icon.enabled", true)) {
            return;
        }
        
        String iconPath = plugin.getConfigManager().getString("server-list.icon.path", "server-icon.png");
        File iconFile = new File(plugin.getDataFolder(), iconPath);
        
        if (!iconFile.exists()) {
            // Plugin klasöründen varsayılan ikonu kopyala
            plugin.saveResource(iconPath, false);
        }
        
        if (iconFile.exists()) {
            try {
                CachedServerIcon icon = Bukkit.loadServerIcon(iconFile);
                event.setServerIcon(icon);
                
                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getPluginLogger().debug("Sunucu ikonu ayarlandı: " + iconPath);
                }
            } catch (Exception e) {
                plugin.getPluginLogger().warn("Sunucu ikonu yüklenemedi: " + e.getMessage());
            }
        }
    }
    
    /**
     * Hover ve Click mesajlarını ayarlar (1.16+)
     */
    private void setHoverAndClick(ServerListPingEvent event) {
        // Bu özellik sadece 1.16+ sürümlerde mevcut
        if (!VersionUtils.isVersionAtLeast("1.16")) {
            return;
        }
        
        try {
            // Reflection kullanarak hover ve click mesajlarını ayarla
            setHoverAndClickViaReflection(event);
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Hover/Click ayarlanamadı (reflection hatası): " + e.getMessage());
            }
        }
    }
    
    /**
     * Reflection kullanarak hover ve click mesajlarını ayarlar
     */
    private void setHoverAndClickViaReflection(ServerListPingEvent event) throws Exception {
        // Bu kısım 1.16+ sürümlerde JSON MOTD desteği için
        // Şimdilik basit bir implementasyon
        if (plugin.getConfigManager().getBoolean("advanced.json-support", true)) {
            // JSON MOTD desteği burada implement edilebilir
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("JSON MOTD desteği aktif");
            }
        }
    }
    
    /**
     * Sunucu açıklamasını ayarlar
     */
    private void setServerDescription(ServerListPingEvent event) {
        if (!plugin.getConfigManager().getBoolean("server-list.description.enabled", true)) {
            return;
        }
        
        String description = plugin.getConfigManager().getString("server-list.description.format", 
            "§6§lKalixMOTD\n§7Ultra profesyonel MOTD plugin");
        
        // Placeholder'ları işle
        description = description.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
        description = description.replace("{max}", String.valueOf(Bukkit.getMaxPlayers()));
        description = description.replace("{server_name}", plugin.getConfigManager().getString("placeholders.custom.server_name", "KalixMOTD"));
        description = description.replace("{server_version}", plugin.getConfigManager().getString("placeholders.custom.server_version", "1.8-1.21"));
        description = description.replace("{plugin_version}", plugin.getDescription().getVersion());
        description = description.replace("{minecraft_version}", plugin.getMinecraftVersion());
        
        // MOTD'yi güncelle
        String currentMotd = event.getMotd();
        if (!description.isEmpty()) {
            event.setMotd(description);
        }
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getPluginLogger().debug("Sunucu açıklaması ayarlandı: " + description);
        }
    }
    
    /**
     * Protokol ayarlarını yapar
     */
    private void setProtocolSettings(ServerListPingEvent event) {
        if (!plugin.getConfigManager().getBoolean("server-list.protocol.enabled", true)) {
            return;
        }
        
        try {
            // Reflection kullanarak protokol ayarlarını yap
            Class<?> eventClass = event.getClass();
            
            // Protokol versiyonunu ayarla
            if (plugin.getConfigManager().getBoolean("advanced.protocol.custom-protocol", false)) {
                int protocolNumber = plugin.getConfigManager().getInt("advanced.protocol.custom-protocol-number", 47);
                
                // setProtocolVersion metodu varsa kullan
                try {
                    Method setProtocolMethod = eventClass.getMethod("setProtocolVersion", int.class);
                    setProtocolMethod.invoke(event, protocolNumber);
                } catch (NoSuchMethodException e) {
                    // Field ile ayarla
                    try {
                        Field protocolField = eventClass.getDeclaredField("protocolVersion");
                        protocolField.setAccessible(true);
                        protocolField.set(event, protocolNumber);
                    } catch (Exception ex) {
                        if (plugin.getConfigManager().isDebugEnabled()) {
                            plugin.getPluginLogger().debug("Protokol versiyonu ayarlanamadı: " + ex.getMessage());
                        }
                    }
                }
            }
            
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Protokol ayarları yapıldı");
            }
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Protokol ayarları yapılırken hata: " + e.getMessage());
            }
        }
    }
    
    /**
     * Sunucu türü ayarlarını yapar
     */
    private void setServerTypeSettings(ServerListPingEvent event) {
        if (!plugin.getConfigManager().getBoolean("server-list.server-type.enabled", true)) {
            return;
        }
        
        try {
            // Reflection kullanarak sunucu türü ayarlarını yap
            Class<?> eventClass = event.getClass();
            
            // setServerDescription metodu varsa kullan (1.16+)
            if (VersionUtils.isVersionAtLeast("1.16")) {
                try {
                    Method setDescriptionMethod = eventClass.getMethod("setServerDescription", String.class);
                    String description = plugin.getMOTDManager().getServerTypeDescription();
                    setDescriptionMethod.invoke(event, description);
                } catch (NoSuchMethodException e) {
                    // Fallback - field ile ayarla
                    try {
                        Field descriptionField = eventClass.getDeclaredField("description");
                        descriptionField.setAccessible(true);
                        String description = plugin.getMOTDManager().getServerTypeDescription();
                        descriptionField.set(event, description);
                    } catch (Exception ex) {
                        if (plugin.getConfigManager().isDebugEnabled()) {
                            plugin.getPluginLogger().debug("Sunucu türü açıklaması ayarlanamadı: " + ex.getMessage());
                        }
                    }
                }
            }
            
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Sunucu türü ayarları yapıldı");
            }
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getPluginLogger().debug("Sunucu türü ayarları yapılırken hata: " + e.getMessage());
            }
        }
    }
}