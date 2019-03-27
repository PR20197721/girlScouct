pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
      }
    stages {
        stage('deploy common') {
            steps {
                withMaven (maven: 'Maven 3.6', mavenLocalRepo: '.newTestRepo'){
                    echo "hello"
                    sh 'cd common && mvn versions:set -DnewVersion=2.5'
                    sh 'mvn versions:set -DnewVersion=2.5'
                }
            }
        }
        stage('deploy web') {
            steps {
                withMaven (maven: 'Maven 3.6', mavenLocalRepo: '.newTestRepo'){
                    sh 'cd common && mvn -e clean install'
                }
            }
        }
        stage('deploy gsusa') {
            steps {
                withMaven (maven: 'Maven 3.6', mavenLocalRepo: '.newTestRepo'){
                    sh 'mvn -e clean install'
                }
            }
        }
        stage('Sanity check') {
            steps {
                echo "placeholder"
            }
        }
    }
    post {
        always {
            sh 'echo "This always prints ok"'
            deleteDir()
        }
    }
}
