//
// Created by oliver.holly on 16. 11. 2020.
//

#include <stdio.h>

#define MAX_INPUT_LENGTH  20

static void vulnerable_function() {
    char user_input[MAX_INPUT_LENGTH] = {0};

    printf("Input: ");
    fgets(user_input, MAX_INPUT_LENGTH, stdin);

    printf(user_input);
}

static void secure_function(){
    char user_input[MAX_INPUT_LENGTH] = {0};

    printf("Input: ");
    fgets(user_input, MAX_INPUT_LENGTH, stdin);

    printf("%s", user_input);
}

//int main() {
////    vulnerable_function();
//    secure_function();
//
//    return 0;
//}