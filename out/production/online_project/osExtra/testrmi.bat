@echo off
echo Test RMI
echo Documentazione in http://docs.oracle.com/javase/1.5.0/docs/guide/rmi/spec/rmiTOC.html
echo Compilazione stub con rmic: questo non e' piu' necessario, e' eseguito run-time
cd ..
rem rmic OsExtra.RemDateImpl
rem echo Battere RETURN per continuare
rem pause
echo Attiva registro RMI
start rmiregistry
echo Attivato, battere Ctrl-C al termine per terminare
echo Battere RETURN per continuare
pause
echo Attiva Server
rem v2.01 prova con localhost implicito
rem start java -classpath . -Djava.rmi.server.codebase=file:%CD%/ osExtra.RemDateImpl
start java -classpath . osExtra.RemDateImpl
echo Attivato, battere RETURN per continuare
pause
echo Attiva client in locale
start java -classpath . osExtra.TestRemDate %1
cd OsExtra
