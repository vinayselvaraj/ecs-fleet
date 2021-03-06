package ecsfleet;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecs.*;
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
                .withMaxAzs(99)
                .withSubnetConfiguration(
                        Arrays.asList(SubnetConfiguration.builder()
                                .withName("ECSFleetPublicSubnet")
                                .withCidrMask(24)
                                .withSubnetType(SubnetType.PUBLIC)
                                .build()))
                .build());

        System.out.println(vpc.getAvailabilityZones());

        // Create ECS Cluster
        Cluster cluster = new Cluster(this, "ECS Fleet Cluster", ClusterProps.builder()
                .withVpc(vpc)
                .build());

        // Add the ASG to the cluster
        AutoScalingGroup asg = cluster.addCapacity("ECS AutoScaling Group", AddCapacityOptions.builder()
                .withInstanceType(InstanceType.of(
                        InstanceClass.BURSTABLE3,
                        InstanceSize.LARGE))
                .withDesiredCapacity(6)
                .withVpcSubnets(SubnetSelection.builder()
                        .withSubnetType(SubnetType.PUBLIC)
                        .build())
                .withSpotPrice("0.05")
                .build());

        // Allow all traffic from the ASG peers
        asg.getConnections().allowFrom(asg.getConnections(), Port.allTraffic());

        // Allow inbound HTTP & HTTPS from anywhere
        asg.getConnections().allowFromAnyIpv4(Port.tcpRange(80, 80));
        asg.getConnections().allowFromAnyIpv4(Port.tcpRange(443, 443));

        // TCP:80 Target Group
        NetworkTargetGroup ntgTCP80 = new NetworkTargetGroup(this, "NLB TCP:80 Target Group", NetworkTargetGroupProps.builder()
                .withPort(80)
                .withTargets(Arrays.asList(asg))
                .withVpc(vpc)
                .build());

        // TCP:443 Target Group
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
