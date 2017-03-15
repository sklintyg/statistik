#!groovy

def buildVersion = "5.0.${BUILD_NUMBER}"
def infraVersion = "3.2.+"

stage('checkout') {
    node {
        git url: "https://github.com/sklintyg/statistik.git", branch: GIT_BRANCH
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        try {
            shgradle "--refresh-dependencies clean build testReport sonarqube -PcodeQuality -PcodeCoverage -DgruntColors=false \
                  -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/allTests', \
                reportFiles: 'index.html', reportName: 'JUnit results'

        }
    }
}

// Right now these tests must run in its own stage, b/c gretty and jacoco don't work together
stage('integrationTest') {
    node {
        try {
            shgradle "integrationTest testReport -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/integrationTest', \
                reportFiles: 'index.html', reportName: 'Integration test results'

        }
    }
}

stage('deploy') {
    node {
        util.run {
            ansiblePlaybook extraVars: [version: buildVersion, ansible_ssh_port: "22", deploy_from_repo: "false"], \
                installation: 'ansible-yum', inventory: 'ansible/inventory/statistik/fitnesse', playbook: 'ansible/deploy.yml'
            util.waitForServer('https://fitnesse.inera.nordicmedtest.se/version.jsp')
        }
    }
}

// stage('protractor') {
//     node {
//         try {
//             wrap([$class: 'Xvfb']) {
//                 shgradle "protractorTests -Dprotractor.env=build-server \
//                       -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
//             }
//         } finally {
//             publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'specifications/reports', \
//                 reportFiles: 'index.html', reportName: 'Protractor results'
//         }
//     }
// }

// stage('fitnesse') {
//    node {
//        try {
//            wrap([$class: 'Xvfb']) {
//                shgradle "fitnesseTest -PfileOutput -PoutputFormat=html \
//                      -Dstatistics.base.url=https://fitnesse.inera.nordicmedtest.se/ -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
//            }
//        } finally {
//            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'specifications/', \
//                reportFiles: 'fitnesse-results.html', reportName: 'Fitnesse results'
//        }
//    }
// }

// stage('tag and upload') {
//     node {
//         shgradle "uploadArchives tagRelease -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
//     }
// }

stage('notify') {
    node {
        util.notifySuccess()
    }
}
