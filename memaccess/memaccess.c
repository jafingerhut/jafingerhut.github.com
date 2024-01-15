#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <sys/time.h>
#include <sys/errno.h>

#undef ALLOC_AT_MOST_512MBYTES

size_t sizes_bytes[] = {
    256,
    1 * 1024,
    2 * 1024,
    4 * 1024,
    8 * 1024,
    16 * 1024,
    32 * 1024,
    64 * 1024,
    128 * 1024,
    256 * 1024,
    512 * 1024,
    1 * 1024 * 1024,
    2 * 1024 * 1024,
    4 * 1024 * 1024,
    8 * 1024 * 1024,
    16 * 1024 * 1024,
    32 * 1024 * 1024,
    64 * 1024 * 1024,
    128 * 1024 * 1024,
    256 * 1024 * 1024,
    512 * 1024 * 1024,
#ifdef ALLOC_AT_MOST_512MBYTES
    1 * 1024 * 1024 * 1024,
#endif  // ALLOC_AT_MOST_512MBYTES
};

void print_type_sizes(FILE *f) {
    fprintf(f, "Size of some signed C types, in bytes:\n");
    fprintf(f, "%2lu char\n", sizeof(char));
    fprintf(f, "%2lu short\n", sizeof(short));
    fprintf(f, "%2lu int\n", sizeof(int));
    fprintf(f, "%2lu long\n", sizeof(long));
    fprintf(f, "%2lu long long\n", sizeof(long long));
    fprintf(f, "%2lu uint8_t\n", sizeof(uint8_t));
    fprintf(f, "%2lu uint16_t\n", sizeof(uint16_t));
    fprintf(f, "%2lu uint32_t\n", sizeof(uint32_t));
    fprintf(f, "%2lu uint64_t\n", sizeof(uint64_t));
    //fprintf(f, "%2lu uint128_t\n", sizeof(uint128_t));
    fprintf(f, "Size of some unsigned C types, in bytes:\n");
    fprintf(f, "%2lu unsigned char\n", sizeof(unsigned char));
    fprintf(f, "%2lu unsigned short\n", sizeof(unsigned short));
    fprintf(f, "%2lu unsigned int\n", sizeof(unsigned int));
    fprintf(f, "%2lu unsigned long\n", sizeof(unsigned long));
    fprintf(f, "%2lu unsigned long long\n", sizeof(unsigned long long));
    fprintf(f, "%2lu size_t\n", sizeof(size_t));
    fprintf(f, "Size of pointers, in bytes:\n");
    fprintf(f, "%2lu void *\n", sizeof(void *));
    fprintf(f, "%2lu char *\n", sizeof(char *));
    fflush(f);
}

char *alloc_block(size_t sz) {
    char *block = malloc(sz);
    if (block == NULL) {
        fprintf(stderr, "Failed to allocate block of %lu bytes\n", sz);
        exit(1);
    }
    return block;
}

void init_array(uint32_t *vals, uint32_t num_vals) {
    uint32_t i;
    for (i = 0; i < num_vals; i++) {
        vals[i] = i;
    }
}

uint32_t sum_vals(uint32_t *vals, uint32_t num_vals, uint32_t stride,
                  int stride_direction)
{
    uint32_t i, j;
    uint32_t sum = 0;
    if (stride_direction > 0) {
        if (stride == 1) {
            for (i = 0; i < num_vals; i++) {
                sum += vals[i];
            }
            return sum;
        }
        for (i = 0; i < stride; i++) {
            for (j = i; j < num_vals; j += stride) {
                sum += vals[j];
            }
        }
        return sum;
    } else {
        if (stride == -1) {
            for (i = num_vals; i != 0;) {
                --i;
                sum += vals[i];
            }
            return sum;
        }
        for (i = 0; i < stride; i++) {
            for (j = num_vals + i; j >= stride;) {
                j -= stride;
                sum += vals[j];
            }
        }
        return sum;
    }
}

/* Return t2 - t1.  Assumes t2 is after t1. */
double time_diff(struct timeval *t1, struct timeval *t2)
{
    time_t sec1 = t1->tv_sec;
    time_t sec2 = t2->tv_sec;
    suseconds_t usec1 = t1->tv_usec;
    suseconds_t usec2 = t2->tv_usec;

    time_t diff_sec = sec2 - sec1;
    if (usec2 < usec1) {
        diff_sec -= 1;
        usec2 += 1000000;
    }
    return ((usec2 - usec1) / 1000000.0) + (double) diff_sec;
}

double one_experiment(uint32_t *vals, uint32_t num_vals, uint32_t stride,
                      int stride_direction, uint32_t trials)
{
    uint32_t trial;
    uint32_t total, tmp;
    struct timeval start_time, end_time;
    int ret;

    if ((num_vals % stride) != 0) {
        fprintf(stderr, "one_experiment only intended to work if stride divides num_vals evenly, but (%u %% %u) = %u\n",
                num_vals, stride, (num_vals % stride));
        exit(1);
    }

    init_array(vals, num_vals);
    total = sum_vals(vals, num_vals, 1, 1);

    ret = gettimeofday(&start_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    for (trial = 0; trial < trials; trial++) {
        tmp = sum_vals(vals, num_vals, stride, stride_direction);
        if (tmp != total) {
            fprintf(stderr, "one_experiment expected total %u but got %u instead\n",
                    total, tmp);
            exit(1);
        }
        /*
        if (((trial + 1) % 1000) == 0) {
            printf("finished %u trials ...\n", trial + 1);
        }
        */
    }
    ret = gettimeofday(&end_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    return time_diff(&start_time, &end_time);
}

uint32_t get_stride(int idx) {
    uint32_t stride;
    switch (idx) {
    case 0: stride = 1; break;
    case 1: stride = 4; break;
    case 2: stride = 16; break;
    case 3: stride = 32; break;
    case 4: stride = 64; break;
    case 5: stride = 128; break;
    case 6: stride = 256; break;
    case 7: stride = 512; break;
    case 8: stride = 1024; break;
    default:
        fprintf(stderr, "unknown stride code point %d\n",
                idx);
        exit(1);
    }
    return stride;
}


int main(int argc, char *argv[]) {
    int num_sizes;
    int stride_direction;
    size_t sz;
    int num_uint32s;
    uint32_t *vals;
    int num_strides = 9;
    uint32_t stride;
    uint32_t i, j;
    uint32_t num_trials;
    double elapsed_time;

    print_type_sizes(stdout);

    num_sizes = sizeof(sizes_bytes) / sizeof(size_t);
    printf("%d elements in array sizes_bytes\n", num_sizes);

    for (stride_direction = -1; stride_direction <= 1; stride_direction += 2) {
        printf("\nstride_direction\t%d\n", stride_direction);
        printf("size_bytes");
        for (j = 0; j < num_strides; j++) {
            printf("\t%u", get_stride(j));
        }
        printf("\n");
        for (i = 0; i < num_sizes; i++) {
            sz = sizes_bytes[i];
            num_uint32s = sz / sizeof(uint32_t);
            vals = (uint32_t *) alloc_block(sz);
            printf("%lu", sz);
            for (j = 0; j < num_strides; j++) {
                stride = get_stride(j);
                num_trials = (1024 * 1024 * 1024) / sz;
                if (stride > (num_uint32s / 2)) {
                    printf("\t-");
                    fprintf(stderr, "%lu\t%u\t%u\t--\t(stride is more than half the number of elements)\n",
                            sz, stride, num_trials);
                } else {
                    elapsed_time = one_experiment(vals, num_uint32s, stride,
                                                  stride_direction, num_trials);
                    printf("\t%.6f", elapsed_time);
                    fprintf(stderr, "%lu\t%u\t%u\t%.6f\n",
                            sz, stride, num_trials, elapsed_time);
                }
            }
            printf("\n");
            fflush(stdout);
            fflush(stderr);
            free(vals);
        }
    }
    exit(0);
}
    
