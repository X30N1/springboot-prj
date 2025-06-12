# System Pytań i Głosowania

Aplikacja webowa umożliwiająca tworzenie pytań i głosowanie na nie, zbudowana przy użyciu Spring Boot i JavaScript.

## Funkcjonalności

- Przeglądanie listy pytań
- Dodawanie nowych pytań
- Edycja istniejących pytań
- Głosowanie (Tak/Nie) na pytania
- Każdy użytkownik może głosować tylko raz na dane pytanie (rozpoznawanie po adresie IP)

## Technologie

### Backend
- Java 24
- Spring Boot
- Spring Data JPA
- Jakarta EE
- H2 Database (baza danych w pamięci)

### Frontend
- HTML5
- CSS3
- JavaScript (vanilla)

## Uruchamianie aplikacji

1. Upewnij się, że masz zainstalowaną Javę 24 i Maven
2. Sklonuj repozytorium
3. Przejdź do katalogu projektu
4. Uruchom aplikację poleceniem: `mvn spring-boot:run`
5. Otwórz przeglądarkę i przejdź pod adres: `http://localhost:8080/app`

## Endpointy API

### Pytania
- `GET /api/questions` - pobierz wszystkie pytania
- `GET /api/questions/{id}` - pobierz pytanie o określonym ID
- `POST /api/questions` - dodaj nowe pytanie
- `PUT /api/questions/{id}` - zaktualizuj pytanie
- `DELETE /api/questions/{id}` - usuń pytanie

### Głosowanie
- `POST /api/questions/{id}/vote/yes` - głosuj "Tak" na pytanie
- `POST /api/questions/{id}/vote/no` - głosuj "Nie" na pytanie

## Struktura bazy danych

### Tabela `questions`
- `id` - klucz główny, identyfikator pytania
- `name` - nazwa pytania
- `description` - opis pytania
- `createtime` - data i czas utworzenia
- `endtime` - termin zakończenia (opcjonalnie)
- `yesvote` - liczba głosów "Tak"
- `novote` - liczba głosów "Nie"

### Tabela `votes`
- `id` - klucz główny, identyfikator głosu
- `question_id` - klucz obcy do tabeli questions
- `ip_address` - adres IP głosującego
- `vote_type` - typ głosu (true = Tak, false = Nie)
