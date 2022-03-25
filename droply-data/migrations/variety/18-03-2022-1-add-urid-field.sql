-- liquibase formatted sql
-- changeset bularond:1
create table droply_user_id_urid
(
    urid           int4 UNIQUE default null,
    droply_user_id int8 not null,
    primary key (droply_user_id)
);
alter table droply_user_id_urid
    add constraint droply_user_id_urid_constraint
        foreign key (droply_user_id) references droply_user;

CREATE OR REPLACE PROCEDURE make_random_code(user_id int8)
AS
'
    DECLARE
        new_code int4 := 0;
    BEGIN
        DELETE FROM droply_user_id_urid WHERE droply_user_id = user_id;
        LOOP
            new_code := (RANDOM() * 1000 * 1000 * 1000)::int4;
            BEGIN
                INSERT INTO droply_user_id_urid (urid, droply_user_id)
                VALUES (new_code, user_id);
                EXIT;
            EXCEPTION
                WHEN unique_violation THEN
                WHEN others THEN EXIT;
            END;
        END LOOP;
    END;
' LANGUAGE plpgsql;
