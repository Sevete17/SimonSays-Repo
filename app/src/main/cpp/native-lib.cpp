#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <assert.h>
#include <time.h>
#include <iostream>
#include <fstream>
#include <cassert>
#include <ctime>

#define TEXTLCD_CLEAR 4
#define TEXTLCD_LINE1 5
#define TEXTLCD_LINE2 6

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_simonsays_MainActivity_updateScoreOnLCD(
        JNIEnv* env,
        jobject /* this */,
        jstring line1,
        jstring line2) {

    int fd_text_lcd = open("/dev/fpga_textlcd", O_WRONLY);

    // Clear the LCD
    ioctl(fd_text_lcd, TEXTLCD_CLEAR);

    // Add a delay after sending the clear command
    usleep(100000); // Sleep for 100 milliseconds (adjust as needed)

    const char* line1_text = env->GetStringUTFChars(line1, JNI_FALSE);
    const char* line2_text = env->GetStringUTFChars(line2, JNI_FALSE);

    // Write text to line 1
    ioctl(fd_text_lcd, TEXTLCD_LINE1);
    write(fd_text_lcd, line1_text, strlen(line1_text));

    // Write text to line 2
    ioctl(fd_text_lcd, TEXTLCD_LINE2);
    write(fd_text_lcd, line2_text, strlen(line2_text));

    // Release the UTF chars
    env->ReleaseStringUTFChars(line1, line1_text);
    env->ReleaseStringUTFChars(line2, line2_text);

    // Close the file descriptor
    close(fd_text_lcd);

    return env->NewStringUTF("");
}
