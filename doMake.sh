#!/bin/sh

echo "Deleting log files"
rm *.log
echo "Doing javah"
cd build/classes
javah com.granolamatt.hardware.HardwareMemory
mv *.h ../../jni

cd ../../
# cd ../../jni
# make clean
# make
#mv libCudaTXJNI.so ../src/cudatransmit/libCudaTXJNI.so
# cd ../
 
