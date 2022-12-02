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
                sh 'ls -la build/libs'
            }
        }

        stage("Test Feature") {
            agent {
              docker {
                image 'gradle:7.5.1-jdk17-focal'
              }
            }

            steps {
                echo "Test Feature ..."
                sh 'gradle test'
            }
        }

        stage("Integrate Feature") {
            steps {
                echo "Integrate Feature..."
            }
        }
    }
}
