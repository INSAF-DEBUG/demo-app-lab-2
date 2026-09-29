pipeline {
    agent any

    environment {
        JAVA_HOME = '/opt/java/jdk-21.0.4+7'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
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
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '=== Analyse SonarQube ==='

                withSonarQubeEnv('SonarQube') {
                    sh '''
                        set -e

                        mvn sonar:sonar \
                          -Dsonar.projectKey=com.insaf.demo:demo-app \
                          -Dsonar.projectName=demo-app \
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

                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo '=== Deploy vers Nexus ==='

                sh '''
                    set -e

                    echo "=== Vérification du JAR ==="
                    ls -lh target/*.jar

                    echo "=== Deploy Maven ==="
                    mvn deploy -DskipTests
                '''
            }
        }

        stage('Archive') {
            steps {
                echo '=== Archive des artefacts ==='

                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }

    post {
        always {
            echo '=== Pipeline terminé ==='
        }

        success {
            echo '=== CI SUCCESS ==='
            echo 'Le pipeline a terminé avec succès.'
        }

        failure {
            echo '=== CI FAILURE ==='
            echo 'Le pipeline a échoué.'
        }
    }
}
