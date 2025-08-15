package com.kalix.motd.utils;

import com.kalix.motd.KalixMOTD;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Plugin logger utility sınıfı
 * 
 * @author Kalix
 */
public class Logger {
    
    private final KalixMOTD plugin;
    private final java.util.logging.Logger bukkitLogger;
    private final SimpleDateFormat dateFormat;
    private FileWriter fileWriter;
    private PrintWriter printWriter;
    
    public Logger(KalixMOTD plugin) {
        this.plugin = plugin;
        this.bukkitLogger = plugin.getLogger();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // ConfigManager henüz hazır olmayabilir, setupFileLogger'ı daha sonra çağır
        // setupFileLogger() çağrısı kaldırıldı
    }
    
    /**
     * Dosya logger'ını kurar
     */
    public void setupFileLogger() {
        try {
            // ConfigManager null kontrolü
            if (plugin.getConfigManager() == null) {
                return;
            }
            
            if (!plugin.getConfigManager().getBoolean("logging.enabled", true)) {
                return;
            }
            
            String logFile = plugin.getConfigManager().getString("logging.file", "logs/kalixmotd.log");
            File logDir = new File(plugin.getDataFolder(), "logs");
            
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            File file = new File(plugin.getDataFolder(), logFile);
            fileWriter = new FileWriter(file, true);
            printWriter = new PrintWriter(fileWriter);
            
        } catch (IOException e) {
            bukkitLogger.warning("Log dosyası oluşturulamadı: " + e.getMessage());
        } catch (Exception e) {
            bukkitLogger.warning("Logger kurulumunda hata: " + e.getMessage());
        }
    }
    
    /**
     * Info mesajı loglar
     */
    public void info(String message) {
        log(Level.INFO, message);
    }
    
    /**
     * Warning mesajı loglar
     */
    public void warn(String message) {
        log(Level.WARNING, message);
    }
    
    /**
     * Error mesajı loglar
     */
    public void error(String message) {
        log(Level.SEVERE, message);
    }
    
    /**
     * Debug mesajı loglar
     */
    public void debug(String message) {
        try {
            if (plugin.getConfigManager() != null && plugin.getConfigManager().isDebugEnabled()) {
                log(Level.INFO, "[DEBUG] " + message);
            }
        } catch (Exception e) {
            // ConfigManager henüz hazır değilse debug mesajını gösterme
        }
    }
    
    /**
     * Mesajı loglar
     */
    private void log(Level level, String message) {
        String timestamp = dateFormat.format(new Date());
        String logMessage = "[" + timestamp + "] " + message;
        
        // Bukkit logger'a gönder
        bukkitLogger.log(level, message);
        
        // Dosyaya yaz
        if (printWriter != null) {
            printWriter.println(logMessage);
            printWriter.flush();
        }
    }
    
    /**
     * Exception'ı loglar
     */
    public void logException(String message, Exception e) {
        error(message + ": " + e.getMessage());
        
        try {
            if (plugin.getConfigManager() != null && plugin.getConfigManager().isDebugEnabled()) {
                e.printStackTrace();
                
                // Stack trace'i dosyaya yaz
                if (printWriter != null) {
                    printWriter.println("Stack trace:");
                    e.printStackTrace(printWriter);
                    printWriter.flush();
                }
            }
        } catch (Exception ex) {
            // ConfigManager henüz hazır değilse sadece console'a yazdır
            e.printStackTrace();
        }
    }
    
    /**
     * Logger'ı kapatır
     */
    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
        
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                bukkitLogger.warning("Log dosyası kapatılırken hata: " + e.getMessage());
            }
        }
    }
}