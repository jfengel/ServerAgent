## Synopsis

This is a sample Java agent that instruments a Servlet and counts the strings used in each service call.

## Installation

Packaged as an IntelliJ file. Import the project and click Build->Build Project.

## Tests

To run with a jetty server:

java -javaagent:ServerAgent.jar start.jar

Note that the javassist.jar has to be in the same directory as the ServerAgent.jar

To look at the results go to http://localhost:1729

## License

Hey, go for it. It's yours. Try to make the world a better place.