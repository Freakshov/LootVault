<p align="center">
  <img src="lootvault_banner.jpg" alt="LootVault Banner" width="100%" />
</p>

<p align="center">
  <strong>Minecraft 1.21.11 için Akıllı ve Ultra Hızlı Konteyner Süpürücü Modu</strong>
</p>

<p align="center">
  <a href="https://fabricmc.net/"><img src="https://img.shields.io/badge/Modloader-Fabric-blue?style=for-the-badge&logo=fabric" alt="Fabric" /></a>
  <a href="https://www.minecraft.net/"><img src="https://img.shields.io/badge/Minecraft-1.21.11-brightgreen?style=for-the-badge&logo=minecraft" alt="Minecraft 1.21.11" /></a>
  <a href="https://adoptium.net/"><img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java 21" /></a>
  <a href="https://github.com/Freakshov"><img src="https://img.shields.io/badge/Developer-Freakshov-purple?style=for-the-badge&logo=github" alt="Developer Freakshov" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License MIT" /></a>
</p>

---

## 📖 Hakkında

**LootVault**, açık kaynaklı **ShulkerSnap** modunun modern Minecraft mimarisine (**1.21.11 / Fabric Loom 1.14**) taşınmış, akıllı büyü filtreleme ve anti-cheat bypass yetenekleriyle donatılmış gelişmiş halidir.

Konteynerleri (Shulker kutuları, sandıklar, fıçılar) açtığınız milisaniyede içindeki eşyaları envanterinize çeker, akıllı filtrelerle değerli eşyalara öncelik verir ve işlemi tamamlar tamamlamaz ekranı otomatik olarak kapatır.

---

## ✨ Özellikler

- ⚡ **Işık Hızında Çekim (`Quick-Move`):** Paketler istemciye ulaştığı anda slot tıklamalarını simüle ederek anında envantere aktarır.
- 📦 **Tüm Konteyner Türleri:**
  - 16 renk boyalı ve renksiz Shulker Kutuları
  - Tekli & Çiftli Sandıklar, Tuzaklı Sandıklar, Ender Sandıkları
  - Fıçılar (Barrels)
  - 1.21 Bakır Sandıklar (Copper Chests)
- 🧠 **Akıllı Büyü Filtresi (`Smart Filter`):** 1.21 Data Components (`DataComponentTypes`) API'si ile çalışır. Envanterinizdeki eşyalarla konteynerdekileri kıyaslayarak üstün büyülere sahip eşyaları önceliklendirir, envanterin gereksiz çöplerle dolmasını engeller.
- 🎲 **Doğal / Rastgele Sıralama (`Anti-Cheat Bypass`):** Eşyaları ardışık (`0, 1, 2...`) sırada değil, rastgele karıştırılmış slot sırasıyla çeker. Sunucu kontrollerinin lineer envanter süpürme hedefini bozarak güvenli bir deneyim sunar.
- 🚪 **Otomatik Kapatma (`Auto-Close`):** Eşyalar çekildiği an arayüzü kapatır; koşarken veya savaşırken duraksamazsınız.
- ⏱️ **Kişiselleştirilebilir Gecikme:** `0 ms` (anında) ile özel milisaniye gecikmeleri arasında geçiş imkanı.
- ⌨️ **Oyun İçi Hızlı Kısayol Değiştirme:** Menüye girmeden sohbete `/vault bind` yazıp istenen tuşa basarak anında kısayol atanabilir.
- 🌐 **Çoklu Dil Desteği:** Türkçe (`tr_tr`), İngilizce (`en_us`) ve İspanyolca (`es_es`) dahili yerelleştirme.

---

## 🎮 Komutlar ve Kullanım

| Tuş / Komut | Açıklama |
| :--- | :--- |
| **`J` Tuşu** | Modu anında açar veya kapatır *(Değiştirilebilir)* |
| `/vault toggle` | Modu komutla açar / kapatır |
| `/vault bind` | Tuş dinleme modunu başlatır; ardından basılan ilk tuş kısayol olur *(ESC iptal eder)* |
| `/vault delay <ms>` | Eşya çekme gecikmesini ayarlar (`0` ms anında çeker) |
| `/vault random <true\|false>` | Rastgele sıra ile çekim (Anti-Cheat Bypass) modunu açar / kapatır |
| `/vault smart <true\|false>` | Akıllı büyü ve eşya önceliklendirme filtresini açar / kapatır |
| `/vault mode <all\|smart_only>` | Çekim modunu belirler (`all` veya `smart_only`) |
| `/vault autoclose <true\|false>` | Çekim sonrası konteyner ekranının otomatik kapanmasını ayarlar |
| `/vault chests <true\|false>` | Sandık desteğini açar / kapatır |
| `/vault shulkers <true\|false>` | Shulker kutusu desteğini açar / kapatır |
| `/vault barrels <true\|false>` | Fıçı desteğini açar / kapatır |
| `/vault lang <tr\|en\|es>` | Dili değiştirir |
| `/vault status` | Tüm aktif ayarları sohbette düzenli bir kart olarak listeler |

---

## 📥 Kurulum

1. Bilgisayarınızda **Minecraft 1.21.11**, **Fabric Loader (0.18+)** ve **Fabric API** kurulu olduğundan emin olun.
2. [Releases](https://github.com/Freakshov/ShulkerSnap) bölümünden veya derlediğiniz `LootVault-2.0.0.jar` dosyasını `.minecraft/mods` klasörünüze taşıyın.
3. Oyunu başlatın; mod tamamen istemci taraflıdır (Client-Side), sunucuya mod kurulması gerekmez!

---

## 🔨 Kaynak Koddan Derleme (Build)

Projeyi kendiniz derlemek için Java 21 gereklidir:

```bash
# Proje dizininde derleme komutunu çalıştırın:
# Windows (PowerShell):
.\gradlew.bat build

# Linux / macOS:
./gradlew build
```

Derleme tamamlandığında hazır mod dosyası `build/libs/LootVault-2.0.0.jar` konumunda üretilir.

---

## 👥 Yapımcı ve Emeği Geçenler (Credits)

- **Orijinal Geliştirici:** **[Freakshov](https://github.com/Freakshov)** — [ShulkerSnap](https://github.com/Freakshov/ShulkerSnap) temel mimarisi ve fikri.
- **Modernizasyon & LootVault Ekibi:** 1.21.11 portu, Data Components API entegrasyonu, Anti-Cheat bypass ve akıllı filtreleme sistemleri.

---

## 📜 Lisans

Bu proje [MIT](LICENSE) lisansı altında korunmaktadır. Orijinal yazara ve kaynak projeye atıfta bulunarak serbestçe geliştirilebilir.
