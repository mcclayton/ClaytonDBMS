/* Michael Clayton 2014 */

CREATE TABLE FACULTY (
fid     int                 NOT NULL,
fname   varchar(50)         CHECK (fname='Michael'),
deptid  int,
FOREIGN KEY(deptid) REFERENCES DEPARTMENT(deptid),
PRIMARY KEY(fid)
);
