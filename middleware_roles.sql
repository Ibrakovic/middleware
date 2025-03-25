-- ==========================
-- Benutzer anlegen
-- ==========================

-- 1. Benutzer mit Schreibrechten für die Middleware
CREATE USER middleware_user WITH PASSWORD 'middleware';

-- 2. Benutzer mit reinen Leserechten (readonly)
CREATE USER middleware_readonly WITH PASSWORD 'readonly';

-- ==========================
-- Allgemeine Rechte
-- ==========================

-- Verbindung zur Datenbank erlauben
GRANT CONNECT ON DATABASE mydatabase TO middleware_user;
GRANT CONNECT ON DATABASE mydatabase TO middleware_readonly;

-- Zugriff auf das public-Schema erlauben
GRANT USAGE ON SCHEMA public TO middleware_user;
GRANT USAGE ON SCHEMA public TO middleware_readonly;

-- ==========================
-- Rechte auf bestehende Tabellen
-- ==========================

-- Schreibrechte für middleware_user (lesen, schreiben, aktualisieren)
GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO middleware_user;

-- Nur Lesezugriff für readonly-Nutzer
GRANT SELECT ON ALL TABLES IN SCHEMA public TO middleware_readonly;

-- ==========================
-- Rechte für zukünftige Tabellen
-- ==========================

-- Schreibrechte für neue Tabellen
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE ON TABLES TO middleware_user;

-- Leserechte für neue Tabellen
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT ON TABLES TO middleware_readonly;
