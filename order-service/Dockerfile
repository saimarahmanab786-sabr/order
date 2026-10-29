
pipeline {
    agent any

    stages {
        stage('Check Tools') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
            }
        }
    }
}
