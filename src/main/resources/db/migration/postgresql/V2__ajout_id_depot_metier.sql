ALTER TABLE depot ADD COLUMN id_depot varchar(255) NOT NULL;
ALTER TABLE depot ADD CONSTRAINT UK_hm6srhe12ep3m2shbbnhhic1g unique (id_depot);