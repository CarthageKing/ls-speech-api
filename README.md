# ls-speech-api

## Introduction

This is an example application coded to meet the requirements for one of LS company's technical challenges.

## Prerequisites

This application requires **Java 17** to build and run. Before doing anything else, ensure you have the recommended version of Java JDK installed. It requires that version of Java since the application is built using Spring Boot 3.1.x, which at the minimum requires Java 17 to build and run. For more information, please refer to [here](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Release-Notes#java-17-baseline-and-java-19-support).

## Building The Application

This application has been tested on Linux and Windows. To build the application after having cloned the repository from GitHub, execute the following command in the base project folder:

Linux:
```shell
./mvnw clean package -e -DskipITs
```

Windows:
```shell
.\mvnw.cmd clean package -e -DskipITs
```

This will download all necessary dependencies to build the application, run tests against it, and then finally produce a zip file inside the `target` folder. The file is usually named `ls-speech-api-<version>-zip-dist.zip` e.g. `ls-speech-api-0.0.1-SNAPSHOT-zip-dist.zip`.

## Running the Application

Extract the contents of that `zip-dist` file. Inside, there should be a couple of script files (`.bat` and `.sh`) as well as a `ls-speech-api.jar` file. Use the appropriate script file to execute the application depending on your environment.

Linux:
```shell
./runH2Local.sh
```

Windows:
```shell
.\runH2Local.bat
```

This will run the application on your local workstation and it will create a file-based H2 database for storing the data. You can use this for testing the application. The default server port used is 9091 and can be changed by editing the script files.

Assuming nothing is changed in the script files, the app can be checked if it is running by copy-pasting the given URL to a browser: `http://localhost:9091/speeches/o/notexist`

Assuming no errors, it should produce an output saying that the speech with the given ID could not be found.
