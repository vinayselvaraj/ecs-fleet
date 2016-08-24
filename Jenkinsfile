import groovy.json.JsonSlurper
import com.amazonaws.services.cloudformation.model.StackStatus
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient

node {

   // Checkout stage
   stage 'Checkout'
   dir('ecs-fleet') { checkout scm }
   
   // Create/Update Stack stage
   stage 'Create/Update Stack'
   try {
     getStackStatus()
     echo "Stack ${STACK_NAME} exists.  Will attempt to update it"
     updateStack()
   } catch(Exception e) {
     createStack()
   }
   
   // Wait for stack create/update to complete
   stage 'Verify'
   waitForStackCreateUpdate()
   stackStatus = getStackStatus()
   if(!isStackCreationSuccessful(stackStatus)) {
     error "Stack create/update failed: ${stackStatus}"
   }
}

def createStack() {
  createUpdateStack("create-stack")
}

def updateStack() {
  createUpdateStack("update-stack")
}

def createUpdateStack(op) {

  def cfn = new AmazonCloudFormationClient()

  sh "aws --region ${AWS_REGION} cloudformation ${op} --stack-name ${STACK_NAME} --template-body ecs-fleet-cfn.json --parameters file://`pwd`/${STACK_PARAMETER_FILE} --capabilities CAPABILITY_IAM"
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
  echo status
  return status.equals("CREATE_IN_PROGRESS") || status.equals("UPDATE_IN_PROGRESS");
}

def isStackCreationSuccessful(status) {
  return status.equals("CREATE_COMPLETE") || status.equals("UPDATE_COMPLETE") || status.equals("UPDATE_COMPLETE_CLEANUP_IN_PROGRESS")
}

def getStackStatus() {
  def status = null
  def cfnClient = new AmazonCloudFormationClient()
  
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