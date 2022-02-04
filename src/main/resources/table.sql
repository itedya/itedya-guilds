create table if not exists guilds (
    id int primary key auto_increment,
    name varchar(255) not null unique,
    short_name varchar(255) not null unique
);

create table if not exists guild_members (
    player_uuid varchar(255) not null unique,
    guild_id int not null
);

create table if not exists guild_homes (
    id int primary key auto_increment,
    x int not null,
    y int not null,
    z int not null
);