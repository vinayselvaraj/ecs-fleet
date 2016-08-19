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
   checkStackExists(STACK_NAME, AWS_REGION)
   
}

def checkStackExists(stackName, regionName) {
  def cfnClient = new AmazonCloudFormationClient()
  cfnClient.setRegion(Region.getRegion(Regions.fromName(regionName)))
}