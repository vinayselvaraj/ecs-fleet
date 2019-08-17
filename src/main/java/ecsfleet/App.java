package ecsfleet;

import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public class App {
    public static void main(final String argv[]) {
        software.amazon.awscdk.core.App app = new software.amazon.awscdk.core.App();

        new AppStack(app, "AppStack", StackProps.builder()
                .withEnv(Environment.builder()
                        .withRegion("us-east-1")
                        .withAccount("746094636937")
                        .build())
                .build());

        // required until https://github.com/awslabs/jsii/issues/456 is resolved
        app.synth();
    }
}
