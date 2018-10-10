DROP TABLE IF EXISTS USER_API;

CREATE TABLE IF NOT EXISTS USER_API
(
  id       INT UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  user_id  INT                        NOT NULL,
  attempts INT                        NOT NULL,
  enabled  BOOLEAN                    NOT NULL DEFAULT TRUE,
  CONSTRAINT user_api_user_id_fk FOREIGN KEY (user_id) REFERENCES USER (id)
);