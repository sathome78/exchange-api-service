pipeline {
  
  agent any
  
  stages {
    stage('Maven Install') {
      agent {
        docker {
          image 'maven:3.5.4'
        }
      }
      steps {
        sh 'mvn clean package'
      }
    }
    stage('Upload to Atrtifactory') {
           steps {
              script {
                 def server = Artifactory.server 'art-1'
                 def uploadSpec = """{
                    "files": [{
                       "pattern": "/var/lib/jenkins/workspace/DEV-ex-micro-app-build/ex_micro_app_api_service@2/target/*.jar",
                       "target": "exrates-api-service/"
                    }]
                 }"""

                 server.upload(uploadSpec)
               }
            }
        }
    stage('Docker Build') {
      agent any
      steps {
        sh 'docker build --build-arg ENVIRONMENT -t roadtomoon/exrates-api-service:$ENVIRONMENT .'
      }
    } 
    stage('Docker pull') {
      agent any
      steps {
        sh 'docker tag roadtomoon/exrates-api-service:$ENVIRONMENT localhost:5000/api-service:$ENVIRONMENT'
        sh 'docker push localhost:5000/api-service:$ENVIRONMENT'
      }
    } 
    stage('Deploy container') {
      steps {
        sh 'docker -H tcp://localhost:2375 service update --image localhost:5000/api-service:$ENVIRONMENT $ENVIRONMENT-api-service'
      }
    }
  }  
}
