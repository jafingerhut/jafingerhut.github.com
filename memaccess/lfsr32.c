#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>

void mu_lfsr_gal_two_length()
{
    uint32_t bits = 0xffffffff;
    uint64_t count = 0;
    uint32_t mask = (1U << 31) | (1 << 29) | (1 << 25) | (1 << 24);

    printf("\nmu_lfsr_gal_two_length()\n");
    do {
        ++count;
        bits = (bits >> 1) ^ (-(bits & 1) & mask);
        if (count < 20 || count > (0xffffffff - 20)) {
            printf("%08x\n", bits);
        }
    } while (bits != 0xffffffff);
    printf("count=%" PRIu64 "\n", count);
}

const uint32_t lfsr32a_mask = (1U << 31) | (1 << 29) | (1 << 25) | (1 << 24);
uint32_t lfsr32a_state;

void lfsr32a_set_seed(uint32_t seed) {
    lfsr32a_state = seed;
}

inline uint32_t lfsr32a_rand() {
    lfsr32a_state = ((lfsr32a_state >> 1)
                     ^ (-(lfsr32a_state & 1) & lfsr32a_mask));
    return lfsr32a_state;
}

void mu_lfsr_gal_two_length_v2()
{
    uint32_t bits;
    uint64_t count = 0;
    uint64_t count_diff_0 = 0;
    uint32_t rand, prev_rand, diff;
    uint64_t sum = 0;
    uint32_t min_diff = 0xffffffff;
    uint32_t max_diff = 0;

    printf("\nmu_lfsr_gal_two_length_v2()\n");
    lfsr32a_set_seed(0xffffffff);
    // Next line does not affect code behavior, but does avoid
    // compiler warning about prev_rand possibly being used
    // uninitialized.
    prev_rand = 0;
    do {
        ++count;
        bits = lfsr32a_rand();
        if (count < 20 || count > (0xffffffff - 20)) {
            printf("%08x\n", bits);
        }
        rand = bits & 0xffff;
        if (count > 1) {
            if (rand > prev_rand) {
                diff = rand - prev_rand;
            } else {
                diff = prev_rand - rand;
            }
            sum += diff;
            if (diff < min_diff) min_diff = diff;
            if (diff > max_diff) max_diff = diff;
            if (diff == 0) {
                ++count_diff_0;
                if (count_diff_0 == 1) {
                    printf("diff=%u prev_rand=%u rand=%u\n",
                           diff, prev_rand, rand);
                }
            }
        }
        prev_rand = rand;
    } while (bits != 0xffffffff);
    printf("count=%" PRIu64 "\n", count);
    printf("average absolute difference between consecutive samples=%.1f\n",
           (double) sum / count);
    printf("min diff=%u\n", min_diff);
    printf("max diff=%u\n", max_diff);
    printf("# of times the difference between consecutive random numbers was 0=%" PRIu64 "\n",
           count_diff_0);
}

int main(int argc, char *argv[]) {
    //mu_lfsr_gal_two_length();
    mu_lfsr_gal_two_length_v2();
    exit(0);
}
