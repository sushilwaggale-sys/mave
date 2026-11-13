# Weather Calculator

A small Java 17 and Maven project that converts Celsius to Fahrenheit and labels the temperature as Cold, Mild, or Hot.

## Run locally

Install Java 17 and Maven, then from this folder run:

```sh
mvn clean test
mvn -q exec:java -Dexec.mainClass=com.example.weather.App -Dexec.args="30"
```

The sample prints `30.0 °C (86.0 °F)` and `Weather: Hot`.

## Add to GitHub

1. On GitHub, create a new empty repository named `weather-calculator` (do not initialize it with a README).
2. Open a terminal in this project folder and run the following, replacing `YOUR-USERNAME` with your GitHub username:

```sh
git init
git add .
git commit -m "Add weather calculator project"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/weather-calculator.git
git push -u origin main
```

## Create the Jenkins job

Create a Jenkins **Pipeline** job, then choose **Pipeline script from SCM**, select **Git**, enter the repository URL, and set the script path to `Jenkinsfile`. Ensure Jenkins has a JDK 17 and a Maven installation configured. The Jenkinsfile above uses `MAVEN_HOME` as the Maven installation name and `bat`, so it expects a Windows agent. For a Linux agent, change `bat 'mvn clean test'` to `sh 'mvn clean test'`.
