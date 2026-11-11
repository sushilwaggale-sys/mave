pipeline{
  agent any
  tools {
        // maven 'MAVEN_HOME'
        maven 'MAVEN_HOME' // Make sure this matches your Jenkins global tool config
    }
  stages{
    stage('welcome stage'){
    steps{
      echo'jenkinsfile added'
    }
  }
    stage('Maven Build') {
            steps {
                bat 'mvn install'
                bat 'mvn clean install' // Includes test by default
            }
        }
    stage('Run Tests') {
            steps {
                echo "Running Unit Tests"
                // Optional: You can explicitly run 'mvn test' here
                bat 'mvn test'
            }
        }
    stage('Publish Test Results') {
            steps {
                junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml' // Standard JUnit report location for Maven
            }
        }

}
}
