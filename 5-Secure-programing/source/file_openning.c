//
// Created by oliver.holly on 16. 11. 2020.
//

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>


#define MY_TMP_FILE "/tmp/file.tmp"
#define FILE_MODE  0600

static int vulnerable_function() {
    if (!access(MY_TMP_FILE, F_OK)) {
        printf("File exists!\n");
        return EXIT_FAILURE;
    }
    /* At this point the attacker creates a symlink from /tmp/file.tmp to /etc/passwd */
    FILE * tmpFile = fopen(MY_TMP_FILE, "w");
    if (tmpFile == NULL) {
        return EXIT_FAILURE;
    }
    fputs("Some text...\n", tmpFile);
    fclose(tmpFile);

    return 0;
}

static int secure_function(){
    int fd;
    FILE* f;

    /* Remove possible symlinks */
    unlink(MY_TMP_FILE);
    /* Open, but fail if someone raced us and restored the symlink (secure version of fopen(path, "w") */
    fd = open(MY_TMP_FILE, O_WRONLY|O_CREAT|O_EXCL, FILE_MODE);
    if (fd == -1) {
        perror("Failed to open the file");
        return EXIT_FAILURE;
    }
    /* Get a FILE*, as they are easier and more efficient than plan file descriptors */
    f = fdopen(fd, "w");
    if (f == NULL) {
        perror("Failed to associate file descriptor with a stream");
        return EXIT_FAILURE;
    }
    fprintf(f, "Hello, world\n");
    fclose(f);
    /* fd is already closed by fclose()!!! */
    return EXIT_SUCCESS;

}

//int main() {
//    vulnerable_function();
////    secure_function();
//
//    return 0;
//}
