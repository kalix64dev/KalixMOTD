package com.kalix.motd.utils;

import org.bukkit.Bukkit;

/**
 * Minecraft sürüm kontrolü yapan utility sınıfı
 * 
 * @author Kalix
 */
public class VersionUtils {
    
    private static String minecraftVersion;
    private static int majorVersion;
    private static int minorVersion;
    private static int patchVersion;
    
    static {
        parseVersion();
    }
    
    /**
     * Minecraft sürümünü parse eder
     */
    private static void parseVersion() {
        String version = Bukkit.getVersion();
        
        // Sürüm bilgisini çıkar
        if (version.contains("MC: ")) {
            version = version.split("MC: ")[1].split("\\)")[0];
        } else if (version.contains("(MC: ")) {
            version = version.split("\\(MC: ")[1].split("\\)")[0];
        } else if (version.contains("git-")) {
            // Spigot sürümü
            version = version.split("git-")[1].split("-")[0];
        }
        
        minecraftVersion = version;
        
        // Major, minor, patch sürümlerini parse et
        String[] parts = version.split("\\.");
        if (parts.length >= 2) {
            try {
                majorVersion = Integer.parseInt(parts[0]);
                minorVersion = Integer.parseInt(parts[1]);
                
                if (parts.length >= 3) {
                    // Patch sürümü (örn: 1.8.8)
                    String patchStr = parts[2];
                    if (patchStr.contains("-")) {
                        patchStr = patchStr.split("-")[0];
                    }
                    patchVersion = Integer.parseInt(patchStr);
                } else {
                    patchVersion = 0;
                }
            } catch (NumberFormatException e) {
                // Varsayılan değerler
                majorVersion = 1;
                minorVersion = 8;
                patchVersion = 0;
            }
        } else {
            // Varsayılan değerler
            majorVersion = 1;
            minorVersion = 8;
            patchVersion = 0;
        }
    }
    
    /**
     * Minecraft sürümünü döndürür
     */
    public static String getMinecraftVersion() {
        return minecraftVersion;
    }
    
    /**
     * Major sürümü döndürür
     */
    public static int getMajorVersion() {
        return majorVersion;
    }
    
    /**
     * Minor sürümü döndürür
     */
    public static int getMinorVersion() {
        return minorVersion;
    }
    
    /**
     * Patch sürümü döndürür
     */
    public static int getPatchVersion() {
        return patchVersion;
    }
    
    /**
     * Verilen sürümden büyük veya eşit olup olmadığını kontrol eder
     */
    public static boolean isVersionAtLeast(String version) {
        String[] parts = version.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        
        try {
            int targetMajor = Integer.parseInt(parts[0]);
            int targetMinor = Integer.parseInt(parts[1]);
            int targetPatch = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;
            
            if (majorVersion > targetMajor) {
                return true;
            } else if (majorVersion == targetMajor) {
                if (minorVersion > targetMinor) {
                    return true;
                } else if (minorVersion == targetMinor) {
                    return patchVersion >= targetPatch;
                }
            }
            
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Verilen sürümden küçük olup olmadığını kontrol eder
     */
    public static boolean isVersionBelow(String version) {
        return !isVersionAtLeast(version);
    }
    
    /**
     * Verilen sürüm aralığında olup olmadığını kontrol eder
     */
    public static boolean isVersionBetween(String minVersion, String maxVersion) {
        return isVersionAtLeast(minVersion) && isVersionBelow(maxVersion);
    }
    
    /**
     * 1.8 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_8() {
        return majorVersion == 1 && minorVersion == 8;
    }
    
    /**
     * 1.9 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_9() {
        return majorVersion == 1 && minorVersion == 9;
    }
    
    /**
     * 1.10 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_10() {
        return majorVersion == 1 && minorVersion == 10;
    }
    
    /**
     * 1.11 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_11() {
        return majorVersion == 1 && minorVersion == 11;
    }
    
    /**
     * 1.12 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_12() {
        return majorVersion == 1 && minorVersion == 12;
    }
    
    /**
     * 1.13 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_13() {
        return majorVersion == 1 && minorVersion == 13;
    }
    
    /**
     * 1.14 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_14() {
        return majorVersion == 1 && minorVersion == 14;
    }
    
    /**
     * 1.15 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_15() {
        return majorVersion == 1 && minorVersion == 15;
    }
    
    /**
     * 1.16 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_16() {
        return majorVersion == 1 && minorVersion == 16;
    }
    
    /**
     * 1.17 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_17() {
        return majorVersion == 1 && minorVersion == 17;
    }
    
    /**
     * 1.18 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_18() {
        return majorVersion == 1 && minorVersion == 18;
    }
    
    /**
     * 1.19 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_19() {
        return majorVersion == 1 && minorVersion == 19;
    }
    
    /**
     * 1.20 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_20() {
        return majorVersion == 1 && minorVersion == 20;
    }
    
    /**
     * 1.21 sürümü olup olmadığını kontrol eder
     */
    public static boolean isVersion1_21() {
        return majorVersion == 1 && minorVersion == 21;
    }
    
    /**
     * Sürüm bilgilerini string olarak döndürür
     */
    public static String getVersionInfo() {
        return String.format("Minecraft %s (Major: %d, Minor: %d, Patch: %d)", 
            minecraftVersion, majorVersion, minorVersion, patchVersion);
    }
}