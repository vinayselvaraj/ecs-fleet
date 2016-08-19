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
   if(checkStackExists(STACK_NAME, AWS_REGION)) {
     echo "Stack ${STACK_NAME}"
   }
   
}

def checkStackExists(stackName, regionName) {
  return sh 'aws --region ${regionName} cloudformation describe-stacks --stack-name ${stackName}'
}
