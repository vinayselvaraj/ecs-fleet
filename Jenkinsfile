node {

   // Checkout stage
   stage 'Checkout'
   checkout scm
   
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
