# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table event (
  id                        bigint not null,
  title                     varchar(255),
  all_day                   boolean,
  start                     timestamp,
  end                       timestamp,
  ends_same_day             boolean,
  constraint pk_event primary key (id))
;

create sequence event_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists event;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists event_seq;

