DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM user_tab_cols
    WHERE table_name = 'USER_ANALYSIS_SETTINGS'
      AND column_name = 'NOTIFICATION_OPTION';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE user_analysis_settings ADD (notification_option VARCHAR2(32) DEFAULT ''off'')';
    END IF;

    SELECT COUNT(*)
    INTO v_count
    FROM user_tab_cols
    WHERE table_name = 'USER_ANALYSIS_SETTINGS'
      AND column_name = 'UPDATED_AT';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE user_analysis_settings ADD (updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)';
    END IF;

    EXECUTE IMMEDIATE 'UPDATE user_analysis_settings
                       SET notification_option = NVL(notification_option, ''off''),
                           updated_at = NVL(updated_at, CURRENT_TIMESTAMP)';
END;
/ 
