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


        // integration

       stage("Build integration ---------------------------------------------------") {
            // Limit Branches
            when {
                branch "${INTEGRATION_BRANCH}"
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

                // stashen if stage was successful
                post {
                    success {
                        // id und includemaske
                        stash name: 'integration_build', includes: 'build/'
                    }
                }

            }
        }

        stage("Test integration ---------------------------------------------------") {
            // Limit Branches
            when {
                branch "${INTEGRATION_BRANCH}"
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

        // artefacts in nexus reinladen
        stage("Publish Artifacts") {
            // Limit Branches
            when {
                branch "${INTEGRATION_BRANCH}"
                beforeAgent true
            }

            steps {
                // unstash
                unstash 'integration_build'

                // Publish artifact in nexus
                // werte aus build.gradle uebernehmen
                nexusArtifactUploader artifacts: [
                [
                    artifactId: at.tectrain.app,
                    classifier: '',
                    file: 'build/libs/app-0.0.1-SNAPSHOT.jar',
                    type: 'jar'
                ]],
                credentialsId: 'nexus_credentials',
                groupId: '',
                nexusUrl: 'nexus:8081/repository/maven-snapshots',
                protocol: 'http',
                repository: '',
                version: '0.0.1-SNAPSHOT'
            }
        }


        stage('Deploy integration branch') {
            when {
                branch "${INTEGRATION_BRANCH}"
                beforeAgent true
            }


            // docker image bauen und starten (und archivieren)

            // Env fuer nexus credentials
            environment {
                NEXUS_CREDENTIALS = credentials('nexus_credential')
            }

            steps {
                echo "deployment ..."

                unstash 'integration_build'

                // Image bauen -> Dockerfile
                sh 'docker build -t nexus:5000/app:latest -f docker/integration/Dockerfile .'

                // Image taggen

                sh 'echo ${NEXUS_CREDENTIALS_PSW} | docker login -u ${NEXUS_CREDENTIALS_USR} --password-stdin nexus:5000'

                // Image pushen
                sh 'docker push nexus:5000/app:latest'

                sh 'docker container run -p 8090:8085 --name testing -d --rm app:latest'

            }

            post {
                always {
                    sh 'docker logout nexus:5000'
                }
            }

        }
    }
}
