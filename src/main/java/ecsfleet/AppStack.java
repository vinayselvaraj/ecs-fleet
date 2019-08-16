package ecsfleet;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroupProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ClusterProps;
import software.amazon.awscdk.services.ecs.EcsOptimizedAmi;
import software.amazon.awscdk.services.elasticloadbalancing.LoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancing.LoadBalancerProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.*;
import software.amazon.awscdk.services.elasticloadbalancingv2.Protocol;

import java.util.Arrays;

public class AppStack extends Stack {

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // Create VPC
        Vpc vpc = new Vpc(this, "ECS Fleet VPC", VpcProps.builder()
                .withSubnetConfiguration(
                        Arrays.asList(SubnetConfiguration.builder()
                                .withName("ECSFleetPublicSubnet")
                                .withCidrMask(24)
                                .withSubnetType(SubnetType.PUBLIC)
                                .build()))
                .build());

        // Create ECS Cluster
        Cluster cluster = new Cluster(this, "ECS Fleet Cluster", ClusterProps.builder()
                .withVpc(vpc)
                .build());

        // Create the ASG
        AutoScalingGroup asg = new AutoScalingGroup(this, "ECS AutoScaling Group", AutoScalingGroupProps.builder()
                .withInstanceType(InstanceType.of(
                        InstanceClass.COMPUTE5,
                        InstanceSize.LARGE))
                .withDesiredCapacity(5)
                .withVpcSubnets(SubnetSelection.builder()
                        .withSubnetType(SubnetType.PUBLIC)
                        .build())
                .withSpotPrice("0.05")
                .withMachineImage(new EcsOptimizedAmi())
                .withVpc(vpc)
                .build());

        // Add the ASG to the cluster
        cluster.addAutoScalingGroup(asg);

        NetworkTargetGroup ntgTCP80 = new NetworkTargetGroup(this, "NLB TCP:80 Target Group", NetworkTargetGroupProps.builder()
                .withPort(80)
                .withTargets(Arrays.asList(asg))
                .withVpc(vpc)
                .build());

        NetworkTargetGroup ntgTCP443 = new NetworkTargetGroup(this, "NLB TCP:443 Target Group", NetworkTargetGroupProps.builder()
                .withPort(443)
                .withTargets(Arrays.asList(asg))
                .withVpc(vpc)
                .build());

        // Create the load balancer
        NetworkLoadBalancer nlb = new NetworkLoadBalancer(this, "ECS Load Balancer", NetworkLoadBalancerProps.builder()
                .withCrossZoneEnabled(true)
                .withInternetFacing(true)
                .withVpc(vpc)
                .withVpcSubnets(SubnetSelection.builder()
                        .withSubnetType(SubnetType.PUBLIC)
                        .build())
                .build());

        // Add the TCP:80 listener
        nlb.addListener("TCP80", NetworkListenerProps.builder()
                .withProtocol(Protocol.TCP)
                .withPort(80)
                .withDefaultTargetGroups(Arrays.asList(ntgTCP80))
                .withLoadBalancer(nlb)
                .build());

        // Add the TCP:443 listener
        nlb.addListener("TCP443", NetworkListenerProps.builder()
                .withProtocol(Protocol.TCP)
                .withPort(443)
                .withDefaultTargetGroups(Arrays.asList(ntgTCP443))
                .withLoadBalancer(nlb)
                .build());

    }
}
