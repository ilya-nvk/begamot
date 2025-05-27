\
    @ECHO OFF
    SET DIR=%~dp0
    SET JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
    IF NOT EXIST "%JAR%" (
        ECHO gradle-wrapper.jar not found. Open the project in Android Studio and run a Gradle sync, or generate the wrapper manually.
        EXIT /B 1
    )
    java -jar "%JAR%" %*
