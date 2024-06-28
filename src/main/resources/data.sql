insert into MPA (NAME) select 'G' x where not exists(select * from MPA WHERE NAME = 'G');
insert into MPA (NAME) select 'PG' x where not exists(select * from MPA WHERE NAME = 'PG');
insert into MPA (NAME) select 'PG-13' x where not exists(select * from MPA WHERE NAME = 'PG-13');
insert into MPA (NAME) select 'R' x where not exists(select * from MPA WHERE NAME = 'R');
insert into MPA (NAME) select 'NC-17' x where not exists(select * from MPA WHERE NAME = 'NC-17');


insert into GENRES (NAME) select 'Комедия' x where not exists(select * from GENRES WHERE NAME = 'Комедия');
insert into GENRES (NAME) select 'Драма' x where not exists(select * from GENRES WHERE NAME = 'Драма');
insert into GENRES (NAME) select 'Мультфильм' x where not exists(select * from GENRES WHERE NAME = 'Мультфильм');
insert into GENRES (NAME) select 'Триллер' x where not exists(select * from GENRES WHERE NAME = 'Триллер');
insert into GENRES (NAME) select 'Документальный' x where not exists(select * from GENRES WHERE NAME = 'Документальный');
insert into GENRES (NAME) select 'Боевик' x where not exists(select * from GENRES WHERE NAME = 'Боевик');