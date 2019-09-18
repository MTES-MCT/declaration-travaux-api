create sequence hibernate_sequence start with 1 increment by 1;
CREATE TABLE dossiers (
    id bigint not null, 
    date_depot timestamp, 
    deposant_email varchar(255), 
    deposant_id varchar(255), 
    dossier_id varchar(255) not null, 
    statut varchar(11) not null, 
    type varchar(4) not null, 
    primary key (id)
);
CREATE TABLE personnes (
    id bigint not null, 
    email varchar(255) not null, 
    commune_naissance varchar(255), 
    date_naissance timestamp, 
    nom varchar(255), 
    personne_id varchar(255) not null, 
    prenom varchar(255), 
    sexe varchar(5), 
    primary key (id)
);
create table pieces_jointes (
    code_numero varchar(255) not null, 
    code_type varchar(255) not null, 
    fichier_id varchar(255) not null, 
    created_on timestamp, 
    dossier_id bigint not null, 
    primary key (code_numero, code_type, dossier_id, fichier_id)
);
alter table dossiers add constraint UK_rh6rflehixaepjjnlleo7xw73 unique (dossier_id);
alter table personnes add constraint UK_ghr7qyf31bwwan3vqn7gqw7gt unique (personne_id);
alter table pieces_jointes add constraint UK_r0niblci12osx8mdlw3sf16sf unique (fichier_id);
alter table pieces_jointes add constraint FK1hrwjto754kbx7gbrs8qv4i8e foreign key (dossier_id) references dossiers;

