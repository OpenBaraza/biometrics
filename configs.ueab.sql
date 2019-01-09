
DROP TABLE sys_configs;

CREATE TABLE sys_configs (
	sys_config_id			serial primary key,
	config_type_id			integer not null,
	config_name				varchar(254) not null unique,
	config_value			varchar(254) not null
);

GRANT ALL ON sys_configs TO biometrics;

INSERT INTO sys_configs (config_type_id, config_name, config_value) VALUES
(0, '1', 'Biometric configurations'),
(1, 'base_url', 'http://192.168.3.10:8795/v2'),
(1, 'domain', '192.168.3.10'),
(1, 'api_name', 'admin'),
(1, 'user_password', 'invent@2018'),
(1, 'user_name', 'admin'),
(1, 'access_group_id', '2'),
(1, 'access_group_name', 'student group'),
(1, 'user_group_id', '1003'),
(1, 'user_group_name', 'student group'),
(1, 'webdav_path', 'http://registration.ueab.ac.ke/repository/webdav/ueab/'),
(1, 'webdav_username', 'repository'),
(1, 'webdav_password', 'baraza');


--- Add then new users
CREATE ROLE biometrics NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
CREATE USER bio1 WITH PASSWORD 'Pass:bio1';
CREATE USER bio2 WITH PASSWORD 'Pass:bio2';
CREATE USER bio3 WITH PASSWORD 'Pass:bio3';
CREATE USER bio4 WITH PASSWORD 'Pass:bio4';
GRANT biometrics TO bio1;
GRANT biometrics TO bio2;
GRANT biometrics TO bio3;
GRANT biometrics TO bio4;
GRANT ALL PRIVILEGES ON sys_configs TO biometrics;
GRANT ALL PRIVILEGES ON students TO biometrics;
GRANT ALL PRIVILEGES ON studentdegrees TO biometrics;
GRANT ALL PRIVILEGES ON qstudents TO biometrics;
GRANT ALL PRIVILEGES ON entitys TO biometrics;
GRANT ALL PRIVILEGES ON quarters TO biometrics;
GRANT ALL PRIVILEGES ON studentdegreeview TO biometrics;




