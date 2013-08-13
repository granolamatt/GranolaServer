//
//  How to access GPIO registers from C-code on the Raspberry-Pi
//  Example program
//  15-January-2012
//  Dom and Gert
//  Revised: 15-Feb-2013


// Access from ARM Running Linux

#define BCM2708_PERI_BASE        0x20000000
#define GPIO_BASE                (BCM2708_PERI_BASE + 0x200000) /* GPIO controller */
#define BSC0_BASE     			 (BCM2708_PERI_BASE + 0x205000)  // I2C controller 

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <unistd.h>
#include <pwd.h>

#define PAGE_SIZE (4*1024)
#define BLOCK_SIZE (4*1024)

/*
 * Class:     com_granolamatt_hardware_HardwareMemory
 * Method:    setupIO
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_granolamatt_hardware_HardwareMemory_setupIO
  (JNIEnv *env, jclass class) {
   int  mem_fd;
   void *gpio_map;
   volatile unsigned *gpio;
   int ures, gres;
   struct passwd *p;

   /* open /dev/mem */
   if ((mem_fd = open("/dev/mem", O_RDWR|O_SYNC) ) < 0) {
      printf("can't open /dev/mem \n");
      exit(-1);
   }

   /* mmap GPIO */
   gpio_map = mmap(
      NULL,             //Any adddress in our space will do
      BLOCK_SIZE,       //Map length
      PROT_READ|PROT_WRITE,// Enable reading & writting to mapped memory
      MAP_SHARED,       //Shared with other processes
      mem_fd,           //File to map
      GPIO_BASE         //Offset to GPIO peripheral
   );

   close(mem_fd); //No need to keep mem_fd open after mmap

   if (gpio_map == MAP_FAILED) {
      printf("mmap error \n");//errno also set!
      exit(-1);
   }

   return (*env)->NewDirectByteBuffer(env, gpio_map, BLOCK_SIZE);

} // setup_io

/*
 * Class:     com_granolamatt_hardware_HardwareMemory
 * Method:    setupI2C
 * Signature: ()Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_granolamatt_hardware_HardwareMemory_setupI2C
  (JNIEnv *env, jclass class) {
   int  mem_fd;
   void *bsc0_map;
   volatile unsigned *bsc0;
   int ures, gres;
   struct passwd *p;

   /* open /dev/mem */
   if ((mem_fd = open("/dev/mem", O_RDWR|O_SYNC) ) < 0) {
      printf("can't open /dev/mem \n");
      exit(-1);
   }

   /* mmap GPIO */
   bsc0_map = mmap(
      NULL,             //Any adddress in our space will do
      BLOCK_SIZE,       //Map length
      PROT_READ|PROT_WRITE,// Enable reading & writting to mapped memory
      MAP_SHARED,       //Shared with other processes
      mem_fd,           //File to map
      BSC0_BASE         //Offset to I2C peripheral
   );

   close(mem_fd); //No need to keep mem_fd open after mmap

   if (bsc0_map == MAP_FAILED) {
      printf("mmap error \n");//errno also set!
      exit(-1);
   }

   return (*env)->NewDirectByteBuffer(env, bsc0_map, BLOCK_SIZE);

} // setup_i2c

/*
 * Class:     com_granolamatt_hardware_HardwareMemory
 * Method:    downUser
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_granolamatt_hardware_HardwareMemory_downUser
  (JNIEnv *env, jclass class, jstring user) {
   int ures, gres;
   struct passwd *p;

  const char *nativeString = (*env)->GetStringUTFChars(env, user, 0);

  if ((p = getpwnam(nativeString)) == NULL) {
    perror("getpwnam() error");
    exit(-1);
  } else {
    printf("getpwnam() returned the following info for user :\n");
    printf("  pw_name  : %s\n",       p->pw_name);
    printf("  pw_uid   : %d\n", (int) p->pw_uid);
    printf("  pw_gid   : %d\n", (int) p->pw_gid);
    printf("  pw_dir   : %s\n",       p->pw_dir);
    printf("  pw_shell : %s\n",       p->pw_shell);
  } 

   // Downgrade the uid and gid to pi 
   gres = setgid(p->pw_gid);
   ures = setuid(p->pw_uid);
   printf("URES %d GRES %d \n",ures,gres);
   (*env)->ReleaseStringUTFChars(env, user, nativeString);

} // downUser

