rem usage: Fsm.bat file.fsm
rem the input file can be eiter in the current directory or in another one.
rem the generated files will be in the same location than the input file.

rem add dot executable directory to path so FsmProcess can find it
rem do it locally in that script only, so it won't be added at each call
setlocal
set PATH=%PATH%;GraphVizPortable\App\bin
"C:\Program Files\Java\jre1.8.0_71\bin\java.exe" -jar FsmProcess.jar  -i -f %1  -c