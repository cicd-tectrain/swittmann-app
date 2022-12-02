pipeline {
    agent any

    stages {
        stage("Build") {
            // Docker agent
            agent {
              docker {
                image 'gradle:7.5.1-jdk17-focal'
              }
            }

            steps {
                echo "Build Feature ..."
                // ohne test
                sh 'gradle clean build -x test'
            }
        }

        stage("Test") {
            steps {
                echo "Test Feature ..."
            }
        }

        stage("Integration") {
            steps {
                echo "Integrate Feature..."
            }
        }
    }
}
