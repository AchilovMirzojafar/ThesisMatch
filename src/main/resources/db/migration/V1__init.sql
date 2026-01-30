CREATE TABLE betreuer
(

    id SERIAL PRIMARY KEY,

    fach_id INTEGER,

    titel VARCHAR(50),

    first_name VARCHAR(50),

    last_name VARCHAR(50),

    email VARCHAR(50),

    phone VARCHAR(12)

);

CREATE TABLE betreuer_tag
(
    betreuer INTEGER NOT NULL REFERENCES betreuer(id) ON DELETE CASCADE,
    tag VARCHAR(100) NOT NULL,

    PRIMARY KEY (betreuer, tag)
);

CREATE TABLE thema
(
    id SERIAL PRIMARY KEY,

    title VARCHAR(50),
    description VARCHAR(300),

    betreuer INTEGER NOT NULL,

    CONSTRAINT fk_thema_betreuer   -- Fremdschlüssel auf Betreuer ID
        FOREIGN KEY (betreuer)
            REFERENCES betreuer(id)
            ON DELETE CASCADE
);

CREATE TABLE thema_tag
(
    thema INTEGER NOT NULL REFERENCES thema(id) ON DELETE CASCADE,
    tag VARCHAR(100) NOT NULL,

    PRIMARY KEY (thema, tag)
);

CREATE TABLE thema_requirement
(
    thema INTEGER NOT NULL REFERENCES thema(id) ON DELETE CASCADE,
    requirement VARCHAR(200) NOT NULL,

    PRIMARY KEY (thema, requirement)
);

