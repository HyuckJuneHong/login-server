DROP TABLE IF EXISTS memberships;

CREATE TABLE memberships
(
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    name   VARCHAR(30)  NOT NULL,
    reg_no VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

INSERT INTO memberships(name, reg_no)
VALUES ('홍도산', 'KqnDzu6g7V6H2P/eokkFeQ==');
