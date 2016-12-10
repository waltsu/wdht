CREATE TABLE objects (
  "id" SERIAL PRIMARY KEY,
  "key" varchar(255) not null UNIQUE,
  "value" varchar(255) not null
);
