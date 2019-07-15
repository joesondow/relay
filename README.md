# relay

A system for timed retweets and unretweets of certain Twitter accounts by certain other Twitter 
accounts, according to specific logic.

Example usage includes:
Having @PicardTips and @RikerGoogling retweet the latest @StarTrekHour call for discussion questions
Having @PicardTips and @RikerGoogling retweet each other's most successful tweets.
Having @EmojiAquarium retweet a new @EmojiTetra poll occasionally.

# Build

This project uses [Gradle](https://gradle.org/) for building, in order to make it easier to include support for [Spock](http://spockframework.org/) testing. The AWS Lambda documentation for [Creating a .jar Deployment Package Using Maven and Eclipse IDE (Java)](https://docs.aws.amazon.com/lambda/latest/dg/java-create-jar-pkg-maven-and-eclipse.html) only covers how to use the Shade plugin for Maven to create an executable "fat jar" (containing all dependencies) suitable for use with Lambda. The Gradle equivalent is the Shadow plugin. To build the project:

## on the command line

gradle clean shadowJar
