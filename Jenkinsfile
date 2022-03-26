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

        stage('Send notification') {
            steps {
                withCredentials([
                    string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                    string(credentialsId: 'telegram-bot-chat', variable: 'CHAT_ID')
                ]) {
                    sh """
                        curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode=markdown \
                        -d text='Запуск 🕙 *${env.BRANCH_NAME} # ${env.BUILD_NUMBER}*\n­\nBlue Ocean: ${env.RUN_DISPLAY_URL}'
                    """
                }
            }
        }

        stage('Build') {
            steps {
                sh script: 'mkdir keys', label: 'Make folder for keys'
                sh script: 'chmod +x gradlew && ./gradlew genkey -Pforce-genkey', label: 'Generate keys with possible overwrite'
                sh script: 'chmod +x gradlew && ./gradlew clean build bootJar', label: 'Build project and make server jar'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }

        stage('SonarQube Analytics') {
            steps {
                withSonarQubeEnv('SonarMine') {
                    sh script: 'chmod +x gradlew && ./gradlew sonarqube',
                        label: 'Run sonar analysis via Gradle'
                }
            }
        }

        stage("Quality Gate") {
            steps {
                script {
                  timeout(time: 1, unit: 'HOURS') {
                    def results = waitForQualityGate()
                    withCredentials([
                        string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                        string(credentialsId: 'telegram-bot-chat', variable: 'CHAT_ID')
                    ]) {
                        if (results.status != "OK") {
                            sh """
                                curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode=markdown \
                                -d text='Quality Gates ❌\n­\n*${env.BRANCH_NAME}*\nСтатус: ${results.status}\n­\nBlue Ocean: ${env.RUN_DISPLAY_URL}'
                            """
                        } else {
                            sh """
                                curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode=markdown \
                                -d text='Quality Gates 🆗\n­\n*${env.BRANCH_NAME}*\nСтатус: ${results.status}\n­\nBlue Ocean: ${env.RUN_DISPLAY_URL}'
                            """
                        }
                    }
                  }
                }
            }
        }

        stage('Docker build and publish') {
            when {
                anyOf {
                    branch "test"
                    branch "master"
                }
            }
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-registry',
                        usernameVariable: 'DOCKER_REGISTRY_USERNAME',
                        passwordVariable: 'DOCKER_REGISTRY_PASSWORD')
                ]) {
                    sh script: 'chmod +x gradlew && ./gradlew jib -DsendCredentialsOverHttp=true --image=registry.mine.theseems.ru/droply-backend:' + env.BRANCH_NAME,
                        label: 'Build and deploy docker image: ' + env.BRANCH_NAME
                }
            }
        }

        stage('Stand Deploy Application') {
            when {
                anyOf {
                    branch "test"
                    branch "master"
                }
            }
            steps {
                git branch: env.BRANCH_NAME,
                    credentialsId: 'bitbucket-ssh',
                    url: 'git@bitbucket.org:beydex/droply-devops.git'

                withCredentials([file(credentialsId: 'common-stand-params', variable: 'STAND_PARAMS')]) {
                    sh script: 'cp ${STAND_PARAMS} common.env',
                        label: 'Move common stand params nearby'
                }

                withCredentials([file(credentialsId: 'stand-' + env.BRANCH_NAME + '-params', variable: 'STAND_PARAMS')]) {
                    sh script: 'cp ${STAND_PARAMS} stand.env',
                        label: 'Move stand-related params nearby'
                }

                sh script: 'cat common.env stand.env > params.env && rm stand.env && rm common.env',
                    label: 'Formatting fulfilled stand params'

                sh script: """
                set +x
                export \$(grep -v '^#' params.env | xargs) && ansible-playbook ansible/deploy.yml && rm params.env
                """, label: 'Deploy application via Ansible'
            }
        }

    }

    post {
        success {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-bot-chat', variable: 'CHAT_ID')
            ]) {
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode=markdown \
                    -d text='✅ *${env.BRANCH_NAME} # ${env.BUILD_NUMBER}*\n­\n${env.BUILD_URL}'
                """
            }
        }

        aborted {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-bot-chat', variable: 'CHAT_ID')
            ]) {
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode=markdown \
                    -d text='⏹ *${env.BRANCH_NAME} # ${env.BUILD_NUMBER}*\n­\n${env.BUILD_URL}'
                """
            }
        }

        failure {
            withCredentials([
                string(credentialsId: 'telegram-bot-token', variable: 'TOKEN'),
                string(credentialsId: 'telegram-bot-chat', variable: 'CHAT_ID')
            ]) {
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage -d chat_id=${CHAT_ID} -d parse_mode=markdown \
                    -d text='❌ *${env.BRANCH_NAME} # ${env.BUILD_NUMBER}*\n­\n${env.BUILD_URL}'
                """
            }
        }

        always {
            cleanWs()
        }
    }
}