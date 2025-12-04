DECLARE
    v_cnt NUMBER;
    v_nullable VARCHAR2(1);
BEGIN
    SELECT COUNT(*) INTO v_cnt FROM user_tab_cols WHERE table_name = 'VIDEO_FILE' AND column_name = 'DATA';
    IF v_cnt > 0 THEN
        SELECT nullable INTO v_nullable FROM user_tab_cols WHERE table_name = 'VIDEO_FILE' AND column_name = 'DATA';
        IF v_nullable = 'N' THEN
            EXECUTE IMMEDIATE 'ALTER TABLE VIDEO_FILE MODIFY (DATA NULL)';
        END IF;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('V5__relax_video_file_data_column failed: ' || SQLERRM);
        RAISE;
END;
/
