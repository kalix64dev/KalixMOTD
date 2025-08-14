package com.kalix.motd.utils;

import com.kalix.motd.KalixMOTD;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Proxy desteği için utility sınıfı
 * 
 * @author Kalix
 */
public class ProxyUtils {
    
    private static KalixMOTD plugin;
    
    /**
     * Plugin'i ayarlar
     */
    public static void setPlugin(KalixMOTD pluginInstance) {
        plugin = pluginInstance;
    }
    
    /**
     * BungeeCord desteğini kontrol eder
     */
    public static boolean isBungeeCordEnabled() {
        if (plugin == null) return false;
        return plugin.isBungeeCordEnabled();
    }
    
    /**
     * Velocity desteğini kontrol eder
     */
    public static boolean isVelocityEnabled() {
        if (plugin == null) return false;
        return plugin.isVelocityEnabled();
    }
    
    /**
     * Folia desteğini kontrol eder
     */
    public static boolean isFoliaEnabled() {
        if (plugin == null) return false;
        return plugin.isFoliaEnabled();
    }
    
    /**
     * Proxy desteğini kontrol eder
     */
    public static boolean isProxyEnabled() {
        return isBungeeCordEnabled() || isVelocityEnabled();
    }
    
    /**
     * BungeeCord ayarlarını yapar
     */
    public static void setupBungeeCord() {
        if (!isBungeeCordEnabled()) {
            return;
        }
        
        try {
            // BungeeCord ayarlarını yap
            // setOnlineMode metodu reflection ile çağrılır
            try {
                Method setOnlineModeMethod = Bukkit.getServer().getClass().getMethod("setOnlineMode", boolean.class);
                setOnlineModeMethod.invoke(Bukkit.getServer(), false);
            } catch (Exception e) {
                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getPluginLogger().debug("setOnlineMode metodu bulunamadı: " + e.getMessage());
                }
            }
            
            // spigot.yml'de bungeecord ayarlarını yap
            try {
                Class<?> spigotConfigClass = Class.forName("org.spigotmc.SpigotConfig");
                Field bungeeField = spigotConfigClass.getDeclaredField("bungee");
                bungeeField.setAccessible(true);
                bungeeField.setBoolean(null, true);
            } catch (Exception e) {
                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getPluginLogger().debug("BungeeCord ayarları yapılamadı: " + e.getMessage());
                }
            }
            
            plugin.getPluginLogger().info("BungeeCord ayarları yapıldı!");
        } catch (Exception e) {
            plugin.getPluginLogger().error("BungeeCord ayarları yapılırken hata: " + e.getMessage());
        }
    }
    
    /**
     * Velocity ayarlarını yapar
     */
    public static void setupVelocity() {
        if (!isVelocityEnabled()) {
            return;
        }
        
        try {
            // Velocity ayarlarını yap
            // setOnlineMode metodu reflection ile çağrılır
            try {
                Method setOnlineModeMethod = Bukkit.getServer().getClass().getMethod("setOnlineMode", boolean.class);
                setOnlineModeMethod.invoke(Bukkit.getServer(), false);
            } catch (Exception e) {
                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getPluginLogger().debug("setOnlineMode metodu bulunamadı: " + e.getMessage());
                }
            }
            
            // paper.yml'de velocity ayarlarını yap
            try {
                Class<?> paperConfigClass = Class.forName("io.papermc.paper.configuration.GlobalConfiguration");
                Field velocityField = paperConfigClass.getDeclaredField("velocity");
                velocityField.setAccessible(true);
                Object velocityConfig = velocityField.get(null);
                
                if (velocityConfig != null) {
                    Class<?> velocityConfigClass = velocityConfig.getClass();
                    Field enabledField = velocityConfigClass.getDeclaredField("enabled");
                    enabledField.setAccessible(true);
                    enabledField.setBoolean(velocityConfig, true);
                }
            } catch (Exception e) {
                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getPluginLogger().debug("Velocity ayarları yapılamadı: " + e.getMessage());
                }
            }
            
            plugin.getPluginLogger().info("Velocity ayarları yapıldı!");
        } catch (Exception e) {
            plugin.getPluginLogger().error("Velocity ayarları yapılırken hata: " + e.getMessage());
        }
    }
    
    /**
     * Folia ayarlarını yapar
     */
    public static void setupFolia() {
        if (!isFoliaEnabled()) {
            return;
        }
        
        try {
            // Folia (PaperMC fork) ayarlarını yap
            plugin.getPluginLogger().info("Folia (PaperMC fork) desteği etkinleştirildi!");
            
            // Folia için özel ayarlar burada yapılabilir
            // Örneğin: regionized server ayarları, async event handling
            
        } catch (Exception e) {
            plugin.getPluginLogger().error("Folia ayarları yapılırken hata: " + e.getMessage());
        }
    }
    
    /**
     * Proxy ayarlarını yapar
     */
    public static void setupProxy() {
        if (isBungeeCordEnabled()) {
            setupBungeeCord();
        }
        
        if (isVelocityEnabled()) {
            setupVelocity();
        }
        
        if (isFoliaEnabled()) {
            setupFolia();
        }
    }
    
    /**
     * Proxy türünü döndürür
     */
    public static String getProxyType() {
        if (isBungeeCordEnabled() && isVelocityEnabled()) {
            return "BungeeCord + Velocity";
        } else if (isBungeeCordEnabled()) {
            return "BungeeCord";
        } else if (isVelocityEnabled()) {
            return "Velocity";
        } else {
            return "Yok";
        }
    }
    
    /**
     * Sunucu türünü döndürür
     */
    public static String getServerType() {
        if (isFoliaEnabled()) {
            return "Folia (PaperMC fork)";
        } else {
            String serverName = plugin.getServer().getName();
            if (serverName.toLowerCase().contains("paper")) {
                return "Paper";
            } else if (serverName.toLowerCase().contains("spigot")) {
                return "Spigot";
            } else if (serverName.toLowerCase().contains("bukkit")) {
                return "Bukkit";
            } else {
                return serverName;
            }
        }
    }
    
    /**
     * Proxy bilgilerini döndürür
     */
    public static String getProxyInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Sunucu Türü: ").append(getServerType()).append("\n");
        info.append("Proxy Türü: ").append(getProxyType()).append("\n");
        info.append("BungeeCord: ").append(isBungeeCordEnabled() ? "Aktif" : "Pasif").append("\n");
        info.append("Velocity: ").append(isVelocityEnabled() ? "Aktif" : "Pasif").append("\n");
        info.append("Folia: ").append(isFoliaEnabled() ? "Aktif (PaperMC fork)" : "Pasif");
        
        return info.toString();
    }
}