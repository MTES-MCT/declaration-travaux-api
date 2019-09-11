CREATE TABLE dossiers (
    id text NOT NULL,
    type text NOT NULL,
    etat text NOT NULL,
    date date NOT NULL,
    PRIMARY KEY ("id")
);
CREATE TABLE personnes_physiques (
    id text NOT NULL,
    nom text NOT NULL,
    prenom text NOT NULL,
    email text NOT NULL,
    PRIMARY KEY ("id")
);