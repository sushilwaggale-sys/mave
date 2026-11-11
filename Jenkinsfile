pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'
    }

    stages {
        stage('Welcome Stage') {
            steps {
                echo "Welcome to Pipeline"
            }
        }

        stage('Maven Build') {
            steps {
                bat 'dir'
                bat 'mvn clean install'
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running Unit Tests"
                bat 'mvn test'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit 'target/surefire-reports/*.xml'
            }
        }

        stage('Build Success') {
            steps {
                echo "Build Successful"
            }
        }
    }
}
