create table statuts (
    id bigint not null, 
    date_debut timestamp, 
    statut varchar(14) not null, 
    dossier_id bigint not null, 
    primary key (id)
);
alter table statuts add constraint FKt8jfrvxpugd7h6m7xrtwevthr foreign key (dossier_id) references dossiers;
update dossiers set statut = 'DPMI' where statut = 'DP';
insert into statuts (statut, date_debut, dossier_id) select statut, date_depot as date_debut, id as dossier_id from dossiers;
alter table dossiers drop COLUMN statut;
alter table dossiers drop COLUMN date_depot;
create table messages (
    id bigint not null, 
    auteur_email varchar(255), 
    auteur_id varchar(255), 
    contenu varchar(255), 
    date timestamp not null, 
    dossier_id bigint not null, 
    primary key (id));
alter table messages add constraint FK9t5hh88hmt4enw1jiufgu0vh5 foreign key (dossier_id) references dossiers;