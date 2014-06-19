webster-example
===============

This is an example application for webster. It's heavily inspired by the [computer-database sample](https://github.com/playframework/playframework/tree/master/samples/java/computer-database)
of the [Play Framework](http://playframework.com/).

Setup
-----

Java 8 and Maven 3 are required.

Install [webster](https://github.com/pschirmacher/webster) to your local maven repository. Then:

    cd webster-example
    mvn package
    java -jar target/webster-example-0.1.0-SNAPSHOT-jar-with-dependencies.jar

This will start the app on [localhost:8080](http://localhost:8080).