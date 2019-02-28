#!groovy

def buildVersion = "7.3.0.${BUILD_NUMBER}"
def infraVersion = "3.10.0.+"
def refDataVersion = "1.0-SNAPSHOT"

def versionFlags = "-DbuildVersion=${buildVersion} -DinfraVersion=${infraVersion} -DrefDataVersion=${refDataVersion}"

stage('checkout') {
    node {
        git url: "https://github.com/sklintyg/statistik.git", branch: GIT_BRANCH
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        try {
            shgradle "--refresh-dependencies clean build testReport sonarqube -PcodeQuality -PcodeCoverage -DgruntColors=false -PuseMinifiedJavaScript \
                  ${versionFlags}"
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
            shgradle "integrationTest testReport ${versionFlags}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'service/build/reports/tests/integrationTest', \
                reportFiles: 'index.html', reportName: 'Integration test results'

        }
    }
}

stage('tag and upload') {
    node {
        shgradle "uploadArchives tagRelease ${versionFlags} -PuseMinifiedJavaScript"
    }
}

stage('notify') {
    node {
        util.notifySuccess()
    }
}

stage('propagate') {
    node {
        gitRef = "v${buildVersion}"
        releaseFlag = "${!GIT_BRANCH.startsWith("develop")}"
        build job: "statistik-sandbox-build", wait: false, parameters: [
            [$class: 'StringParameterValue', name: 'STATISTIK_BUILD_VERSION', value: buildVersion],
            [$class: 'StringParameterValue', name: 'INFRA_VERSION', value: infraVersion],
            [$class: 'StringParameterValue', name: 'REF_DATA_VERSION', value: refDataVersion],
            [$class: 'StringParameterValue', name: 'GIT_REF', value: gitRef],
            [$class: 'StringParameterValue', name: 'RELEASE_FLAG', value: releaseFlag]
        ]
    }
}

