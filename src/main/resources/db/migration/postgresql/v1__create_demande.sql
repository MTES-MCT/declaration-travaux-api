CREATE TABLE DEMANDE (
    id text NOT NULL,
    type text NOT NULL,
    etat text NOT NULL,
    date timestamptz NOT NULL,
    PRIMARY KEY ("id")
);