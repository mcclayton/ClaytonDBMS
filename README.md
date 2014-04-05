ClaytonDBMS
===========

Author: Michael Clayton

A mini-database management system I made for CS448.

HOW TO RUN:
-----------
The runnable jar file is in the location ClaytonDBMS/executable/minidbms.jar  
Navigate to the directory ClaytonDBMS/executable and run one of the following command forms.  
    INTERACTIVE MODE:   Run command: 'java -jar ./minidbms.jar username'  
    BATCH MODE:         Run command: 'java -jar ./minidbms.jar username < inputBatchFile.sql'

PERSISTING DATA INFO:
---------------------  
When the user uses the 'QUIT;' command, the database data is stored in ClaytonDBMS/executable/database.data  
Remove this file to clear the persisted data.

GENERAL INFO
------------  
1. It should be noted that I used the GeneralSQLParser library as the basis of my SQL parsing (which I was told was ok to do).  
2. 2. Table/attribute names are case sensitive.
