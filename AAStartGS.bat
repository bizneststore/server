@ECHO off

SET GSID=1
SET INTIPADDR=127.0.0.1
SET INTPTR=46656

SET IOCPMODE=0
SET EXE_FILE=C:\Program Files\Java\jdk1.7.0_21\bin\java.exe
SET START_ARGS=-server -Xbootclasspath/p:crypt/l2ft.jar -Dfile.encoding=UTF-8 -Xmx1G -cp config/xml;./lib/* l2p.gameserver.GameServer

SET AA_AUTORESTART_MODE=0

start starter.exe
