# KalixMOTD

Ultra profesyonel MOTD (Message of the Day) plugin for Minecraft 1.8-1.21

## 🚀 Özellikler

### ✨ Temel Özellikler
- **Çoklu Sürüm Desteği**: Minecraft 1.8'den 1.21'e kadar tüm sürümleri destekler
- **Rastgele MOTD**: Belirli aralıklarla değişen MOTD mesajları
- **Özelleştirilebilir**: Tamamen özelleştirilebilir config sistemi
- **Performanslı**: Optimize edilmiş kod yapısı

### 🎨 Görsel Özellikler
- **Renk Desteği**: Legacy renk kodları (&a, &b, vb.)
- **Hex Renk Desteği**: Modern hex renk kodları (&#RRGGBB)
- **Gradient Desteği**: Gradient renk geçişleri
- **Hover Mesajları**: 1.16+ sürümlerde hover desteği
- **Click Mesajları**: URL, komut ve öneri desteği

### 🔧 Teknik Özellikler
- **PlaceholderAPI Entegrasyonu**: Tüm PlaceholderAPI placeholder'ları
- **Vault Entegrasyonu**: Ekonomi, izin ve chat desteği
- **Async İşlemler**: Performans için asenkron işlemler
- **Cache Sistemi**: Hızlı erişim için cache
- **Debug Modu**: Geliştirici dostu debug sistemi

### 📊 Sunucu Listesi Özellikleri
- **Özelleştirilebilir Oyuncu Sayısı**: Sahte online/max oyuncu sayısı
- **Sunucu İkonu**: Özel sunucu ikonu desteği
- **Sunucu Açıklaması**: Özelleştirilebilir sunucu açıklaması

## 📦 Kurulum

### Gereksinimler
- **Minecraft**: 1.8 - 1.21
- **Server Software**: Spigot, Paper, Bukkit
- **Java**: Java 8 veya üzeri
- **Opsiyonel**: PlaceholderAPI, Vault

### Kurulum Adımları
1. Plugin dosyasını `plugins` klasörüne kopyalayın
2. Sunucuyu yeniden başlatın
3. `config.yml` dosyasını özelleştirin
4. `/motd reload` komutu ile yeniden yükleyin

## ⚙️ Konfigürasyon

### Temel MOTD Ayarları
```yaml
motd:
  messages:
    line1: "§6§lKalixMOTD §8» §fHoş geldiniz!"
    line2: "§7Sunucu sürümü: §a1.8-1.21"
  
  random:
    enabled: true
    interval: 30 # saniye
    messages:
      - line1: "§6§lKalixMOTD §8» §fUltra profesyonel MOTD!"
        line2: "§7Desteklenen sürümler: §a1.8-1.21"
```

### Renk Desteği
```yaml
# Legacy renkler
line1: "&6&lKalixMOTD &8» &fHoş geldiniz!"

# Hex renkler
line1: "&#FF6B35&lKalixMOTD &#8B4513» &#FFFFFFHoş geldiniz!"

# Gradient renkler
line1: "<gradient:FF6B35:8B4513>KalixMOTD</gradient> §8» §fHoş geldiniz!"
```

### Placeholder Desteği
```yaml
# Özel placeholder'lar
line1: "§6§l{server_name} §8» §fHoş geldiniz!"
line2: "§7Oyuncular: §a{online}§7/§c{max}"

# PlaceholderAPI placeholder'ları
line1: "§6§l%player_name% §8» §fHoş geldiniz!"

# Vault placeholder'ları
line1: "§6§l{vault_prefix}%player_name% §8» §fHoş geldiniz!"
```

## 🎮 Komutlar

### Ana Komutlar
- `/motd` - Ana MOTD komutu
- `/motdset <line1|line2> <message>` - MOTD ayarlama

### Alt Komutlar
- `/motd reload` - Plugin'i yeniden yükle
- `/motd set <line1|line2> <message>` - MOTD ayarla
- `/motd list` - MOTD ayarlarını listele
- `/motd info` - Plugin bilgilerini göster
- `/motd help` - Yardım mesajını göster

## 🔐 İzinler

### Temel İzinler
- `kalixmotd.admin` - Tüm MOTD yetkileri
- `kalixmotd.reload` - Plugin yeniden yükleme
- `kalixmotd.set` - MOTD ayarlama
- `kalixmotd.info` - Plugin bilgilerini görme

## 📝 Placeholder'lar

### Özel Placeholder'lar
- `{online}` - Online oyuncu sayısı
- `{max}` - Maksimum oyuncu sayısı
- `{server_name}` - Sunucu adı
- `{server_version}` - Sunucu sürümü
- `{plugin_version}` - Plugin sürümü
- `{minecraft_version}` - Minecraft sürümü

### Vault Placeholder'ları
- `{vault_balance}` - Sunucu bakiyesi
- `{vault_group}` - Sunucu grubu
- `{vault_prefix}` - Sunucu prefix'i
- `{vault_suffix}` - Sunucu suffix'i

## 🎨 Renk Kodları

### Legacy Renkler
- `&0` - Siyah
- `&1` - Koyu mavi
- `&2` - Koyu yeşil
- `&3` - Koyu turkuaz
- `&4` - Koyu kırmızı
- `&5` - Koyu mor
- `&6` - Altın
- `&7` - Gri
- `&8` - Koyu gri
- `&9` - Mavi
- `&a` - Yeşil
- `&b` - Turkuaz
- `&c` - Kırmızı
- `&d` - Pembe
- `&e` - Sarı
- `&f` - Beyaz

### Format Kodları
- `&k` - Karışık
- `&l` - Kalın
- `&m` - Üstü çizili
- `&n` - Altı çizili
- `&o` - İtalik
- `&r` - Sıfırla

### Hex Renkler
- `&#RRGGBB` formatında hex renk kodları
- Örnek: `&#FF6B35` - Turuncu

### Gradient Renkler
- `<gradient:RRGGBB:RRGGBB>metin</gradient>` formatında
- Örnek: `<gradient:FF6B35:8B4513>KalixMOTD</gradient>`

## 🔧 Gelişmiş Ayarlar

### Performans Ayarları
```yaml
performance:
  cache:
    enabled: true
    duration: 300 # 5 dakika
  
  async:
    enabled: true
    thread-pool-size: 2
```

### Debug Ayarları
```yaml
settings:
  debug: false
  check-updates: true
  placeholderapi-support: true
  vault-support: true
```

### Log Ayarları
```yaml
logging:
  enabled: true
  level: "INFO"
  file: "logs/kalixmotd.log"
  max-size: "10MB"
  max-files: 5
```

## 🐛 Sorun Giderme

### Yaygın Sorunlar

#### Plugin Yüklenmiyor
- Java 8 veya üzeri kullandığınızdan emin olun
- Sunucu sürümünüzün desteklendiğini kontrol edin
- Console'da hata mesajlarını kontrol edin

#### MOTD Görünmüyor
- Config dosyasını kontrol edin
- `/motd reload` komutunu çalıştırın
- Debug modunu açın ve logları kontrol edin

#### Placeholder'lar Çalışmıyor
- PlaceholderAPI'nin yüklü olduğundan emin olun
- Placeholder'ların doğru yazıldığını kontrol edin
- Config'de placeholder desteğinin açık olduğunu kontrol edin

#### Renkler Görünmüyor
- Renk kodlarının doğru yazıldığını kontrol edin
- Sunucu sürümünüzün hex renk desteğini kontrol edin
- Legacy renk kodları için `&` kullandığınızdan emin olun

## 📞 Destek

### İletişim
- **GitHub**: [KalixMOTD Repository](https://github.com/kalix/KalixMOTD)
- **Discord**: [Kalix Discord](https://discord.gg/kalix)
- **Email**: support@kalix.com

### Katkıda Bulunma
1. Repository'yi fork edin
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Branch'inizi push edin (`git push origin feature/AmazingFeature`)
5. Pull Request oluşturun

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için `LICENSE` dosyasına bakın.

## 🙏 Teşekkürler

- **Spigot Team** - Spigot API
- **Paper Team** - Paper API
- **PlaceholderAPI Team** - PlaceholderAPI
- **Vault Team** - Vault API
- **Minecraft Community** - Sürekli destek

## 📈 Sürüm Geçmişi

### v1.0.0
- İlk sürüm
- Temel MOTD özellikleri
- Çoklu sürüm desteği
- PlaceholderAPI entegrasyonu
- Vault entegrasyonu
- Renk desteği
- Rastgele MOTD
- Hover ve Click desteği

---

**KalixMOTD** - Ultra profesyonel MOTD deneyimi için tasarlandı! 🚀