#include <stdio.h>

#ifdef INTERNAL_CONDITION
    int existing;
#else
    int missing;
#endif

#define INTERNAL_CONDITION

#ifdef INTERNAL_CONDITION
    int internalTrue;
#else
    int internalFalse;
#endif

#ifdef EXTERNAL_CONDITION
    int externalTrue;
#else
    int externalFalse;
#endif

#if 0
    int none;
#endif

/*
    int voidWalker;
*/

// don't forget to hydrate \
int sneaky

#define AVERAGE(x, y) ((x + y) \
    / 2)

#if defined(AVERAGE) && (defined(INTERNAL_CONDITION) || defined(EXTERNAL_CONDITION))
    int allDefined;
#endif

#define FALSE 0
#define TRUE 1

#if FALSE
    enum wrong;
#elif TRUE
    enum correct {
        ANSWER = AVERAGE(8, 4)
    };
#endif

int main(int argc, char* argv) {
    return 0;
}