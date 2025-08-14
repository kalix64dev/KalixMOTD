package com.kalix.motd.managers;

import com.kalix.motd.KalixMOTD;
import com.kalix.motd.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Rate limiting yöneticisi
 * 
 * @author Kalix
 */
public class RateLimitManager {
    
    private final KalixMOTD plugin;
    private final Logger logger;
    private final ConcurrentHashMap<String, RateLimitEntry> rateLimitMap;
    private final ScheduledExecutorService scheduler;
    
    private boolean enabled;
    private int requestsPerWindow;
    private int windowSeconds;
    private int blockDurationSeconds;
    private boolean emptyResponseOnLimit;
    private boolean logViolations;
    
    /**
     * Rate limit girişi
     */
    private static class RateLimitEntry {
        private int requestCount;
        private long firstRequestTime;
        private long lastRequestTime;
        private boolean isBlocked;
        private long blockStartTime;
        
        public RateLimitEntry() {
            this.requestCount = 1;
            this.firstRequestTime = System.currentTimeMillis();
            this.lastRequestTime = System.currentTimeMillis();
            this.isBlocked = false;
        }
        
        public void incrementRequest() {
            this.requestCount++;
            this.lastRequestTime = System.currentTimeMillis();
        }
        
        public void reset() {
            this.requestCount = 1;
            this.firstRequestTime = System.currentTimeMillis();
            this.lastRequestTime = System.currentTimeMillis();
            this.isBlocked = false;
        }
        
        public void block() {
            this.isBlocked = true;
            this.blockStartTime = System.currentTimeMillis();
        }
        
        public boolean isBlockExpired(int blockDurationSeconds) {
            if (!isBlocked) return false;
            return System.currentTimeMillis() - blockStartTime >= blockDurationSeconds * 1000L;
        }
        
        public boolean isWindowExpired(int windowSeconds) {
            return System.currentTimeMillis() - firstRequestTime >= windowSeconds * 1000L;
        }
        
        public boolean isLimitExceeded(int requestsPerWindow) {
            return requestCount > requestsPerWindow;
        }
    }
    
    /**
     * Constructor
     */
    public RateLimitManager(KalixMOTD plugin) {
        this.plugin = plugin;
        this.logger = plugin.getPluginLogger();
        this.rateLimitMap = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        loadConfig();
        startCleanupTask();
    }
    
    /**
     * Konfigürasyonu yükler
     */
    public void loadConfig() {
        this.enabled = plugin.getConfigManager().getBoolean("advanced.rate-limit.enabled", true);
        this.requestsPerWindow = plugin.getConfigManager().getInt("advanced.rate-limit.requests-per-window", 5);
        this.windowSeconds = plugin.getConfigManager().getInt("advanced.rate-limit.window-seconds", 5);
        this.blockDurationSeconds = plugin.getConfigManager().getInt("advanced.rate-limit.block-duration-seconds", 30);
        this.emptyResponseOnLimit = plugin.getConfigManager().getBoolean("advanced.rate-limit.empty-response-on-limit", true);
        this.logViolations = plugin.getConfigManager().getBoolean("advanced.rate-limit.log-violations", true);
        
        if (enabled) {
            logger.info("Rate limiting etkinleştirildi: " + requestsPerWindow + " istek/" + windowSeconds + "s");
        } else {
            logger.info("Rate limiting devre dışı");
        }
    }
    
    /**
     * Temizlik görevini başlatır
     */
    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                cleanupExpiredEntries();
            } catch (Exception e) {
                logger.error("Rate limit temizlik görevinde hata: " + e.getMessage());
            }
        }, 60, 60, TimeUnit.SECONDS);
    }
    
    /**
     * Süresi dolmuş girişleri temizler
     */
    private void cleanupExpiredEntries() {
        int removedCount = 0;
        
        for (String ip : rateLimitMap.keySet()) {
            RateLimitEntry entry = rateLimitMap.get(ip);
            if (entry != null) {
                // Blok süresi dolmuşsa veya pencere süresi dolmuşsa temizle
                if ((entry.isBlocked && entry.isBlockExpired(blockDurationSeconds)) || 
                    (!entry.isBlocked && entry.isWindowExpired(windowSeconds))) {
                    rateLimitMap.remove(ip);
                    removedCount++;
                }
            }
        }
        
        if (removedCount > 0 && plugin.getConfigManager().isDebugEnabled()) {
            logger.debug("Rate limit temizliği: " + removedCount + " giriş temizlendi");
        }
    }
    
    /**
     * IP adresinin rate limit durumunu kontrol eder
     * 
     * @param ip IP adresi
     * @return Rate limit aşıldı mı
     */
    public boolean isRateLimited(String ip) {
        if (!enabled) {
            return false;
        }
        
        RateLimitEntry entry = rateLimitMap.get(ip);
        
        if (entry == null) {
            // Yeni IP, giriş oluştur
            entry = new RateLimitEntry();
            rateLimitMap.put(ip, entry);
            return false;
        }
        
        // Blok kontrolü
        if (entry.isBlocked) {
            if (entry.isBlockExpired(blockDurationSeconds)) {
                // Blok süresi dolmuş, sıfırla
                entry.reset();
                rateLimitMap.put(ip, entry);
                return false;
            } else {
                // Hala bloklu
                if (logViolations) {
                    logger.warn("Rate limit aşıldı (bloklu): " + ip);
                }
                return true;
            }
        }
        
        // Pencere süresi kontrolü
        if (entry.isWindowExpired(windowSeconds)) {
            // Pencere süresi dolmuş, sıfırla
            entry.reset();
            rateLimitMap.put(ip, entry);
            return false;
        }
        
        // İstek sayısını artır
        entry.incrementRequest();
        
        // Limit kontrolü
        if (entry.isLimitExceeded(requestsPerWindow)) {
            // Limit aşıldı, blokla
            entry.block();
            rateLimitMap.put(ip, entry);
            
            if (logViolations) {
                logger.warn("Rate limit aşıldı: " + ip + " (" + entry.requestCount + " istek/" + windowSeconds + "s)");
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Rate limit durumunu sıfırlar
     * 
     * @param ip IP adresi
     */
    public void resetRateLimit(String ip) {
        rateLimitMap.remove(ip);
        if (plugin.getConfigManager().isDebugEnabled()) {
            logger.debug("Rate limit sıfırlandı: " + ip);
        }
    }
    
    /**
     * Tüm rate limit durumlarını sıfırlar
     */
    public void resetAllRateLimits() {
        int count = rateLimitMap.size();
        rateLimitMap.clear();
        logger.info("Tüm rate limit durumları sıfırlandı: " + count + " IP");
    }
    
    /**
     * Rate limit istatistiklerini döndürür
     * 
     * @return İstatistik mesajı
     */
    public String getStats() {
        if (!enabled) {
            return "Rate limiting devre dışı";
        }
        
        int totalIPs = rateLimitMap.size();
        int blockedIPs = 0;
        
        for (RateLimitEntry entry : rateLimitMap.values()) {
            if (entry.isBlocked) {
                blockedIPs++;
            }
        }
        
        return String.format("Rate Limit İstatistikleri: %d toplam IP, %d bloklu IP", totalIPs, blockedIPs);
    }
    
    /**
     * Rate limiting etkin mi
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Boş yanıt gönderilsin mi
     */
    public boolean shouldSendEmptyResponse() {
        return emptyResponseOnLimit;
    }
    
    /**
     * Temizlik yapar
     */
    public void cleanup() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        rateLimitMap.clear();
        logger.info("Rate limit yöneticisi temizlendi");
    }
}