pipeline {
    agent any

    environment {
        LANG = 'en_US.UTF-8'
        LANGUAGE = 'en_US.UTF-8'
        LC_ALL = 'en_US.UTF-8'
        GRADLE_VERSION = '7.3.3'
        GRADLE_EXEC = '/opt/gradle/gradle-7.3.3/bin/gradle'
    }

    stages {
        stage('Log configuration') {
            steps {
                echo 'GRADLE_EXEC =' + GRADLE_EXEC
                sh 'java -version'
            }
        }
        stage('Build') {
            steps {
                sh '${GRADLE_EXEC} clean build bootJar'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}