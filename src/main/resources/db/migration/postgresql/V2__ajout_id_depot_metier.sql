CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;
ALTER TABLE dossier ALTER COLUMN id TYPE BIGINT USING id::bigint;
ALTER TABLE dossier ALTER COLUMN etat TYPE VARCHAR(11);
ALTER TABLE dossier ALTER COLUMN type TYPE VARCHAR(8);
ALTER TABLE dossier ALTER COLUMN date TYPE TIMESTAMP;
ALTER TABLE dossier ADD COLUMN no_national VARCHAR(255) NOT NULL;
ALTER TABLE dossier ADD CONSTRAINT UK_hm6srhe12ep3m2shbbnhhic1g unique (no_national);