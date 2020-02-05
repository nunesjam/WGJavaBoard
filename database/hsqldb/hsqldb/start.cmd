@ECHO OFF
set var= java -classpath lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:hsqldb/WeeGeeDB --weegeedeebee.0

echo %var%
pause