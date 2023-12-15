create table if not exists `tb_play_item_available`
(
    `id`       VARCHAR(255)  not null primary key,
    `mediaUrl` VARCHAR(1023) not null
);