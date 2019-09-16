create sequence hibernate_sequence start with 1 increment by 1;
CREATE TABLE dossiers (
    id bigint not null, 
    date timestamp, 
    deposant_email varchar(255) not null, 
    deposant_id varchar(255) not null, 
    dossier_id varchar(255) not null, 
    statut varchar(11) not null, 
    type varchar(4) not null, 
    primary key (id)
);
CREATE TABLE personnes (
    id bigint not null, 
    commune_naissance varchar(255), 
    date_naissance timestamp, 
    email varchar(255) not null, 
    nom varchar(255), 
    personne_id varchar(255) not null, 
    prenom varchar(255), 
    sexe integer, 
    primary key (id)
);
create table pieces_jointes (
    id bigint not null, 
    code varchar(255) not null, 
    file_name varchar(255) not null, 
    file_type varchar(255) not null, 
    numero integer not null, 
    storage_id varchar(255) not null, 
    type integer, 
    version bigint, 
    dossier_id bigint, 
    primary key (id)
);
alter table dossiers add constraint UK_rh6rflehixaepjjnlleo7xw73 unique (dossier_id);
alter table personnes add constraint UK_ghr7qyf31bwwan3vqn7gqw7gt unique (personne_id);
alter table pieces_jointes add constraint UK_9hbfbex4yev356usdnb57lobk unique (storage_id);
alter table pieces_jointes add constraint FK1hrwjto754kbx7gbrs8qv4i8e foreign key (dossier_id) references dossiers;

