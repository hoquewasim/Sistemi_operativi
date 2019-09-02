@echo off
echo Test Client RMI
cd ..
echo Attiva client su %1
echo Battere RETURN per eseguire:
pause
start java -classpath . osExtra.TestRemDate %1
cd OsExtra
