/* Michael Clayton 2014 */
heLp TableS         ;           

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
    fname2  char(40),
    PRIMARY KEY(fidentification)
);

CREATE TABLE TABLE_TEST2 (
    fid Char(256),
    PRIMARY KEY(fid)
);

CREATE TABLE TABLE_WITH_NO_ERRORS (
fid     int                 CHECK (fid > 100),
fname   char(50)         CHECK (fname='Michael' AND fname like 'wow'),
deptid  int                 CHECK (deptid <= 50),
FOREIGN KEY(deptid) REFERENCES TABLE_TEST(fidentification),
FOREIGN KEY(fname)    REFERENCES TABLE_TEST(fname2),
PRIMARY KEY(fid)
);

DROP TABLE TABLE_WITH_NO_ERRORS;

HELP tables;

 help DESCRIBE TABLE_TEST2;
