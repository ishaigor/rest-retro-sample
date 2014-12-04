CREATE SCHEMA java_one_2014 COLLATE UTF8_GENERAL_CI;


CREATE USER java_one;

GRANT ALL PRIVILEGES ON java_one_2014.* TO 'java_one'@'%' IDENTIFIED BY '';

use java_one_2014;

CREATE TABLE users(
	username varchar(64) NOT NULL PRIMARY KEY,
	password varchar(500) NOT NULL,
	enabled boolean NOT NULL DEFAULT TRUE);


CREATE TABLE authorities (
	username varchar(64) NOT NULL,
	authority varchar(50) NOT NULL,
	CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username));


CREATE UNIQUE INDEX ix_auth_username ON authorities (username,authority);


CREATE TABLE groups (
	id bigint AUTO_INCREMENT PRIMARY KEY,
	group_name varchar(50) NOT NULL UNIQUE);


CREATE TABLE group_authorities (
	group_id bigint NOT NULL,
	authority varchar(50) NOT NULL,
	CONSTRAINT fk_group_authorities_group FOREIGN KEY(group_id) REFERENCES groups(id));


CREATE TABLE group_members (
	id bigint AUTO_INCREMENT PRIMARY KEY,
	username varchar(64) NOT NULL,group_id bigint NOT NULL,
	CONSTRAINT fk_group_members_group FOREIGN KEY(group_id) REFERENCES groups(id));


INSERT INTO users (username, password)
VALUES('Ashlie', '6e22be5a7ca6342a8981ff9e9365c7b27d2dde9a48ad47f80d5969182486c31632fc1588a733e025'); -- 123456


INSERT INTO authorities
VALUES('Ashlie', 'USER');

INSERT INTO authorities
VALUES('Ashlie', 'MAX_WORDS_100');

INSERT INTO users (username, password)
VALUES('Cameron', '6e22be5a7ca6342a8981ff9e9365c7b27d2dde9a48ad47f80d5969182486c31632fc1588a733e025'); -- 123456

INSERT INTO authorities
VALUES('Cameron', 'USER');

INSERT INTO authorities
VALUES('Cameron', 'WORDS_DEMO');


INSERT INTO users (username, password)
VALUES('Juhana', '6e22be5a7ca6342a8981ff9e9365c7b27d2dde9a48ad47f80d5969182486c31632fc1588a733e025'); -- 123456

INSERT INTO authorities
VALUES('Juhana', 'USER');

INSERT INTO authorities
VALUES('Juhana', 'WORDS_PRODUCTION');


INSERT INTO groups
VALUES(1, 'OPERATIONS');


INSERT INTO group_authorities
VALUES(1, 'DBA');


INSERT INTO group_members
VALUES(1, 'Ashlie', 1);

create table oauth_refresh_token (
  token_id VARCHAR(32) PRIMARY KEY,
  token BLOB  NOT NULL,
  authentication BLOB  NOT NULL,
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
) ;

create table oauth_access_token (
  token_id VARCHAR(32) PRIMARY KEY NOT NULL,
  token BLOB  NOT NULL,
  authentication_id VARCHAR(256)  NOT NULL,
  user_name VARCHAR(64)  NOT NULL,
  client_id VARCHAR(36)  NOT NULL,
  authentication BLOB  NOT NULL,
  refresh_token VARCHAR(32)  NULL COMMENT 'token id of the refresh token in oauth_refresh_token table (optional)',
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  INDEX idx_oauth_access_token_authentication_id (authentication_id(36)),
  INDEX idx_oauth_access_token_user_name (user_name),
  INDEX idx_oauth_access_token_client_id(client_id),	  
  INDEX idx_oauth_access_token_refresh_token(refresh_token)	  
) ;

create table oauth_client_details (
  client_id VARCHAR(36) PRIMARY KEY COMMENT 'Use UUID()',
  resource_ids VARCHAR(256) COMMENT 'Comma delimited list',
  client_secret VARCHAR(36) NOT NULL COMMENT 'Use UUID()',
  scope VARCHAR(256) NOT NULL COMMENT 'Comma delimited list',
  authorized_grant_types VARCHAR(256) NOT NULL COMMENT 'Comma delimited list: supported grant types are client_credentials, authorization_code, refresh_token, implicit_grant and password',
  web_server_redirect_uri VARCHAR(256) COMMENT 'Comma delimited list',
  authorities VARCHAR(256) COMMENT 'Comma delimited list',
  access_token_validity INTEGER COMMENT 'Seconds, if not specified will be 1860 (31 min)?',
  refresh_token_validity INTEGER COMMENT 'Seconds, if not specified will be 2700 (45 min)?',
  additional_information VARCHAR(4096) COMMENT 'JSON map of key/value pairs',
  autoapprove VARCHAR(256) COMMENT 'Comma delimited list: auto approval scopes', 
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ;

create table oauth_client_token (
  token_id VARCHAR(36) PRIMARY KEY,
  token BLOB NOT NULL,
  authentication_id VARCHAR(256) NOT NULL,
  user_name VARCHAR(64) NOT NULL,
  client_id VARCHAR(36) NOT NULL COMMENT 'The client record is found in oauth_client_details', 
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  INDEX idx_oauth_client_token_authentication_id (authentication_id(36)),
  INDEX idx_oauth_client_token_user_name (user_name),
  INDEX idx_oauth_client_token_client_id(client_id)
) ;


create table oauth_code (
  code VARCHAR(36) PRIMARY KEY, 
  authentication BLOB NOT NULL,
  create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
) ;

create table oauth_approvals (
	userId VARCHAR(64),
	clientId VARCHAR(36),
	scope VARCHAR(256),
	status VARCHAR(10),
	expiresAt TIMESTAMP,
	lastModifiedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_oauth_approvals_userId_clientId (userId,clientId),
	INDEX idx_oauth_approvals_userId_clientId_scope (userId,clientId,scope(255))
);

INSERT INTO oauth_client_details
VALUES(
'35221af8-39ee-11e4-9346-bb86709fcb1d', 
'rest-retro', 
'3c0dfe9a-39ee-11e4-9346-bb86709fcb1d',
'words',
 'password', 
null,
null,
1800,
2700,
null,
'words',
now(),
now());

INSERT INTO oauth_client_details
VALUES(
'42be4a10-39ee-11e4-9346-bb86709fcb1d', 
'rest-retro', 
'ff02f49a-4052-11e4-9346-bb86709fcb1d',
'words',
 'authorization_code', 
'http://localhost:8080/secure/idp/client/v1.0/oauth/redirect',
null,
1800,
2700,
null,
'words',
now(),
now());

INSERT INTO oauth_client_details
VALUES(
'b1c1c908-7b68-11e4-8f7a-de10b3517e85', 
'rest-retro-client', 
'6e22be5a7ca6342a8981ff9e9365c7b27d2dde9a48ad47f80d5969182486c31632fc1588a733e025',
'clients',
 'client_credentials', 
null,
null,
86400,
null,
null,
null,
now(),
now());

select uuid();