-- liquibase formatted sql
-- changeset bularond:1
CREATE OR REPLACE PROCEDURE make_random_code(user_id int8)
AS
'
    DECLARE
        new_code int4 := 0;
    BEGIN
        DELETE FROM droply_user_id_urid WHERE droply_user_id = user_id;
        LOOP
            new_code := (RANDOM() * 900 * 1000 * 1000 + 100 * 1000 * 1000)::int4;
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
