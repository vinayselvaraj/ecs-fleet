package ecsfleet;

public class App {
    public static void main(final String argv[]) {
        software.amazon.awscdk.core.App app = new software.amazon.awscdk.core.App();

        new AppStack(app, "AppStack");

        // required until https://github.com/awslabs/jsii/issues/456 is resolved
        app.synth();
    }
}
