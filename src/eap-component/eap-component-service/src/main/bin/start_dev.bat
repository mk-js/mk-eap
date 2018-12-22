setlocal 

set name="component-service"
set port=8000
set jarfile=component-service.jar


rem PubReference
set group1=%COMPUTERNAME% 
IF type_%1==type_PubReference ( 
	set group2=
) ELSE (
	set group2=%COMPUTERNAME% 
)

start %name% /MIN java -Ddubbo.service.group=%group1% -Ddubbo.reference.group=%group2%  -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%port% -jar "%cd%\..\%jarfile%"

endlocal









