drop database twitter;
create database twitter;
use  twitter;
create table login(
user_id int AUTO_INCREMENT PRIMARY KEY,
first_name varchar(50),
email varchar(50),
password varchar(50),
picture_path varchar(350)
);

create table tweets(
	tweet_id int AUTO_INCREMENT PRIMARY KEY,
	user_id int ,
	FOREIGN KEY (user_id) REFERENCES login(user_id),
	tweet_text varchar(100),
	tweet_picture varchar(350),
	tweet_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);

describe tweets;
 
create table following (
	user_id int,
	following_user_id int,
	FOREIGN KEY (user_id) REFERENCES login(user_id),
	FOREIGN KEY (following_user_id) REFERENCES login(user_id)
);

describe following;

create view user_tweets as 

	select t.user_id,t.tweet_id,t.tweet_text, t.tweet_date, t.tweet_picture, l.first_name,l.picture_path
	from tweets t inner join login l on t.user_id=l.user_id;

describe user_tweets;