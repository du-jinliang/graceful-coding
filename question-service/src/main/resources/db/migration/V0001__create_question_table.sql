create table question
(
    id           serial primary key,
    questionerId text not null,
    title        text not null,
    detail       text
)
