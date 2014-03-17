/* Michael Clayton 2014 */

CREATE TABLE DEPARTMENT (
deptid      none            NOT NULL,
dname       varchar(50)     CHECK (dname='hi'),
location    varchar(50),
PRIMARY KEY(deptid)
);

CREATE TABLE STUDENT (
snum    int                 NOT NULL,
sname   varchar(50),
deptid  int,
slevel  varchar(50),
age     int,
FOREIGN KEY(deptid) REFERENCES DEPARTMENT(deptid),
PRIMARY KEY(snum)
);

CREATE TABLE FACULTY (
fid     int                 NOT NULL,
fname   varchar(50),
deptid  int,
FOREIGN KEY(deptid) REFERENCES DEPARTMENT(deptid),
PRIMARY KEY(fid)
);

CREATE TABLE CLASS (
cname       varchar(50)     NOT NULL,
meets_at    date,
room        varchar(50),
fid         int,
FOREIGN KEY(fid) REFERENCES FACULTY(fid),
PRIMARY KEY(cname)
);

CREATE TABLE ENROLLED(
snum    int                 NOT NULL,
cname   varchar(50)         NOT NULL,
PRIMARY KEY(snum, cname),
FOREIGN KEY(snum) REFERENCES STUDENT(snum),
FOREIGN KEY (cname) REFERENCES CLASS(cname)
);

