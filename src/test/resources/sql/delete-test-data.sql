SET FOREIGN_KEY_CHECKS=0;
DELETE FROM `user_api` WHERE user_id = (SELECT id FROM `user` WHERE email = 'APITest@email.com');
DELETE FROM `user` WHERE email = 'APITest@email.com';
SET FOREIGN_KEY_CHECKS=1;
