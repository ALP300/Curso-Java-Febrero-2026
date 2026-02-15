@echo off
echo Compilando...
javac -d . src/main/java/com/mycompany/wawawa/*.java
if %errorlevel% neq 0 (
    echo Error de compilacion.
    pause
    exit /b %errorlevel%
)
echo Ejecutando Gestor de Inventario...
java com.mycompany.wawawa.Wawawa
pause
