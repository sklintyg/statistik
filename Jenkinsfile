#!groovy

def buildVersion = "7.0.0.${BUILD_NUMBER}"
def infraVersion = "3.6.0.+"

stage('checkout') {
    node {
        git url: "https://github.com/sklintyg/statistik.git", branch: GIT_BRANCH
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        try {
            shgradle "--refresh-dependencies clean build testReport sonarqube -PcodeQuality -PcodeCoverage -DgruntColors=false -Pstatistik.useMinifiedJavaScript \
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
            util.waitForServer('https://fitnesse2.inera.nordicmedtest.se/version.jsp')
        }
    }
}

stage('fitnesse') {
    node {
        try {
            shgradle "fitnesseTest -PfileOutput -PoutputFormat=html \
                 -DbaseUrl=https://fitnesse2.inera.nordicmedtest.se/ -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'specifications/', \
               reportFiles: 'fitnesse-results.html', reportName: 'Fitnesse results'
        }
    }
}

stage('protractor') {
    node {
        try {
            sh(script: 'sed -i -r "s,(e.code === \'ECONNRESET\'),e.code === \'ECONNRESET\' || e.code === \'ETIMEDOUT\'," specifications/node_modules/selenium-webdriver/http/index.js')// NMT magic
            wrap([$class: 'Xvfb']) {
                shgradle "protractorTests -Dprotractor.env=build-server -DbaseUrl=https://fitnesse2.inera.nordicmedtest.se/ \
                      -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion}"
            }
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'specifications/reports', \
                reportFiles: 'index.html', reportName: 'Protractor results'
        }
    }
}

stage('tag and upload') {
    node {
        shgradle "uploadArchives tagRelease -DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion} -Pstatistik.useMinifiedJavaScript"
    }
}

stage('notify') {
    node {
        util.notifySuccess()
    }
}
