#!/bin/sh

echo "installing the right Version of Java Development Kit with the Java runtime envrironment in it for ARM processor 32 bits (for devices  like Raspberry) As JavaFX is no longer part of the Java JDK (since version 11), running a JavaFX program on Raspberry Pi will not work out-of-the-box.Luckily we can use the LibericaJDK which is provided by BellSoft. They have a version dedicated for the Raspberry Pi which includes JavaFX, so you can run JavaFx jar with java -jar JavaFxApp.jar"
wget https://download.bell-sw.com/java/13/bellsoft-jdk13-linux-arm32-vfp-hflt.deb
sudo apt-get install ./bellsoft-jdk13-linux-arm32-vfp-hflt.deb
echo "JDK for ARM32 bit successfully installed, setting default JDK to use  to this JDK..."
sudo update-alternatives --set javac /usr/lib/jvm/bellsoft-java13-arm32-vfp-hflt/bin/javac
sudo update-alternatives --set java /usr/lib/jvm/bellsoft-java13-arm32-vfp-hflt/bin/java
java --version
echo "JDK for ARM32 successfully set on the RaspBerry pi"
echo "installation is successfull"



