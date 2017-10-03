## Synopsis

This is a sample Java agent that instruments a Servlet and counts the strings used in each call.

## Installation

Packaged as an IntelliJ file. Import the project and click Build->Build Project.

## Tests

To run with a jetty server:

java -javaagent:ServerAgent.jar start.jar

Note that the javassist.jar has to be in the same directory as the ServerAgent.jar

## License

(c) Joshua Engel, all rights reserved
