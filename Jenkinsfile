node {

   // Checkout stage
   stage 'Checkout'
   dir('ecs-fleet') {
     checkout scm
   }
   
   dir('jenkins-scripts') {
    git url: https://github.com/vinayselvaraj/jenkins-scripts.git
   }
   
   
   // Create/Update Stack stage
   stage 'Create/Update Stack'
   createUpdateStack()
   
   // Wait for stack create/update to complete
   stage 'Verify'
   waitForStackCreateUpdate()
}

def createUpdateStack() {
}

def waitForStackCreateUpdate() {
}
