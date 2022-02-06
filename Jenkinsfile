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
        sh script: 'chmod +x gradlew && ./gradlew genkey -Pforce-genkey', label: 'Generate keys with possible overwrite'
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

    stage('Docker build and publish') {
      when {
        anyOf {
            branch "test"

            // Plans
            branch "production"
        }
      }
      steps {
        withCredentials([
            usernamePassword(
            credentialsId: 'docker-registry',
            usernameVariable: 'DOCKER_REGISTRY_USERNAME',
            passwordVariable: 'DOCKER_REGISTRY_PASSWORD')]) {
            sh script: 'chmod +x gradlew && ./gradlew jib -DsendCredentialsOverHttp=true --image=registry.mine.theseems.ru/droply-backend',
               label: 'Build and deploy docker image'
        }
      }
    }

    stage('Deploy to test') {
      when {
        branch "test"
      }
      steps {
        git branch: 'test',
            credentialsId: 'bitbucket-ssh',
            url: 'git@bitbucket.org:beydex/droply-devops.git'

        withCredentials([
            usernamePassword(
            credentialsId: 'docker-registry',
            usernameVariable: 'DOCKER_REGISTRY_USERNAME',
            passwordVariable: 'DOCKER_REGISTRY_PASSWORD')]) {
                sh script: 'ansible-playbook ansible/deploy_test.yml',
                   label: 'Deploy to the test stand: ws://test.mine.theseems.ru'
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