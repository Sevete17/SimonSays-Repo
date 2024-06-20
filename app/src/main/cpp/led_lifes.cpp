#include <jni.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <assert.h>

extern "C" JNIEXPORT void JNICALL
Java_com_example_simonsays_MainActivity_initializeLED(JNIEnv* env, jobject /* this */) {
    // Open LED control device
    int fd_led = open("/dev/fpga_led", O_WRONLY);
    assert(fd_led != -1);

    // Define LED pattern to turn on 3 LED lights
    unsigned char led_pattern = 0x07; // Binary pattern to turn on 3 LED lights (0b00000111)

    // Update LED pattern
    if (write(fd_led, &led_pattern, 1) == -1) {
        close(fd_led);
    }

    // Close the file descriptor
    close(fd_led);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_simonsays_MainActivity_updateLEDOnLEDController(
        JNIEnv* env,
        jobject /* this */,
        jint lifes_remaining) {

    // Open LED control device
    int fd_led = open("/dev/fpga_led", O_RDWR);
    assert(fd_led != -1);

    // Define LED pattern based on remaining lives
    unsigned char led_pattern;
    switch (lifes_remaining) {
        case 3:
            led_pattern = 0x07; // Example pattern for 3 lives remaining
            break;
        case 2:
            led_pattern = 0x03; // Example pattern for 2 lives remaining
            break;
        case 1:
            led_pattern = 0x01; // Example pattern for 1 life remaining
            break;
        default:
            led_pattern = 0x00; // Default pattern (LED off)
            break;
    }

    // Update LED pattern
    if (write(fd_led, &led_pattern, 1) == -1) {
        close(fd_led);
    }

    // Close the file descriptor
    close(fd_led);

    return env->NewStringUTF("");
}
