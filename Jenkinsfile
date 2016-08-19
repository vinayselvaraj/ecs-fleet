import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudformation.AmazonCloudFormationClient

node {

   // Checkout stage
   stage 'Checkout'
   dir('ecs-fleet') { checkout scm }
   dir('configs')   { git url: "${CONFIG_GIT_REPO}" }
   
   // Create/Update Stack stage
   stage 'Create/Update Stack'
   if(checkStackExists()) {
     echo "Stack ${STACK_NAME}"
   }
   
}

def checkStackExists() {
  echo "stackName: ${STACK_NAME}, regionName: ${AWS_REGION}"
  sh 'aws --region ${AWS_REGION} cloudformation describe-stacks --stack-name ${STACK_NAME}'
}
