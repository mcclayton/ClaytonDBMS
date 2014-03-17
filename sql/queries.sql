rem Query 1
SELECT COUNT(*)
FROM FACULTY 
WHERE deptid=1;


rem Query 2
SELECT sname 
FROM STUDENT 
WHERE age>=ALL (SELECT age FROM STUDENT);


rem Query 3
SELECT DISTINCT s.sname, s.deptid, s.age
FROM STUDENT s, ENROLLED e, FACULTY f, DEPARTMENT d, CLASS c
WHERE d.dname = 'Computer Sciences'
AND f.deptid = d.deptid
AND c.fid = f.fid
AND e.cname = c.cname
AND s.snum = e.snum;


rem Query 4
SELECT sname, deptid FROM STUDENT s, ENROLLED e WHERE s.snum = e.snum 
UNION 
SELECT sname, deptid FROM STUDENT WHERE age < 20;


rem Query 5
SELECT DISTINCT snum
FROM ENROLLED e1
WHERE (SELECT COUNT(*) 
        FROM ENROLLED e2 
        WHERE e1.snum <> e2.snum 
        AND e1.cname = e2.cname) >= 1;


rem Query 6
SELECT DISTINCT fid
FROM CLASS c1
WHERE (SELECT COUNT(*)
        FROM CLASS c2
        WHERE c1.room = c2.room
        AND c1.fid = c2.fid) > 1;


rem Query 7
SELECT dname, fname
FROM DEPARTMENT d, FACULTY f
WHERE d.deptid = f.deptid
ORDER BY dname ASC;


/* 
* I am making the assumption that because ENROLLED has the Primary Key (snum, cname)
* that the query below does not need to handle a student enrolled in the same class
* twice.
*/
rem Query 8
SELECT DISTINCT snum
FROM ENROLLED e1
WHERE (SELECT COUNT(*)
        FROM ENROLLED e2
        WHERE e1.snum = e2.snum) > 1;


rem Query 9
SELECT DISTINCT fid 
FROM CLASS c, ENROLLED e
WHERE e.cname = c.cname
AND c.cname = ANY (
    SELECT cname 
    FROM ENROLLED 
    WHERE snum = (
        SELECT snum 
        FROM STUDENT 
        WHERE sname = 'E.Cho'
    )
);


rem Query 10
SELECT COUNT(*)
FROM STUDENT s 
WHERE s.age < 21
AND s.snum = ANY(
    SELECT e.snum
    FROM ENROLLED e, CLASS c
    WHERE c.cname = 'ENG400'
    AND c.cname = e.cname
);
