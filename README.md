GranolaServer
=============

GranolaServer for creating Restful web service devices out of devices such as computers and the Raspberry Pi.  
This uses a module mentality where a jar file can be inserted into a running server.

I have tried many different servers and ultimately decided on Jetty.  To compile: 

1) check out the project from git<br>
2) cd to GranolaServer/jetty-service directory <br>
3) run: mvn package <br>
<br>
Maven will then download all requirements for jetty server and jersey and package them into a single jar.
This jar is a requirement for the Netbeans project.  I do not use maven to compile the project itself, only to find the dependencies.
<br>
4) Open GranolaServer project in netbeans <br>
5) Run the server and then log into http://localhost:7023 <br>

To deploy to the raspberry pi, the jni folder needs to be compiled on the pi.  This then gives ability
to turn on and off gpio pins through rest server.

