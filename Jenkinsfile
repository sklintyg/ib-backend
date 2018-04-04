pipeline {
    environment {
        buildVersion = "1.0.0.${BUILD_NUMBER}"
        infraVersion = "3.6.0.+"
    }

    agent any //TODO: this should later be replaced with a gradle-docker-image

    stages {

        stage('build') {
            steps {
                shgradle "--refresh-dependencies clean build testReport sonarqube -PcodeQuality " +
                         "-DbuildVersion=" + buildVersion + " -DinfraVersion=" + infraVersion
            }
            post {
                always {
                    publishHTML target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/allTests',
                        reportFiles: 'index.html',
                        reportName: 'JUnit results'
                    ]
                }
            }
        }

        stage('tag and upload') {
            steps {
                shgradle "uploadArchives tagRelease " +
                         "-DbuildVersion=" + buildVersion + " -DinfraVersion=" + infraVersion
            }
        }
    }
}

