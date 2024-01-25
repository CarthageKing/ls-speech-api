
    create table speech_api.speech (
        s_speech_date date not null,
        s_id varchar(64) not null,
        s_speech_text varchar(1048576) not null,
        primary key (s_id)
    );

    create table speech_api.speech_author (
        s_speech_id varchar(64) not null,
        sa_author varchar(1024) not null,
        primary key (s_speech_id, sa_author)
    );

    create table speech_api.speech_keyword (
        s_speech_id varchar(64) not null,
        sk_keyword varchar(1024) not null,
        primary key (s_speech_id, sk_keyword)
    );
