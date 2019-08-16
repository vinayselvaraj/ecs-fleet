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
import software.amazon.awscdk.services.ssm.StringParameter;

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
        new Cluster(this, "ECS Fleet Cluster", ClusterProps.builder()
                .withVpc(vpc)
                .build());

        new AutoScalingGroup(this, "ECS Fleet ASG", AutoScalingGroupProps.builder()
                .withMachineImage(new EcsOptimizedAmi())
                .withInstanceType(InstanceType.of(
                        InstanceClass.BURSTABLE3,
                        InstanceSize.NANO))
                .withVpc(vpc)
                .withVpcSubnets(SubnetSelection.builder()
                        .withSubnetType(SubnetType.PUBLIC)
                        .build())
                .build());
    }
}
