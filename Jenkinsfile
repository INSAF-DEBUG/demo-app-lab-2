pipeline {

    agent {
        label 'centos-build'
    }

    environment {
        JAVA_HOME = '/opt/java/jdk-21.0.4+7'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"

        SONAR_URL = 'http://192.168.42.152:9000'

        NEXUS_URL = 'http://192.168.42.XXX:8081'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=== Checkout Git ==='
                checkout scm
            }
        }

        stage('Check Java / Maven') {
            steps {
                sh '''
                    set -e

                    echo "=== JAVA_HOME ==="
                    echo "$JAVA_HOME"

                    echo "=== Java ==="
                    which java
                    java -version

                    echo "=== Maven ==="
                    which mvn
                    mvn -version
                '''
            }
        }

        stage('Build') {
            steps {
                echo '=== Build Maven ==='

                sh '''
                    set -e
                    mvn clean package -DskipTests
                '''
            }
        }

        stage('Tests') {
            steps {
                echo '=== Tests Maven ==='

                sh '''
                    set -e
                    mvn test
                '''
            }

            post {
                always {
                    junit(
                        testResults: 'target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '=== Analyse SonarQube ==='

                withSonarQubeEnv('SonarQube') {
                    sh '''
                        set -e

                        echo "=== SonarQube ==="
                        echo "$SONAR_HOST_URL"

                        echo "=== Test connexion SonarQube ==="
                        curl -I "$SONAR_HOST_URL"

                        echo "=== Analyse SonarQube ==="

                        mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.697:sonar \
                          -Dsonar.projectKey=com.insaf.demo:demo-app \
                          -Dsonar.projectName=demo-app \
                          -Dsonar.host.url="$SONAR_HOST_URL" \
                          -Dsonar.sources=src/main/java \
                          -Dsonar.tests=src/test/java \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '=== Quality Gate SonarQube ==='

                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo '=== Deploy vers Nexus ==='

                sh '''
                    set -e

                    echo "Nexus URL : $NEXUS_URL"

                    curl -I "$NEXUS_URL"

                    echo "Nexus accessible."
                '''
            }
        }

        stage('Archive') {
            steps {
                echo '=== Archive des artefacts ==='

                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }
    }

    post {

        success {
            echo '=== CI/CD SUCCESS ==='
            echo 'Le pipeline est terminé avec succès.'
        }

        failure {
            echo '=== CI/CD FAILURE ==='
            echo 'Le pipeline a échoué.'
        }

        always {
            echo '=== Pipeline terminé ==='
        }
    }
}
