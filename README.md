GranolaServer
=============

GranolaServer for creating Restful web service devices out of devices such as computers and the Raspberry Pi.  
This uses a module mentality where a jar file can be inserted into a running server.

I first implemented it with Jersey 1.17 and then with Jersey 2.2.  
However, the PI was so slow I decided to implement it on my own Rest Services.  
Not all functions are implemented, but I am adding to it as I move along.

I also included the required jars in the lib directory.  
This is a NetBeans project and you should be able to download it, compile it, then run it on the raspberry pi with the latest jdk.  
I will include more instructions as I get further along and closer to a first release.  
It includes functions to control the GPIO and I2C on the server.  
You also have to run it using sudo and then it downgrades to user pi after loading device memory for security.
It also runs on the other versions of linux such as archlinux, but you might have to change the user it downgrades to.
Also, it will run on your PC and if it is not able to run the .so then it will just make a default buffer to simulate hardware.
This is not a release version yet.  There are bugs and security issues with the http server so use behind a firewall for now.

