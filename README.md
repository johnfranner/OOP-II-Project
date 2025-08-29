# OOP II Project

This project was developed for the Object Oriented Programming II assignment.  

## How to Run
Compile the source files and run the `App` class:

```bash
cd OOP-II-Project
$srcs = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $srcs
java -cp out com.books.App
```
You can also run with the --demo flag to skip the interactive menu.
This will automatically run a sample flow of the application:

```bash
java -cp out com.books.App --demo
```
