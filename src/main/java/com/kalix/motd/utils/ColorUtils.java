package com.kalix.motd.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renk kodlarını işleyen utility sınıfı
 * 
 * @author Kalix
 */
public class ColorUtils {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:([A-Fa-f0-9]{6}):([A-Fa-f0-9]{6})>(.*?)</gradient>");
    
    /**
     * Mesajı renklendirir
     */
    public static String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        // Hex renkleri işle (&#RRGGBB formatı)
        message = processHexColors(message);
        
        // Gradient renkleri işle
        message = processGradientColors(message);
        
        // Legacy renk kodlarını işle
        message = processLegacyColors(message);
        
        return message;
    }
    
    /**
     * Hex renkleri işler
     */
    private static String processHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String replacement = "§x";
            
            // Hex renk kodunu §x formatına çevir
            for (char c : hexColor.toCharArray()) {
                replacement += "§" + c;
            }
            
            matcher.appendReplacement(buffer, replacement);
        }
        
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    /**
     * Gradient renkleri işler
     */
    private static String processGradientColors(String message) {
        Matcher matcher = GRADIENT_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String color1 = matcher.group(1);
            String color2 = matcher.group(2);
            String text = matcher.group(3);
            
            String gradientText = createGradient(text, color1, color2);
            matcher.appendReplacement(buffer, gradientText);
        }
        
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    /**
     * Gradient metni oluşturur
     */
    private static String createGradient(String text, String color1, String color2) {
        if (text.isEmpty()) {
            return text;
        }
        
        StringBuilder result = new StringBuilder();
        int length = text.length();
        
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            
            // Renk geçişini hesapla
            double ratio = (double) i / (length - 1);
            String interpolatedColor = interpolateColor(color1, color2, ratio);
            
            // Hex renk kodunu §x formatına çevir
            String colorCode = "§x";
            for (char colorChar : interpolatedColor.toCharArray()) {
                colorCode += "§" + colorChar;
            }
            
            result.append(colorCode).append(c);
        }
        
        return result.toString();
    }
    
    /**
     * İki renk arasında geçiş yapar
     */
    private static String interpolateColor(String color1, String color2, double ratio) {
        int r1 = Integer.parseInt(color1.substring(0, 2), 16);
        int g1 = Integer.parseInt(color1.substring(2, 4), 16);
        int b1 = Integer.parseInt(color1.substring(4, 6), 16);
        
        int r2 = Integer.parseInt(color2.substring(0, 2), 16);
        int g2 = Integer.parseInt(color2.substring(2, 4), 16);
        int b2 = Integer.parseInt(color2.substring(4, 6), 16);
        
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        
        return String.format("%02x%02x%02x", r, g, b);
    }
    
    /**
     * Legacy renk kodlarını işler
     */
    private static String processLegacyColors(String message) {
        return message.replace("&", "§");
    }
    
    /**
     * Renk kodlarını temizler
     */
    public static String stripColors(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        
        // § kodlarını kaldır
        message = message.replaceAll("§[0-9a-fk-or]", "");
        
        // Hex renkleri kaldır
        message = message.replaceAll("§x(§[0-9a-fA-F]){6}", "");
        
        // Gradient kodlarını kaldır
        message = message.replaceAll("<gradient:[A-Fa-f0-9]{6}:[A-Fa-f0-9]{6}>(.*?)</gradient>", "$1");
        
        return message;
    }
    
    /**
     * Renk kodlarının geçerli olup olmadığını kontrol eder
     */
    public static boolean isValidColor(String color) {
        if (color == null || color.isEmpty()) {
            return false;
        }
        
        // Legacy renk kontrolü
        if (color.length() == 1) {
            return "0123456789abcdefklmnor".indexOf(color.toLowerCase()) != -1;
        }
        
        // Hex renk kontrolü
        if (color.startsWith("#") && color.length() == 7) {
            return color.substring(1).matches("[A-Fa-f0-9]{6}");
        }
        
        return false;
    }
}