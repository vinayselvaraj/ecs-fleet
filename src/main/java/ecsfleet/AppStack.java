package ecsfleet;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.iam.User;
import software.amazon.awscdk.services.iam.UserProps;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.TopicProps;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueProps;

public class AppStack extends Stack {
    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        Queue queue = new Queue(this, "MyFirstQueue", QueueProps.builder()
                .withVisibilityTimeout(Duration.seconds(300))
                .build());

        Topic topic = new Topic(this, "MyFirstTopic", TopicProps.builder()
                .withDisplayName("My First Topic Yeah")
                .build());

        topic.addSubscription(new SqsSubscription(queue));

        HelloConstruct hello = new HelloConstruct(this, "Buckets", HelloConstructProps.builder()
                .withBucketCount(5)
                .build());

        User user = new User(this, "MyUser", UserProps.builder().build());
        hello.grantRead(user);
    }
}
