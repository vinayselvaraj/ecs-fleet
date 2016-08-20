import groovy.json.JsonSlurper
import com.amazonaws.services.cloudformation.model.StackStatus

node {

   // Checkout stage
   stage 'Checkout'
   dir('ecs-fleet') { checkout scm }
   dir('configs')   { git url: "${CONFIG_GIT_REPO}" }
   
   // Create/Update Stack stage
   stage 'Create/Update Stack'
   if(getStackStatus() != null) {
     echo "Stack ${STACK_NAME} exists.  Will attempt to update it"
     updateStack()
   } else {
     createStack()
   }
   
   // Wait for stack create/update to complete
   stage 'Verify'
   waitForStackCreateUpdate()
   if(!isStackCreationSuccessful(getStackStatus())) {
     throw new RuntimeException("Stack create/update not successful!")
   }
}

def createStack() {
  createUpdateStack("create-stack")
}

def updateStack() {
  createUpdateStack("update-stack")
}

def createUpdateStack(op) {
  sh "aws --region ${AWS_REGION} cloudformation ${op} --stack-name ${STACK_NAME} --template-body file://`pwd`/${STACK_TEMPLATE_FILE} --parameters file://`pwd`/${STACK_PARAMETER_FILE} ${STACK_CREATE_UPDATE_OPTIONS}"
}

def waitForStackCreateUpdate() {

  def status = "CREATE_IN_PROGRESS"
  
  while(isStackCreationInProgress(status)) {
    echo "Waiting for stack to complete create/update."
    sleep 60
    status = getStackStatus()
  }
}

def isStackCreationInProgress(status) {
  return status.equals("CREATE_IN_PROGRESS") || status.equals("UPDATE_IN_PROGRESS");
}

def isStackCreationSuccessful(status) {
  return status.equals("CREATE_COMPLETE") || status.equals("UPDATE_COMPLETE") || status.equals("UPDATE_COMPLETE_CLEANUP_IN_PROGRESS")
}

@NonCPS
def getStackStatus() {
  def status = null
  
  try {
    def jsonSlurper = new JsonSlurper()
    def output = sh(script: "aws --region ${AWS_REGION} cloudformation describe-stacks --stack-name ${STACK_NAME}", returnStdout: true)
    def jsonObject = jsonSlurper.parseText(output)
    status = jsonObject.Stacks[0].StackStatus
  } catch(Exception e) {
    echo "Stack ${STACK_NAME} does not exist"
  }

  return status
}