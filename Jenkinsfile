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
          WORKSPACE_OUT = sh(script: 'ls -a', returnStdout: true)
          GRADLE_VERSION_OUT = sh(script: 'chmod +x gradlew && ./gradlew --version', returnStdout: true)
          JAVA_VERSION_OUT = sh(script: 'java -version', returnStdout: true)
          OPENSSL_VERSION_OUT = sh(script: 'openssl version', returnStdout: true)

          echo "Workspace setup"
          echo "${WORKSPACE}"

          echo "Gradle (using wrapper)"
          echo "${GRADLE_VERSION_OUT}"

          echo "Java"
          echo "${JAVA_VERSION_OUT}"

          echo "OpenSSL"
          echo "${OPENSSL_VERSION_OUT}"
        }
      }
    }

    stage('Generate keys') {
      steps {
        sh 'mkdir keys'
        sh 'chmod +x gradlew && ./gradlew genkey -Pforce-genkey'
      }
    }

    stage('Build') {
      steps {
        sh 'chmod +x gradlew && ./gradlew clean build bootJar'
        archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
      }
    }

    stage('SonarQube Analytics') {
      steps {
        withSonarQubeEnv('SonarMine') {
            sh 'chmod +x gradlew && ./gradlew sonarqube'
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