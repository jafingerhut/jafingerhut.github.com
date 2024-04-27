#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <ctype.h>
#include <sys/time.h>
#include <sys/errno.h>

FILE *outf = NULL;
int power_of_2_for_min_block_size = 10;
int power_of_2_for_max_block_size = 30;

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

uint32_t random_sum_vals(uint32_t *vals, uint32_t num_vals, uint32_t index_mask)
{
    uint32_t i;
    uint32_t idx;
    uint32_t sum = 0;

    for (i = 0; i < num_vals; i++) {
        idx = lfsr32a_rand() & index_mask;
        sum += vals[idx];
    }
    return sum;
}

double random_experiment(uint32_t *vals, uint32_t num_vals, uint32_t trials)
{
    uint32_t trial;
    uint32_t total, tmp;
    struct timeval start_time, end_time;
    int ret;
    uint32_t index_mask;

    index_mask = 1;
    while (index_mask < num_vals) {
        index_mask <<= 1;
    }
    if (index_mask != num_vals) {
        fprintf(stderr, "random_experiment() only supports num_vals that is a power of 2 with the power in range [1,31].  num_vals=%u is not supported.\n",
                num_vals);
        exit(1);
    }
    --index_mask;

    init_array(vals, num_vals);
    lfsr32a_set_seed(0xdeadbeef);
    total = random_sum_vals(vals, num_vals, index_mask);

    ret = gettimeofday(&start_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    for (trial = 0; trial < trials; trial++) {
        lfsr32a_set_seed(0xdeadbeef);
        tmp = random_sum_vals(vals, num_vals, index_mask);
        if (tmp != total) {
            fprintf(stderr, "one_experiment expected total %u but got %u instead\n",
                    total, tmp);
            exit(1);
        }
    }
    ret = gettimeofday(&end_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    return time_diff(&start_time, &end_time);
}

uint32_t random2_sum_vals(uint32_t *vals, uint32_t num_vals, uint32_t index_mask)
{
    uint32_t i;
    uint32_t idx;
    uint32_t sum = 0;

    for (i = 0; i < num_vals; i++) {
        idx = lrand48() & index_mask;
        sum += vals[idx];
    }
    return sum;
}

double random2_experiment(uint32_t *vals, uint32_t num_vals, uint32_t trials)
{
    uint32_t trial;
    uint32_t total, tmp;
    struct timeval start_time, end_time;
    int ret;
    uint32_t index_mask;
    unsigned short seed16v[3];

    index_mask = 1;
    while (index_mask < num_vals) {
        index_mask <<= 1;
    }
    if (index_mask != num_vals) {
        fprintf(stderr, "random2_experiment() only supports num_vals that is a power of 2 with the power in range [1,31].  num_vals=%u is not supported.\n",
                num_vals);
        exit(1);
    }
    --index_mask;

    init_array(vals, num_vals);
    seed16v[0] = 0xdead;
    seed16v[1] = 0xbeef;
    seed16v[2] = 0xcafe;
    seed48(seed16v);
    total = random2_sum_vals(vals, num_vals, index_mask);

    ret = gettimeofday(&start_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    for (trial = 0; trial < trials; trial++) {
        seed48(seed16v);
        tmp = random2_sum_vals(vals, num_vals, index_mask);
        if (tmp != total) {
            fprintf(stderr, "one_experiment expected total %u but got %u instead\n",
                    total, tmp);
            exit(1);
        }
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

void print_usage(FILE *f, char *progname) {
    fprintf(f, "%s [ -h ] [-m <power_of_2_for_max_block_size> ] <output_filename>\n", progname);
    fprintf(f, "\n");
    fprintf(f, "    e.g. -m 28 means the maximum block size allocated will be\n");
    fprintf(f, "    2^28 = 256 MBytes in length.\n");
    fflush(f);
}

void parse_args(int argc, char *argv[]) {
    int i = 1;
    int j;
    int remaining_args;
    char *powerstr;
    char *fname;

    while (i < argc && argv[i][0] == '-') {
        switch (argv[i][1]) {
        case 'h':
            print_usage(stderr, argv[0]);
            exit(0);
        case 'm':
            if ((i + 1) >= argc) {
                fprintf(stderr, "Expected a numeric value after '-m' option.\n");
                exit(1);
            }
            powerstr = argv[i+1];
            for (j = 0; powerstr[j] != '\0'; j++) {
                if (! isdigit(powerstr[j])) {
                    fprintf(stderr, "Expected parameter after '-m' option to be a decimal number, but found '%s'\n",
                            powerstr);
                    exit(1);
                }
            }
            power_of_2_for_max_block_size = atoi(powerstr);
            if (power_of_2_for_max_block_size < 10 || power_of_2_for_max_block_size > 31) {
                fprintf(stderr, "Value of '-m' option must be in range [10,31].  Found '%s'\n",
                        powerstr);
                exit(1);
            }
            i += 2;
            break;
        default:
            fprintf(stderr, "Unrecognized command line option '%s'\n",
                    argv[i]);
            print_usage(stderr, argv[0]);
            exit(1);
        }
    }
    remaining_args = argc - i;
    if (remaining_args != 1) {
        fprintf(stderr, "Expecting an output file name but found none\n");
        print_usage(stderr, argv[0]);
        exit(1);
    }
    fname = argv[i];
    outf = fopen(fname, "w");
    if (outf == NULL) {
        fprintf(stderr, "Failed to open file '%s' for writing: %s\n",
                fname, strerror(errno));
        fflush(stderr);
        exit(1);
    }
}


int main(int argc, char *argv[]) {
    int num_sizes;
    int stride_direction;
    size_t sz;
    int num_uint32s;
    uint32_t *vals;
    int num_strides = 9;
    uint32_t stride;
    uint32_t j;
    uint32_t num_trials;
    double elapsed_time;
    size_t min_block_size, max_block_size;

    parse_args(argc, argv);
    print_type_sizes(outf);

    fprintf(stderr, "\n");
    min_block_size = 1ULL << power_of_2_for_min_block_size;
    max_block_size = 1ULL << power_of_2_for_max_block_size;

    num_sizes = (power_of_2_for_max_block_size
                 - power_of_2_for_min_block_size + 1);
    fprintf(stderr, "%d different block sizes to test in this range:\n",
            num_sizes);
    fprintf(stderr, "min block size: %lu bytes\n", min_block_size);
    fprintf(stderr, "max block size: %lu bytes\n", max_block_size);

    //for (stride_direction = -1; stride_direction <= 1; stride_direction++) {
    for (stride_direction = 0; stride_direction <= 0; stride_direction++) {
        fprintf(stderr, "\nstride_direction\t%d\n", stride_direction);
        fprintf(outf, "\nstride_direction\t%d\n", stride_direction);
        fprintf(outf, "size_bytes");
        if (stride_direction != 0) {
            for (j = 0; j < num_strides; j++) {
                fprintf(outf, "\t%u", get_stride(j));
            }
        }
        fprintf(outf, "\n");
        for (sz = min_block_size; sz <= max_block_size; sz <<= 1) {
            num_uint32s = sz / sizeof(uint32_t);
            vals = (uint32_t *) alloc_block(sz);
            num_trials = (1024 * 1024 * 1024) / sz;
            fprintf(outf, "%lu", sz);
            if (stride_direction == 0) {
                // Use pseudo-random order to access the array indices.
                elapsed_time = random2_experiment(vals, num_uint32s, num_trials);
                fprintf(outf, "\t%.6f", elapsed_time);
                fprintf(stderr, "%lu\t%u\t%.6f\n",
                        sz, num_trials, elapsed_time);
            } else {
                for (j = 0; j < num_strides; j++) {
                    stride = get_stride(j);
                    if (stride > (num_uint32s / 2)) {
                        fprintf(outf, "\t-");
                        fprintf(stderr, "%lu\t%u\t%u\t--\t(stride is more than half the number of elements)\n",
                                sz, stride, num_trials);
                    } else {
                        elapsed_time = one_experiment(vals, num_uint32s,
                                                      stride, stride_direction,
                                                      num_trials);
                        fprintf(outf, "\t%.6f", elapsed_time);
                        fprintf(stderr, "%lu\t%u\t%u\t%.6f\n",
                                sz, stride, num_trials, elapsed_time);
                    }
                }
            }
            fprintf(outf, "\n");
            fflush(outf);
            fflush(stderr);
            free(vals);
        }
    }
    fclose(outf);
    exit(0);
}
