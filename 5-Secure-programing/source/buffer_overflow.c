//
// Created by oliver.holly on 16. 11. 2020.
//

#include <stdio.h>
#include <string.h>

#define MAX_PASSWORD_LENGTH  20

static const char DEFAULT_PASSWORD_PLAIN[] = "edf98gh";

static void vulnerable_function() {
    int password_check = 0;
    char password_buf[MAX_PASSWORD_LENGTH] = {0};

    printf("Enter password: ");
    gets(password_buf);

    if (strcmp(password_buf, DEFAULT_PASSWORD_PLAIN) == 0)
        password_check = 1;

    if(password_check)
        printf("Correct password.");
    else
        printf("Incorrect password.");
}

static void secure_function(){
    char password_buf[MAX_PASSWORD_LENGTH] = {0};

    printf("Enter password: ");
    fgets(password_buf, MAX_PASSWORD_LENGTH, stdin);

    if (strncmp(password_buf, DEFAULT_PASSWORD_PLAIN, MAX_PASSWORD_LENGTH) == 0)
        printf("Correct password.");
    else
        printf("Incorrect password.");

}

//int main() {
////    vulnerable_function();
//    secure_function();
//
//    return 0;
//}