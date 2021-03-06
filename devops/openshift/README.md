# Deployment instructions

### Pre-req

- A git client must be available
- "oc" must be installed locally and available on your PATH.
- VPN connected to the appropriate network.

(1.) Clone the github repo and switch to the release branch specified in the release notes, e.g:

    
    > git clone https://github.com/sklintyg/statistik.git
    > git checkout release/7.0.0
    > cd devops/openshift
    
Note that we strongly recommend using a git account that has read-only (e.g. public) access to the repo.
    
(2.) Log-in into the cluster with oc and select the correct project, e.g:

    
    > oc login https://path.to.cluster
    username: ******
    password: ******
    > oc project intygdevtest

(3.) Make sure the latest deployment template is installed into the cluster, see https://github.com/sklintyg/tools/blob/develop/devops/openshift/deploytemplate-webapp.yaml

    
## DINTYG setup

##### Create pipeline

    ~/intyg/oc/./oc process -f ~/intyg/tools/devops/openshift/pipelinetemplate-test-webapp.yaml -p APP_NAME=statistik-test -p STAGE=test -p SECRET=nosecret -p TESTS="fitnesseTest,protractorTest" | ~/intyg/oc/./oc apply -f -

##### Create env var secret and config map

    oc create -f test/configmap-vars.yaml
    oc create -f test/secret-vars.yaml
    
##### Create file secret and config map

    oc create configmap "statistik-test-config" --from-file=test/config/
    oc create secret generic "statistik-test-env" --from-file=test/env/ --type=Opaque
    oc create secret generic "statistik-test-certifikat" --from-file=test/certifikat/ --type=Opaque
    
##### Run pipeline
Typically triggered from Jenkins, but it's possible to trigger a pipeline manually, just remember to specify the required parameters.

## devtest deploy

- If necessary, open devtest/secret-vars.yaml and replace <placeholder> with real values. Save, but do **not** commit. 
- Make sure _devtest/configmap-vars.yaml_ contains the expected env vars.

##### Create env var secret and config map

    oc create -f devtest/configmap-vars.yaml
    oc create -f devtest/secret-vars.yaml
    
##### Create file secret and config map

    oc create configmap "statistik-devtest-config" --from-file=devtest/config/
    oc create secret generic "statistik-devtest-env" --from-file=devtest/env/ --type=Opaque
    
##### Create certificate secret (optional)
Certificates are typically pre-installed into the devtest "project". If necessary, the JKS / P12 files can be copied into _devtest/certifikat_ and then installed into the project:    
    
    oc create secret generic "statistik-devtest-certifikat" --from-file=devtest/certifikat/ --type=Opaque

##### Deploy för NMT till tintyg

Note that the deploytemplate may be pre-installed in the OCP cluster.

    oc process -f ~/intyg/tools/devops/openshift/deploytemplate-webapp-new.yaml \
    -p APP_NAME=statistik-devtest \
    -p IMAGE=docker-registry.default.svc:5000/dintyg/statistik-verified:latest \
    -p STAGE=devtest -p DATABASE_NAME=statistikdevtest \
    -p HEALTH_URI=/inera-certificate/services \
    -o yaml | oc apply -f -


# PROD
##### Create env var secret and config map

    oc create -f prod/configmap-vars.yaml
    oc create -f prod/secret-vars.yaml
    
##### Create file secret and config map

    oc create configmap "statistik-prod-config" --from-file=prod/config/
    oc create secret generic "statistik-prod-env" --from-file=prod/env/ --type=Opaque
    oc create secret generic "statistik-prod-certifikat" --from-file=prod/certifikat/ --type=Opaque
 
##### DEPLOY PROD

    oc process deploytemplate-webapp \
        -p APP_NAME=statistik-prod \
        -p IMAGE=<url to NEXUS> \
        -p STAGE=prod -p DATABASE_NAME=<samma som på riktigt> \
        -p HEALTH_URI=/ \
        -o yaml | oc apply -f -
