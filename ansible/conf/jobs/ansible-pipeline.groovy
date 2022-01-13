#!groovy
pipeline {

    agent any

    environment {
        ANSIBLE = 'Ansible'
    }

    stages {
        stage('Set host list') {
            steps {
                sh "sed -i 's/AWS_HOST/${params.AWS_HOST}/g' ${params.ANSIBLE_PATH}/hosts "
            }
        }
        stage('Set AWS IP address in api url') {
            steps {
                script {
                    sh "sed -i 's/AWS_HOST/${params.AWS_HOST}/g' ${params.ANSIBLE_PATH}/monitor-playbook.yml "
                }
            }
        }
    }
}