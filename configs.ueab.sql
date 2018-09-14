
DROP TABLE sys_configs;

CREATE TABLE sys_configs (
	sys_config_id			serial primary key,
	config_type_id			integer not null,
	config_name				varchar(254) not null unique,
	config_value			varchar(254) not null
);

INSERT INTO sys_configs (config_type_id, config_name, config_value) VALUES
(0, '1', 'Biometric configurations'),
(1, 'base_url', 'http://192.168.3.195:8795/v2'),
(1, 'domain', '192.168.3.195'),
(1, 'api_name', 'admin'),
(1, 'user_password', 'invent@2018'),
(1, 'user_name', 'admin'),
(1, 'access_group_id', '5'),
(1, 'access_group_name', 'student access'),
(1, 'user_group_id', '1030'),
(1, 'user_group_name', 'student group'),
(1, 'webdav_path', 'https://registration.ueab.ac.ke/repository/webdav/ueab/'),
(1, 'webdav_username', 'repository'),
(1, 'webdav_password', 'baraza');
