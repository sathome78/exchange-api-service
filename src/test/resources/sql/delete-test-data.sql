SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM USER_API ua WHERE ua.user_id = (SELECT u.id FROM USER u WHERE u.email = 'APITest@email.com');

DELETE FROM USER u WHERE u.email = 'APITest@email.com';

SET FOREIGN_KEY_CHECKS = 1;
