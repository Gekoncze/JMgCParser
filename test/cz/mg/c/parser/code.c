#include <stdio.h>

typedef void (*Function)();

enum Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
};

union Color {
    int i;
    char c[4];
};

struct FooBar {
    Function f;
    Day d;
};

const FooBar* variable[2];

int main(int argc, char* argv) {
    printf("OK\n");
    return 0;
}