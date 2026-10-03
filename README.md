# Banking System

Java console project (abstract `Account`, `SavingsAccount`, `CurrentAccount`, custom exceptions).

## Requirements
JDK 17+ (no external dependencies)

## Build
**Windows (PowerShell):**
```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```
**Linux/macOS:**
```bash
find src -name "*.java" > sources.txt && javac -d out @sources.txt
```

## Run a test
```
java -cp out com.gdb.tests.TestAccountSubclasses
```