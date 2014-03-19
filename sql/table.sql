/* Michael Clayton 2014 */

CREATE TABLE FACULTY (
fid     int                 NOT NULL,
fname   varchar(50)         CHECK (fname='Michael' AND fname like 'wow'),
deptid  int                 UNIQUE,
UNIQUE (deptid),
FOREIGN KEY(deptid) REFERENCES DEPARTMENT(deptid),
PRIMARY KEY(fid)
);


CREATE TABLE TABLE_TEST (
    fidentification     int     CHECK (fidentification > 100),
    PRIMARY KEY(fidentification)
);

CREATE TABLE TABLE_TEST2 (
    fid int,
    PRIMARY KEY(fid)
);

CREATE TABLE TABLE_WITH_NO_ERRORS (
fid     int                 CHECK (fid > 100),
fname   varchar(50)         CHECK (fname='Michael' AND fname like 'wow'),
deptid  int                 CHECK (deptid <= 50),
FOREIGN KEY(deptid) REFERENCES TABLE_TEST(fidentification),
PRIMARY KEY(fid, deptid)
);


