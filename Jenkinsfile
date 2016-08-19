node {
   // Checkout stage
   stage 'Checkout'
   dir('ecs-fleet') {
     checkout scm
   }
   dir('configs') {
     git url: "${config.git.repo}"
   }   
}
