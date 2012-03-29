# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table event (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  all_day                   tinyint(1) default 0,
  start                     datetime,
  end                       datetime,
  ends_same_day             tinyint(1) default 0,
  constraint pk_event primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table event;

SET FOREIGN_KEY_CHECKS=1;

