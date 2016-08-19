node {

   // Checkout stage
   stage 'Checkout'
   dir('ecs-fleet') { checkout scm }
   dir('configs')   { git url: "${CONFIG_GIT_REPO}" }
   
   // Create/Update Stack stage
   stage 'Create/Update Stack'
   checkStackExists("${CONFIG_STACK_NAME}")
   
}

def checkStackExists(stackName) {
  print stackName
}