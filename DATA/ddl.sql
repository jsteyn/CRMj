
CREATE TABLE organisations (
	name VARCHAR,
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	addressline1 VARCHAR,
	addressline2 VARCHAR,
	addressline3 VARCHAR,
	city VARCHAR,
	county VARCHAR,
	postcode VARCHAR
, country VARCHAR);



CREATE TABLE people (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	lastname VARCHAR,
	middlenames VARCHAR,
	firstname VARCHAR,
	title VARCHAR
, social_media INTEGER);



CREATE TABLE project (
	project_id INTEGER PRIMARY KEY AUTOINCREMENT,
	project_name VARCHAR,
	startdate VARCHAR,
	stage_id INTEGER,
	close_date VARCHAR
);



CREATE TABLE social_media (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR
);



CREATE TABLE people_social_media (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	person_id INTEGER,
	social_media_id INTEGER,
	username VARCHAR
);



CREATE TABLE people_addresses (
	person VARCHAR,
	addressline1 VARCHAR,
	addressline2 VARCHAR,
	addressline3 VARCHAR,
	city VARCHAR,
	county VARCHAR,
	postcode VARCHAR,
	country VARCHAR,
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	CONSTRAINT people_addresses_FK FOREIGN KEY (id) REFERENCES people(id) ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE people_organisations (
	person INTEGER,
	organisation INTEGER,
	telephone VARCHAR,
	address VARCHAR,
	id INTEGER PRIMARY KEY AUTOINCREMENT, department VARCHAR, "position" VARCHAR,
	CONSTRAINT people_organisations_FK FOREIGN KEY (organisation) REFERENCES organisations(id),
	CONSTRAINT people_organisations_FK_1 FOREIGN KEY (person) REFERENCES people(id)
);



CREATE TABLE people_telephones (
	person_id INTEGER,
	"number" VARCHAR,
	numbername VARCHAR,
	CONSTRAINT people_telephones_FK FOREIGN KEY (person_id) REFERENCES people(id)
);
