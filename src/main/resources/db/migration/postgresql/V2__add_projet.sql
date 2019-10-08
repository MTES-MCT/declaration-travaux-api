create table projets (
    adresse_bp varchar(255), 
    adresse_cedex varchar(255), 
    code_postal varchar(255) not null, 
    lieu_dit varchar(255), 
    adresse_numero varchar(255), 
    adresse_voie varchar(255), 
    construction_nouvelle boolean not null, 
    lotissement boolean not null,
    parcelles_cadastrales varchar(255) not null, 
    dossier_id bigint not null, 
    primary key (dossier_id)
);
alter table projets add constraint FK8vojf2o8m8xowtu6h2t0iqxg7 foreign key (dossier_id) references dossiers