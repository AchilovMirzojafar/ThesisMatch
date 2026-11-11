-- Optimistic Locking für das Aggregat Betreuer (verhindert Lost Updates)
ALTER TABLE betreuer ADD COLUMN version INTEGER NOT NULL DEFAULT 0;

-- Die GitHub-ID ist die fachliche Kennung und muss eindeutig sein
ALTER TABLE betreuer ALTER COLUMN fach_id SET NOT NULL;
ALTER TABLE betreuer ADD CONSTRAINT uq_betreuer_fach_id UNIQUE (fach_id);

-- Pflichtfelder und Länge passend zur Validierung im Code
ALTER TABLE betreuer ALTER COLUMN titel SET NOT NULL;
ALTER TABLE betreuer ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE betreuer ALTER COLUMN last_name SET NOT NULL;
ALTER TABLE betreuer ALTER COLUMN email TYPE VARCHAR(254);
ALTER TABLE betreuer ALTER COLUMN email SET NOT NULL;

-- Tags liegen in der Tabelle betreuer_tag, diese Spalte wurde nie benutzt
ALTER TABLE betreuer DROP COLUMN tags;

-- Im Veranstaltungskatalog stand "Programmierpraktikum 1 " mit Leerzeichen am Ende
UPDATE thema_requirement SET requirement = TRIM(requirement) WHERE requirement <> TRIM(requirement);
