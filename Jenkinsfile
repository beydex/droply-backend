pipeline {
  agent any

  environment {
    LANG = 'en_US.UTF-8'
    LANGUAGE = 'en_US.UTF-8'
    LC_ALL = 'en_US.UTF-8'
  }

  stages {
    stage('Environment') {
      steps {
        script {
          sh script: 'ls -a', label: 'Workspace'
          sh script: 'chmod +x gradlew && ./gradlew --version', label: 'Gradle'
          sh script: 'java -version', label: 'Java'
          sh script: 'openssl version', label: 'OpenSSL'
        }
      }
    }

    stage('Generate keys') {
      steps {
        sh script: 'mkdir keys', label: 'Make folder for keys'
        sh script: 'chmod +x gradlew && ./gradlew genkey -Pforce-genkey', label: 'Generate keys with (potentially) override'
      }
    }

    stage('Build') {
      steps {
        sh script: 'chmod +x gradlew && ./gradlew clean build bootJar', label: 'Build project and make server jar'
        archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
      }
    }

    stage('SonarQube Analytics') {
      steps {
        withSonarQubeEnv('SonarMine') {
            sh script: 'chmod +x gradlew && ./gradlew sonarqube', label: 'Run sonar analysis via Gradle'
        }
      }
    }

  }

  post {
    always {
      cleanWs()
    }
  }
}