
    alter table DBGroupResults_Results 
        drop constraint if exists FK_aevegygxfhmq39x0f9rey4ldj;
    alter table lag.public.DBAssembly_districtList 
        drop constraint if exists FK_m7jb784s4jjmisy9qss3omgmv;
    alter table lag.public.DBAssembly_districtList 
        drop constraint if exists FK_i4ndorr543f640f2r5m6vwxn3;
    alter table lag.public.DBAssembly_groupInfoMap 
        drop constraint if exists FK_aga9de8numrkiljshgnixjvqx;
    alter table lag.public.DBAssembly_groupInfoMap 
        drop constraint if exists FK_m14kccfdk6ekucm591i0kmgj3;
    alter table lag.public.DBAssembly_groupInfoMap 
        drop constraint if exists FK_e3x7ct2iyegt9w08j0vq8mlte;
    alter table lag.public.DBAssembly_groupResultsMap 
        drop constraint if exists FK_mu3dc10vl8i6g62eokcmufkds;
    alter table lag.public.DBAssembly_groupResultsMap 
        drop constraint if exists FK_cm2r0250hys0nelwneck4d4fx;
    alter table lag.public.DBAssembly_groupResultsMap 
        drop constraint if exists FK_q03btcrppy8am0oh1thm5tq71;
    alter table lag.public.DBDistrict_groupResultsMap 
        drop constraint if exists FK_7yjdg1ekllqcem1msm68wygam;
    alter table lag.public.DBDistrict_groupResultsMap 
        drop constraint if exists FK_qpk1m5o4rqjeevamrfhmw3vtr;
    alter table lag.public.DBDistrict_groupResultsMap 
        drop constraint if exists FK_2sow4ye0m6t3llx3h1fh1pu5l;
    alter table lag.public.DBDistrict_legislators 
        drop constraint if exists FK_gcglx7bc2bha3driqmy4f33g6;
    alter table lag.public.DBDistrict_legislators 
        drop constraint if exists FK_a4omrinmxoff1tvh1fp5mxx8d;
    alter table lag.public.DBGroupInfo_GroupItems 
        drop constraint if exists FK_gf185iy4kvsjano69glgvg4ee;
    alter table lag.public.DBGroupInfo_GroupItems 
        drop constraint if exists FK_jgiah9g0gv9y6qqinax45samy;
    alter table lag.public.DBLegislator_groupResultsMap 
        drop constraint if exists FK_7nk0q9fot3ahn8g2hnny6gmvm;
    alter table lag.public.DBLegislator_groupResultsMap 
        drop constraint if exists FK_60gos6gbbj9lleb9152p5c07q;
    alter table lag.public.DBLegislator_groupResultsMap 
        drop constraint if exists FK_9vlkjla0bq7iv3j87acqaqxl6;
    drop table DBGroupResults_Results cascade;
    drop table lag.public.DBAssembly cascade;
    drop table lag.public.DBAssembly_districtList cascade;
    drop table lag.public.DBAssembly_groupInfoMap cascade;
    drop table lag.public.DBAssembly_groupResultsMap cascade;
    drop table lag.public.DBDistrict cascade;
    drop table lag.public.DBDistrict_groupResultsMap cascade;
    drop table lag.public.DBDistrict_legislators cascade;
    drop table lag.public.DBGroup cascade;
    drop table lag.public.DBGroupInfo cascade;
    drop table lag.public.DBGroupInfo_GroupItems cascade;
    drop table lag.public.DBGroupResults cascade;
    drop table lag.public.DBInfoItem cascade;
    drop table lag.public.DBLegislator cascade;
    drop table lag.public.DBLegislator_groupResultsMap cascade;
    drop sequence hibernate_sequence;
    create table DBGroupResults_Results (
        DBGroupResults_id int8 not null,
        error varchar(255),
        value varchar(255),
        Results_ORDER int4 not null,
        primary key (DBGroupResults_id, Results_ORDER)
    );
    create table lag.public.DBAssembly (
        id int8 not null,
        session varchar(255),
        state varchar(255),
        primary key (id)
    );
    create table lag.public.DBAssembly_districtList (
        DBAssembly int8 not null,
        districtList_id int8 not null
    );
    create table lag.public.DBAssembly_groupInfoMap (
        DBAssembly int8 not null,
        DBGroupInfo int8 not null,
        DBGroup int8 not null,
        primary key (DBAssembly, DBGroup)
    );
    create table lag.public.DBAssembly_groupResultsMap (
        DBAssembly int8 not null,
        DBGroupResults int8 not null,
        DBGroup int8 not null,
        primary key (DBAssembly, DBGroup)
    );
    create table lag.public.DBDistrict (
        id int8 not null,
        chamber int4,
        description varchar(255),
        district varchar(3),
        primary key (id)
    );
    create table lag.public.DBDistrict_groupResultsMap (
        DBDistrict int8 not null,
        DBGroupResults int8 not null,
        DBGroup int8 not null,
        primary key (DBDistrict, DBGroup)
    );
    create table lag.public.DBDistrict_legislators (
        DBDistrict int8 not null,
        legislators_id int8 not null
    );
    create table lag.public.DBGroup (
        id int8 not null,
        groupDescription varchar(255),
        groupName varchar(255),
        primary key (id)
    );
    create table lag.public.DBGroupInfo (
        id int8 not null,
        primary key (id)
    );
    create table lag.public.DBGroupInfo_GroupItems (
        DBGroupInfo int8 not null,
        GroupItems_id int8 not null,
        GroupItems_ORDER int4 not null,
        primary key (DBGroupInfo, GroupItems_ORDER)
    );
    create table lag.public.DBGroupResults (
        id int8 not null,
        primary key (id)
    );
    create table lag.public.DBInfoItem (
        id int8 not null,
        Label varchar(255),
        description varchar(1023),
        primary key (id)
    );
    create table lag.public.DBLegislator (
        id int8 not null,
        endDate date,
        name varchar(255),
        party varchar(255),
        startDate date,
        primary key (id)
    );
    create table lag.public.DBLegislator_groupResultsMap (
        DBLegislator int8 not null,
        DBGroupResults int8 not null,
        DBGroup int8 not null,
        primary key (DBLegislator, DBGroup)
    );
    alter table lag.public.DBAssembly_districtList 
        add constraint UK_m7jb784s4jjmisy9qss3omgmv  unique (districtList_id);
    alter table lag.public.DBAssembly_groupInfoMap 
        add constraint UK_aga9de8numrkiljshgnixjvqx  unique (DBGroupInfo);
    alter table lag.public.DBAssembly_groupResultsMap 
        add constraint UK_mu3dc10vl8i6g62eokcmufkds  unique (DBGroupResults);
    alter table lag.public.DBDistrict_groupResultsMap 
        add constraint UK_7yjdg1ekllqcem1msm68wygam  unique (DBGroupResults);
    alter table lag.public.DBDistrict_legislators 
        add constraint UK_gcglx7bc2bha3driqmy4f33g6  unique (legislators_id);
    alter table lag.public.DBGroup 
        add constraint UK_fnj4ivbsm7v9e9b4q29g7k9vk  unique (groupName);
    alter table lag.public.DBGroupInfo_GroupItems 
        add constraint UK_gf185iy4kvsjano69glgvg4ee  unique (GroupItems_id);
    alter table lag.public.DBLegislator_groupResultsMap 
        add constraint UK_7nk0q9fot3ahn8g2hnny6gmvm  unique (DBGroupResults);
    alter table DBGroupResults_Results 
        add constraint FK_aevegygxfhmq39x0f9rey4ldj 
        foreign key (DBGroupResults_id) 
        references lag.public.DBGroupResults;
    alter table lag.public.DBAssembly_districtList 
        add constraint FK_m7jb784s4jjmisy9qss3omgmv 
        foreign key (districtList_id) 
        references lag.public.DBDistrict;
    alter table lag.public.DBAssembly_districtList 
        add constraint FK_i4ndorr543f640f2r5m6vwxn3 
        foreign key (DBAssembly) 
        references lag.public.DBAssembly;
    alter table lag.public.DBAssembly_groupInfoMap 
        add constraint FK_aga9de8numrkiljshgnixjvqx 
        foreign key (DBGroupInfo) 
        references lag.public.DBGroupInfo;
    alter table lag.public.DBAssembly_groupInfoMap 
        add constraint FK_m14kccfdk6ekucm591i0kmgj3 
        foreign key (DBGroup) 
        references lag.public.DBGroup;
    alter table lag.public.DBAssembly_groupInfoMap 
        add constraint FK_e3x7ct2iyegt9w08j0vq8mlte 
        foreign key (DBAssembly) 
        references lag.public.DBAssembly;
    alter table lag.public.DBAssembly_groupResultsMap 
        add constraint FK_mu3dc10vl8i6g62eokcmufkds 
        foreign key (DBGroupResults) 
        references lag.public.DBGroupResults;
    alter table lag.public.DBAssembly_groupResultsMap 
        add constraint FK_cm2r0250hys0nelwneck4d4fx 
        foreign key (DBGroup) 
        references lag.public.DBGroup;
    alter table lag.public.DBAssembly_groupResultsMap 
        add constraint FK_q03btcrppy8am0oh1thm5tq71 
        foreign key (DBAssembly) 
        references lag.public.DBAssembly;
    alter table lag.public.DBDistrict_groupResultsMap 
        add constraint FK_7yjdg1ekllqcem1msm68wygam 
        foreign key (DBGroupResults) 
        references lag.public.DBGroupResults;
    alter table lag.public.DBDistrict_groupResultsMap 
        add constraint FK_qpk1m5o4rqjeevamrfhmw3vtr 
        foreign key (DBGroup) 
        references lag.public.DBGroup;
    alter table lag.public.DBDistrict_groupResultsMap 
        add constraint FK_2sow4ye0m6t3llx3h1fh1pu5l 
        foreign key (DBDistrict) 
        references lag.public.DBDistrict;
    alter table lag.public.DBDistrict_legislators 
        add constraint FK_gcglx7bc2bha3driqmy4f33g6 
        foreign key (legislators_id) 
        references lag.public.DBLegislator;
    alter table lag.public.DBDistrict_legislators 
        add constraint FK_a4omrinmxoff1tvh1fp5mxx8d 
        foreign key (DBDistrict) 
        references lag.public.DBDistrict;
    alter table lag.public.DBGroupInfo_GroupItems 
        add constraint FK_gf185iy4kvsjano69glgvg4ee 
        foreign key (GroupItems_id) 
        references lag.public.DBInfoItem;
    alter table lag.public.DBGroupInfo_GroupItems 
        add constraint FK_jgiah9g0gv9y6qqinax45samy 
        foreign key (DBGroupInfo) 
        references lag.public.DBGroupInfo;
    alter table lag.public.DBLegislator_groupResultsMap 
        add constraint FK_7nk0q9fot3ahn8g2hnny6gmvm 
        foreign key (DBGroupResults) 
        references lag.public.DBGroupResults;
    alter table lag.public.DBLegislator_groupResultsMap 
        add constraint FK_60gos6gbbj9lleb9152p5c07q 
        foreign key (DBGroup) 
        references lag.public.DBGroup;
    alter table lag.public.DBLegislator_groupResultsMap 
        add constraint FK_9vlkjla0bq7iv3j87acqaqxl6 
        foreign key (DBLegislator) 
        references lag.public.DBLegislator;
    create sequence hibernate_sequence;