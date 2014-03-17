rem Query 5
SELECT DISTINCT snum, cname, michael
FROM ENROLLED e1, STUDENT s1
WHERE (SELECT COUNT(*) 
        FROM ENROLLED e2 
        WHERE e1.snum <> e2.snum 
        AND e1.cname = e2.cname) >= 1
UNION
SELECT DISTINCT snum, cname, michael
FROM ENROLLED e1, STUDENT s1
WHERE (SELECT COUNT(*) 
        FROM ENROLLED e2 
        WHERE e1.snum <> e2.snum 
        AND e1.cname = e2.cname) >= 1
UNION
SELECT DISTINCT snum, cname, michael
FROM ENROLLED e1, STUDENT s1
WHERE (SELECT COUNT(*) 
        FROM ENROLLED e2 
        WHERE e1.snum <> e2.snum 
        AND e1.cname = e2.cname) >= 1;