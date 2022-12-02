pipeline {
    agent any

    environment {
        INTEGRATION_BRANCH = 'integration'
    }

    stages {
        stage("Build ---------------------------------------------------") {
            // Limit Branches
            when {
                branch 'feature/*'
                beforeAgent true
            }

            // Docker agent
            agent {
              docker {
                image 'gradle:7.5.1-jdk17-focal'
              }
            }

            steps {
                echo "Build Feature ..."
                echo "Branchname = ${BRANCH_NAME}"
                // ohne test
                sh 'gradle clean build -x test'
                sh 'ls -la build/libs'
            }
        }

        stage("Test Feature ---------------------------------------------------") {
            // Limit Branches
            when {
                branch 'feature/*'
                beforeAgent true
            }

            agent {
              docker {
                image 'gradle:7.5.1-jdk17-focal'
              }
            }

            steps {
                echo "Test Feature ..."
                sh 'gradle test'
                // JUNIT xml reports
                sh 'ls -la build/test-results/test'
                // html reports
                sh 'ls -la build/reports/tests'
            }
            // nach dem step eine post action
            post {
                always {
                    // junits results archivieren
                    junit 'build/test-results/test/*.xml'
                }

                success {
                    publishHTML target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test-Report'
                    ]
                }
            }
        }

        stage("Integrate Feature ---------------------------------------------------") {
            // Limit Branches
            when {
                branch 'feature/*'
                beforeAgent true
            }

            // hier wieder agent any
            steps {
                echo "Integrate Feature..."
                sh 'git --version'
                sh 'git branch -a'
                sh 'git checkout ${INTEGRATION_BRANCH}'
                sh 'git pull'
                // no-ff nur wenn ohne probleme mergebar
                // no-edit nur wenn ohne Bearbeitung
                // FIX ME richtiger Branche
                sh 'git merge --no-ff --no-edit remotes/origin/${BRANCH_NAME}'

                // pushen
                withCredentials([gitUsernamePassword(credentialsId: 'github_token', gitToolName: 'Default')]) {
                    sh 'git push origin ${INTEGRATION_BRANCH}'
                }
            }
        }
    }
}
