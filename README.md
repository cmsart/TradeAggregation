# TradeAggregator
## Overview
This program subscribes to a [polygon.io](https://polygon.io/) websocket and consumes trade data for a given cryptocurrency pair (eg. BTC-USD).
The data is used to compile 30-second aggregates containing the following information:

* Ticker
* Aggregate Start Time
* Open Price
* Close Price
* High Price
* Low Price

Example output:
`21:14:01 - BTC-USD - open: $42500.00, close: $42474.69, high: $42521.00, low: $42471.19, volume: 4.419786490`

## Building and Running
To run this program, you will need a Java 11 JDK installed on your machine and included in your classpath. You can download the JDK from
[AdoptOpenJDK](https://adoptopenjdk.net/).

Once Java is installed, clone the repository, then use the included Gradle build script to execute the program. There is a `run` command
that takes in an API key as an argument.

Example: `./gradlew run --args="your_api_key"`
