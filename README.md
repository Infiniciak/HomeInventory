Dokumentacja Techniczna Systemu "Home Inventory"
Autor: Bartosz Winczowski
## 🛠️ Stos Technologiczny

Aplikacja została zbudowana zgodnie ze standardami **Modern Android Development (MAD)**:

* **Język:** [Kotlin]
* **UI:** [Jetpack Compose]
* **Architektura:** MVVM (Model-View-ViewModel) + Clean Architecture (podział na warstwy)
* **Wstrzykiwanie zależności:** Hilt (Dagger)
* **Baza danych:** Room (SQLite) z relacjami i kluczami obcymi
* **AI & Multimedia:** CameraX + Google ML Kit
* **Sensory:** Android Sensor Manager (Light Sensor, Vibrator)
* **Serializacja:** Gson

---

## 🏗️ Architektura Systemu

Projekt realizuje ścisły podział na warstwy, co zapewnia testowalność i łatwą rozbudowę:

1.  **Warstwa Danych (Data):** * DAO (Data Access Objects) dla zapytań SQL.
    * Entity (Room) oraz DTO (Data Transfer Objects) dla importu JSON.
2.  **Warstwa Domeny (Domain):** * Interfejsy repozytoriów definiujące logikę biznesową niezależną od frameworka.
3.  **Warstwa Prezentacji (Presentation):** * ViewModel zarządzający stanem UI.
    * Komponenty Compose (HomeScreen, ScannerScreen, StatsScreen).



---

## 🗄️ Model Danych (ERD)

Baza danych `AppDatabase` składa się z 5 powiązanych tabel:
* `items`: Główna tabela przedmiotów (relacje z kategoriami i lokalizacjami).
* `locations`: Pomieszczenia (Salon, Kuchnia, itp.).
* `categories`: Kategorie (Elektronika, Meble, itp.).
* `warranties`: Gwarancje (relacja 1:1 z przedmiotem, kaskadowe usuwanie).
* `reminders`: Przypomnienia powiązane z przedmiotami.

---

## 🚀 Implementacja - Detale techniczne

### Skaner Kodów & AI
Moduł wykorzystuje `ImageAnalysis` z CameraX. Po wykryciu kodu przez ML Kit, aplikacja wyzwala wibrację i automatycznie paruje dane. Dodatkowo, system monitoruje natężenie światła (lux) przez `callbackFlow` i sugeruje użycie latarki w trudnych warunkach.

### Custom Drawing (Statystyki)
Wykresy kołowe w `StatsScreen` są rysowane bezpośrednio na komponencie `Canvas`. Wykorzystano funkcje trygonometryczne do obliczania kątów wycinków (`drawArc`), co eliminuje potrzebę stosowania ciężkich bibliotek zewnętrznych.

### Zarządzanie Danymi
Przy pierwszym uruchomieniu system parsuje pliki `initial_data.json` oraz `initial_warranties.json`. Zastosowano **inteligentne parowanie**: gwarancje są przypisywane do przedmiotów na podstawie pola `modelId`, co zapewnia spójność danych niezależnie od generowanych kluczy głównych (PK).

---

## ⚙️ Instrukcja Uruchomienia

1.  Sklonuj repozytorium: 
2.  Otwórz projekt w **Android Studio)**.
3.  Upewnij się, że masz zainstalowane **JDK 17**.
4.  Zbuduj projekt (`Build > Rebuild Project`).
5
.  Uruchom na urządzeniu fizycznym lub emulatorze z **Android 12+ (Min SDK 31)**.
