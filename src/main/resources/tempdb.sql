create table if not exists `tb_play_item`
(
    `id`           VARCHAR(64)   not null primary key,
    `groupTitle`   VARCHAR(255)  not null default '默认分组',
    `channelTitle` VARCHAR(255)  not null,
    `duration`     INT(6)        not null default -1,
    `tvgId`        VARCHAR(255),
    `tvgName`      VARCHAR(255),
    `tvgLogo`      VARCHAR(255),
    `aspectRatio`  VARCHAR(255),
    `mediaUrl`     VARCHAR(1023) not null
);
