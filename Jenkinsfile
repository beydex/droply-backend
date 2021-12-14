pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'gradle bootJar'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }
    }
}