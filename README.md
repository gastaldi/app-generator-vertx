# app-generator-vertx

## Steps to build and run this project

- Run `mvn clean install -DskipTests` for JBoss Forge 3.9.1-SNAPSHOT from https://github.com/forge/core
- Import this project and build in IDEA
  - Run the `Main` class
- In a terminal, enter `echo project-new --named demo --type war | http POST http://localhost:8080/api/forge/zip --timeout=1000 --download` to get a ZIP file

