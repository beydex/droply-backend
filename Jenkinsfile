pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'gradle test bootJar'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }
    }
}