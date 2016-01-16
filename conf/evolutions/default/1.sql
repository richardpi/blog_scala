# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "POSTS" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(254) NOT NULL, "date" BIGINT NOT NULL,"intro" VARCHAR(2000),"content" VARCHAR(5000),"author" VARCHAR(100));

# --- !Downs

drop table "POSTS";
