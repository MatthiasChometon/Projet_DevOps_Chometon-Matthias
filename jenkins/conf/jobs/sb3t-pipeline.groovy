#!groovy

pipeline {
    agent any
    tools {
        maven 'maven'
    }
    environment {
        TEST = 'TEST'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: '100'))
        ansiColor('xterm')
    }
    stages {
        stage('Job description') {
            steps {
                script {
                    println('Env var: ' + env.TEST)
                    sh 'java --version'
                    sh 'mvn --version'
                    sh 'python3 --version'
                    currentBuild.displayName = "#${BUILD_NUMBER} ${params.PARAM1}"
                }
            }
        }
        stage('git clone') {
            steps {
                git branch: "${params.BRANCH}",
                    url: 'https://github.com/Ozz007/sb3t.git'
            }
        }
        stage('compile') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('testunit') {
            when {
            expression { params.SKIP_TESTS == false }
            }
            steps {
                sh 'mvn test'
            }
        }
        stage('package') {
            steps {
                sh 'mvn package'
            }
        }
        stage('testinteg') {
            when {
            expression { params.SKIP_TESTS == false }
            }
            steps {
                sh 'mvn verify'
            }
        }
        stage('rename move') {
            steps {
                script{
                    sh "mv sb3t-ws/target/sb3t-ws-1.0-SNAPSHOT.jar sb3t-${params.VERSION}-${params.VERSION_TYPE}.jar"
                }
            }
        }
    }
}
