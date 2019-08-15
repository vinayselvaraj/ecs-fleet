package ecsfleet;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;

import java.util.Arrays;

public class AppStack extends Stack {
    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        new Vpc(this, "ECS Fleet VPC", VpcProps.builder()
                .withSubnetConfiguration(
                        Arrays.asList(SubnetConfiguration.builder()
                                .withSubnetType(SubnetType.PUBLIC)
                                .build()))
                .build());
    }
}
