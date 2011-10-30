use lunchat;

drop table if exists user;
drop table if exists health;
drop table if exists userGroup;
drop table if exists userRole;
drop table if exists shop;
drop table if exists product;
drop table if exists receiving;
drop table if exists shipping;
drop table if exists ordered;

create table if not exists user (
	id varchar(64) primary key,
	groupId varchar(128),
	password varchar(64),
	name varchar(256),
	email varchar(255) unique,
	role varchar(256)
);

drop table if exists health;
create table if not exists health (
	userId varchar(64) primary key,
	login		int,
	lastLogin timestamp,
	loginFail tinyint,
	active boolean,
	foreign key (userId) references user(id) on delete cascade
);

drop table if exists groups;
create table if not exists groups (
	id varchar(64) primary key,
	name varchar(128) not null,
	lastOreder time,
	foreign key (id) references user(id) on delete cascade
);


drop table if exists shop;
create table if not exists shop (
	id varchar(16) primary key,
	name varchar(256),
	url varchar(128),
	tel varchar(64)
);

insert into shop values ('tamagoya', '玉子屋', 'http://www.tamagoya.co.jp/menu/menu.html', '03-3754-6167');
insert into shop values ('fresh', 'フレッシュランチ', 'http://www.fl39.com/month_menu', '03-5769-0339');

drop table if exists product;
create table if not exists product (
	id varchar(32) primary key,
	refId varchar(32),
	shopId varchar(16),
	name varchar(256) not null,
	amount int,
	start timestamp,
	finish timestamp,
	foreign key (refId) references product(id) on delete cascade,
	foreign key (shopId) references shop(id) on delete cascade 
);

insert into product values ( 'Oct24Lunch', null, 'tamagoya', '10/24 ランチ', 430, '2011-10-01 00:00:00', '2011-10-24 12:00:00.0' );


drop table if exists receiving;
create table if not exists receiving (
	id int unsigned auto_increment primary key,
	productId varchar(32),
	quantity int,
	foreign key (productId) references product(id) on delete no action
);

drop table if exists shipping;
create table if not exists shipping (
	id int unsigned auto_increment primary key,
	userId varchar(64),
	productId varchar(32),
	quantity int,
	memo varchar(256),
	datetime timestamp,
	foreign key (userId) references user(id) on delete no action,
	foreign key (productId) references product(id) on delete no action
);

drop table if exists ordered;
create table if not exists ordered (
	id int unsigned auto_increment primary key,
	userId varchar(64),
	productId varchar(32),
	quantity int,
	amount int,
	datetime timestamp,
	shipping timestamp,
	foreign key (userId) references user(id) on delete no action,
	foreign key (productId) references product(id) on delete no action
);

