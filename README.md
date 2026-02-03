Dokumentacja Techniczna Systemu "Home Inventory"
Autor: Bartosz Winczowski
Wersja: 1.0.0
Technologia: Android (Kotlin, Jetpack Compose)
1. Przegląd Projektu
Home Inventory to aplikacja mobilna na platformę Android, służąca do zarządzania domowym inwentarzem. System umożliwia katalogowanie przedmiotów, przypisywanie ich do pomieszczeń i kategorii, monitorowanie statusu gwarancji oraz generowanie statystyk. Aplikacja wykorzystuje uczenie maszynowe  do skanowania kodów kreskowych oraz sensory urządzenia do poprawy User Experience.
  
  
 
2. Stos Technologiczny 
Aplikacja została zbudowana w oparciu o nowoczesne standardy Google (Modern Android Development):
•	Język programowania: Kotlin.
•	Interfejs użytkownika: Jetpack Compose (deklaratywne UI).
•	Architektura: MVVM (Model-View-ViewModel) .
•	Wstrzykiwanie zależności: Hilt (Dagger).
•	Baza danych: Room (SQLite abstraction).
•	Asynchroniczność: Kotlin Coroutines & Flow (StateFlow, SharedFlow).
•	Multimedia i AI:
o	CameraX: Obsługa podglądu kamery.
o	Google ML Kit: Analiza obrazu i rozpoznawanie kodów kreskowych/QR.
•	Sensory: Obsługa czujnika światła (Light Sensor) oraz wibracji (Haptic Feedback).
•	Serializacja danych: Gson (Import/Eksport do JSON).
3. Architektura Systemu
Projekt realizuje podział na warstwy.
3.1. Warstwa Danych 
Odpowiada za źródła danych i ich mapowanie.
•	DAO (Data Access Objects): Interfejsy definiujące zapytania SQL (np. ItemDao, LocationDao).
•	Entity: Klasy reprezentujące tabele w bazie danych.
•	DTO (Data Transfer Objects): Obiekty pomocnicze do importu danych z JSON (InitialItemDto).
3.2. Warstwa Domeny 
Zawiera logikę biznesową niezależną od frameworka Android.
•	Repository Interface: Abstrakcja dostępu do danych (np. ItemRepository). Dzięki temu ViewModel nie wie, czy dane pochodzą z bazy SQL, czy z sieci.
3.3. Warstwa Prezentacji 
Odpowiada za wyświetlanie danych i interakcję z użytkownikiem.
•	ViewModel: Zarządza stanem ekranów, przetwarza dane z Repozytoriów i wystawia je jako strumienie (Flow) dla UI.
•	UI (Compose): Komponenty widoku (Ekrany: HomeScreen, ScannerScreen, StatsScreen).
4. Model Danych (Baza Danych)
Baza danych AppDatabase składa się z 5 powiązanych tabel. Relacje zostały zabezpieczone kluczami obcymi z regułami CASCADE (kaskadowe usuwanie) lub SET_NULL.
Schemat Tabel (ERD):
1.	items (Przedmioty)
o	id (PK): Unikalny identyfikator.
o	categoryId (FK -> categories): Relacja wiele-do-jednego.
o	locationId (FK -> locations): Relacja wiele-do-jednego.
o	modelId: Kod modelu (klucz do parowania przy imporcie).
o	serialNumber, price, dateAdded, imageUri.
2.	locations (Lokalizacje)
o	id (PK).
o	name, floor: Np. "Salon", "Parter".
3.	categories (Kategorie)
o	id (PK).
o	name (Unique Index), description, iconResId.
4.	warranties (Gwarancje)
o	id (PK).
o	itemId (FK -> items): Relacja jeden-do-jednego (z onDelete = CASCADE). Usunięcie przedmiotu usuwa gwarancję.
o	expiryDate, provider.
5.	reminders (Przypomnienia)
o	id (PK).
o	itemId (FK -> items): Powiązanie z przedmiotem.
5. Kluczowe Funkcjonalności i Implementacja
5.1. Skaner Kodów (AI & CameraX)
Moduł ScannerScreen wykorzystuje ImageAnalysis z biblioteki CameraX.
•	Obraz z kamery jest analizowany w czasie rzeczywistym przez ML Kit.
•	Po wykryciu kodu kreskowego następuje wibracja (Vibration.kt) i automatyczne utworzenie obiektu w bazie.
•	LightSensor Integration: Aplikacja monitoruje natężenie światła (lux) wykorzystując callbackFlow i sugeruje użytkownikowi działania, gdy jest za ciemno.
5.2. Zarządzanie Lokalizacjami (Grid System)
Ekran LocationScreen wyświetla siatkę pomieszczeń (LazyVerticalGrid).
•	Każda karta pokazuje nazwę pokoju oraz dynamicznie obliczoną liczbę przedmiotów wewnątrz (count).
•	Zaimplementowano interaktywne menu (Dropdown) oraz gesty (Long Press) do edycji i usuwania lokalizacji.
5.3. Statystyki (Custom Drawing)
Ekran StatsScreen nie używa zewnętrznych bibliotek do wykresów.
•	Wykres kołowy (Pie Chart) jest rysowany natywnie na Canvas przy użyciu funkcji trygonometrycznych do obliczania kątów wycinków (drawArc).
•	Legenda jest generowana dynamicznie na podstawie danych z CategoryRepository.
5.4. Zarządzanie Danymi (Import/Eksport)
DataManagementViewModel obsługuje logikę "Seedowania" danych.
•	Przy pierwszym uruchomieniu aplikacja parsuje pliki initial_data.json oraz initial_warranties.json przy użyciu biblioteki Gson.
•	Inteligentne parowanie: Gwarancje są przypisywane do przedmiotów na podstawie pola modelId, co uniezależnia import od generowanych ID w bazie danych.
6. Wstrzykiwanie Zależności (Hilt Modules)
System wykorzystuje konteneryzację zależności:
•	DatabaseModule: Dostarcza instancję AppDatabase (Singleton) oraz poszczególne obiekty DAO.
•	RepositoryModule: Wiąże interfejsy (np. ItemRepository) z ich implementacjami (ItemRepositoryImpl) przy użyciu adnotacji @Binds, co zapewnia luźne powiązania komponentów.
7. Instrukcja Uruchomienia i Budowania
Wymagania:
•	Android Studio.
•	JDK 17.
•	Urządzenie z systemem Android 12 (Min SDK 31).

