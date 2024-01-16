#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>
#include <string.h>
#include <ctype.h>
#include <sys/time.h>
#include <sys/errno.h>

FILE *outf = NULL;
int power_of_2_for_min_block_size = 10;
int power_of_2_for_max_block_size = 30;
int debug_level = 0;

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
    fprintf(f, "%2lu uintptr_t\n", sizeof(uintptr_t));
    fprintf(f, "%2lu size_t\n", sizeof(size_t));
    fprintf(f, "Size of pointers, in bytes:\n");
    fprintf(f, "%2lu void *\n", sizeof(void *));
    fprintf(f, "%2lu char *\n", sizeof(char *));
    fprintf(f, "%2lu uintptr_t *\n", sizeof(uintptr_t));
    fprintf(f, "PRIx64 format string=:%s:\n", PRIx64);
    fprintf(f, "PRIX64 format string=:%s:\n", PRIX64);
    fprintf(f, "PRId64 format string=:%s:\n", PRId64);
    fprintf(f, "PRIu64 format string=:%s:\n", PRIu64);
    fprintf(f, "PRIuPTR format string=:%s:\n", PRIuPTR);
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

uintptr_t follow_pointers(uintptr_t *ptrs, uintptr_t count) {
    uintptr_t ptr = ptrs[0];

    while (count != 0) {
        --count;
        ptr = *((uintptr_t *) ptr);
    }
    return (((uintptr_t *) ptr) - ptrs);
}

uintptr_t cycle_len_starting_at_index(uintptr_t *ptrs, uintptr_t idx) {
    uintptr_t count = 0;
    uintptr_t ptr = ptrs[idx];
    uintptr_t start = ptr;
    uintptr_t tmp;
    if (debug_level >= 2) {
        fprintf(stderr, "dbg cycle_len start %" PRIuPTR "\n", start);
        tmp = ((uintptr_t *) ptr) - ptrs;
        fprintf(stderr, "dbg cycle_len count %10" PRIuPTR " ptr %" PRIuPTR
                " tmp %" PRIuPTR "\n",
                count, ptr, tmp);
    }
    do {
        ++count;
        ptr = *((uintptr_t *) ptr);
        if (debug_level >= 2) {
            tmp = ((uintptr_t *) ptr) - ptrs;
            fprintf(stderr, "dbg cycle_len count %10" PRIuPTR " ptr %" PRIuPTR
                    " tmp %" PRIuPTR "\n",
                    count, ptr, tmp);
        }
    } while (ptr != start);
    return count;
}

void debug_print_ptrs(uintptr_t *ptrs, uintptr_t num_ptrs) {
    uintptr_t i;
    uintptr_t idx_of_ptr;

    for (i = 0; i < num_ptrs; i++) {
        idx_of_ptr = ((uintptr_t *) ptrs[i]) - ptrs;
        fprintf(stderr, "idx %10" PRIuPTR " ptrs[idx] as index %10" PRIuPTR "\n",
                i, idx_of_ptr);
    }
}

void warn_if_cycle_too_short(uintptr_t *ptrs, uintptr_t num_ptrs) {
    uintptr_t cycle_len;

    cycle_len = cycle_len_starting_at_index(ptrs, 0);
    if (cycle_len != num_ptrs) {
        debug_print_ptrs(ptrs, num_ptrs);
        fprintf(stderr, "\n");
        fprintf(stderr, "Cycle len starting at index %" PRIuPTR
                " is %" PRIuPTR,
                (uintptr_t) 0, cycle_len);
        fprintf(stderr, "   WARNING not equal to num_ptrs=%" PRIuPTR,
                num_ptrs);
        fprintf(stderr, "\n");
        exit(1);
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

void init_ptrs_with_constant_stride(uintptr_t *ptrs, uintptr_t num_ptrs,
                                    uintptr_t stride, int stride_direction)
{
    uintptr_t i, j;
    uintptr_t *prev_loc;
    uintptr_t prev_loc_idx;

    if (debug_level >= 2) {
        fprintf(stderr, "init_ptrs_with_constant_stride num_ptrs %" PRIuPTR
                " stride %" PRIuPTR " stride_direction %d\n",
                num_ptrs, stride, stride_direction);
    }
    if ((num_ptrs % stride) != 0) {
        fprintf(stderr, "init_ptrs_with_constant_stride only intended to work if stride divides num_ptrs evenly, but (%" PRIuPTR " %% %" PRIuPTR ") = %" PRIuPTR "\n",
                num_ptrs, stride, (num_ptrs % stride));
        exit(1);
    }
    // Example: num_ptrs 4, stride 2
    // i 0
    //      j 0
    //      j 2
    //      j 4 (do not enter inner loop - exit)
    // i 1
    //      j 1
    //      j 3
    //      j 5 (do not enter inner loop - exit)
    // i 2 (do not enter outer loop - exit)
    prev_loc = &(ptrs[0]);
    if (stride_direction > 0) {
        for (i = 0; i < stride; i++) {
            for (j = i; j < num_ptrs; j += stride) {
                *prev_loc = (uintptr_t) &(ptrs[j]);
                prev_loc = &(ptrs[j]);
            }
        }
        // Set last entry to point back at the first
        *prev_loc = (uintptr_t) &(ptrs[0]);
    } else {
        for (i = 0; i < stride; i++) {
            for (j = num_ptrs + i; j >= stride;) {
                j -= stride;
                if (j > 0) {
                    if (debug_level >= 2) {
                        prev_loc_idx = prev_loc - ptrs;
                        fprintf(stderr, "Assigning pointer at idx %10" PRIuPTR " to point at index %10" PRIuPTR "\n",
                                prev_loc_idx, j);
                    }
                    *prev_loc = (uintptr_t) &(ptrs[j]);
                    prev_loc = &(ptrs[j]);
                }
            }
        }
        // Set last entry to point back at the first
        if (debug_level >= 2) {
            prev_loc_idx = prev_loc - ptrs;
            fprintf(stderr, "Assigning pointer at idx %10" PRIuPTR " to point at index %10" PRIuPTR "\n",
                    prev_loc_idx, (uintptr_t) 0);
        }
        *prev_loc = (uintptr_t) &(ptrs[0]);
    }
}

double one_experiment(uintptr_t *ptrs, uintptr_t trials)
{
    uintptr_t end_index;
    struct timeval start_time, end_time;
    int ret;

    ret = gettimeofday(&start_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    end_index = follow_pointers(ptrs, trials);
    if (debug_level >= 1) {
        fprintf(stderr, "After %" PRIuPTR " pointer followings reached index %" PRIuPTR "\n",
                trials, end_index);
    }
    ret = gettimeofday(&end_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    return time_diff(&start_time, &end_time);
}

/*
double random_experiment(uint32_t *ptrs, uint32_t num_ptrs, uint32_t trials)
{
    uint32_t trial;
    uint32_t total, tmp;
    struct timeval start_time, end_time;
    int ret;
    uint32_t index_mask;

    index_mask = 1;
    while (index_mask < num_ptrs) {
        index_mask <<= 1;
    }
    if (index_mask != num_ptrs) {
        fprintf(stderr, "random_experiment() only supports num_ptrs that is a power of 2 with the power in range [1,31].  num_ptrs=%u is not supported.\n",
                num_ptrs);
        exit(1);
    }
    --index_mask;

    init_array(ptrs, num_ptrs);
    lfsr32a_set_seed(0xdeadbeef);
    total = random_sum_vals(ptrs, num_ptrs, index_mask);

    ret = gettimeofday(&start_time, NULL);
    if (ret != 0) {
        fprintf(stderr, "gettimeofday() returned %d with errno %d: %s",
                ret, errno, strerror(errno));
        exit(1);
    }
    for (trial = 0; trial < trials; trial++) {
        lfsr32a_set_seed(0xdeadbeef);
        tmp = random_sum_vals(ptrs, num_ptrs, index_mask);
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
*/

void init_ptrs_random_pattern_1(uintptr_t *ptrs, uintptr_t num_ptrs)
{
    uintptr_t i, next_idx;
    unsigned short seed16v[3];
    uintptr_t num_remaining;
    uintptr_t tmp;
    uintptr_t *P;
    uintptr_t cur_idx;

    // Ideally I would like to generate a permutation of the indices
    // in [0, num_ptrs-1] such that if we call follow_pointers() on
    // the resulting array ptrs, it will cycle exactly once through
    // each location, returning back to index 0 at the end.
    //
    // There might be a way to do this with only (num_ptrs *
    // sizeof(uintptr_t)), memory and linear time, but if so, I have
    // not thought of one yet.
    //
    // The next best idea I have is to do it with twice that much
    // memory and linear time.
    //
    // Step 1 is to create a random permutation 'P' of the values in
    // [1, num_ptrs-1], and append 0 to the end of that.
    //
    // The desired order of visiting indices is then 0, P[0], P[1],
    // P[2], ..., P[num_ptrs-2], P[num_ptrs-1], where P[num_ptrs-1]=0.
    //
    // Step 2 is to then use that array, without modifying it, to
    // initialize the array ptrs.

    //////////////////////////////////////////////////////////////
    // Step 1
    //////////////////////////////////////////////////////////////
    P = (uintptr_t *) calloc(num_ptrs, sizeof(uintptr_t));
    if (P == NULL) {
        fprintf(stderr, "Failed to allocate block of %" PRIuPTR " bytes\n",
                num_ptrs * sizeof(uintptr_t));
        exit(1);
    }
    // Create array of only values [1,num_ptrs-1], intentionally
    // omitting 0, which we know we want to be at the very end.
    for (i = 0; i < num_ptrs-1; i++) {
        P[i] = i+1;
    }
    /* Now randomly permute values in [1,num_ptr-1] */
    seed16v[0] = 0xdead;
    seed16v[1] = 0xbeef;
    seed16v[2] = 0xcafe;
    seed48(seed16v);
    num_remaining = num_ptrs-1;
    for (i = 0; i < num_ptrs-1; i++) {
        num_remaining = (num_ptrs - 1 - i);
        next_idx = i + (lrand48() % num_remaining);
        tmp = P[i];
        P[i] = P[next_idx];
        P[next_idx] = tmp;
    }
    /* Put 0 at the end. */
    P[num_ptrs-1] = 0;

    //////////////////////////////////////////////////////////////
    // Step 2
    //////////////////////////////////////////////////////////////
    cur_idx = 0;
    for (i = 0; i < num_ptrs; i++) {
        ptrs[cur_idx] = P[i];
        cur_idx = P[i];
    }
    free(P);

    /* Sanity check that all index values are in range, and ptrs[i] !=
     * i for all i. */
    if (debug_level >= 2) {
        fprintf(stderr, "dbg init_ptrs_random_pattern_1:\n");
    }
    for (i = 0; i < num_ptrs; i++) {
        if (ptrs[i] >= num_ptrs) {
            fprintf(stderr, "ptrs[%" PRIuPTR "] = %lu >= %" PRIuPTR " = num_ptrs\n",
                    i, ptrs[i], num_ptrs);
            exit(1);
        }
        if (ptrs[i] == i) {
            fprintf(stderr, "ptrs[%" PRIuPTR "] = %lu = %" PRIuPTR "\n",
                    i, ptrs[i], i);
            exit(1);
        }
        if (debug_level >= 2) {
            fprintf(stderr, "dbg ptrs[%10" PRIuPTR "] = %10lu\n",
                    i, ptrs[i]);
        }
    }

    /* Replace all indices with pointers. */
    for (i = 0; i < num_ptrs; i++) {
        tmp = ptrs[i];
        ptrs[i] = (uintptr_t) &(ptrs[tmp]);
    }

    /* Sanity check that all pointers are within the bounds of the
     * array. */
    for (i = 0; i < num_ptrs; i++) {
        if ((((uintptr_t *) ptrs[i]) < ptrs) ||
            (((uintptr_t *) ptrs[i]) > &(ptrs[num_ptrs-1])))
        {
            fprintf(stderr, "ptrs[%" PRIuPTR "] = %lu is outside of array bounds of [%lu, %lu]\n",
                    i, ptrs[i], (uintptr_t) ptrs,
                    (uintptr_t) &(ptrs[num_ptrs-1]));
            exit(1);
        }
    }

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
    size_t sz;
    uintptr_t num_ptrs;
    uintptr_t *ptrs;
    int num_access_patterns = 3;
    int access_pattern;
    int constant_stride = 0;
    int num_strides = 9;
    int stride_direction = 1;
    uintptr_t stride;
    uintptr_t j;
    uintptr_t num_trials;
    double elapsed_time;
    double avg_nsec_per_iteration;
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

    for (access_pattern = 0; access_pattern < num_access_patterns; access_pattern++) {
        fprintf(stderr, "\naccess_pattern\t%d\n", access_pattern);
        fprintf(outf, "\naccess_pattern\t%d\n", access_pattern);
        constant_stride = 0;
        switch (access_pattern) {
        case 0:
            // Constant stride, negative
            constant_stride = 1;
            stride_direction = -1;
            break;
        case 1:
            // Constant stride, positive
            constant_stride = 1;
            stride_direction = 1;
            break;
        case 2:
            // Pseudo-random pattern #1
            break;
        default:
            fprintf(stderr, "Unsupported value access_pattern=%d\n",
                    access_pattern);
            exit(1);
            break;
        }
        if (constant_stride) {
            fprintf(stderr, "\nstride_direction\t%d\n", stride_direction);
            fprintf(outf, "\nstride_direction\t%d\n", stride_direction);
        }
        fprintf(outf, "size_bytes");
        if (constant_stride) {
            for (j = 0; j < num_strides; j++) {
                fprintf(outf, "\t%u", get_stride(j));
            }
        }
        fprintf(outf, "\n");
        for (sz = min_block_size; sz <= max_block_size; sz <<= 1) {
            num_ptrs = sz / sizeof(uintptr_t *);
            ptrs = (uintptr_t *) alloc_block(sz);
            num_trials = (256 * 1024 * 1024);
            fprintf(outf, "%lu", sz);
            if (constant_stride) {
                for (j = 0; j < num_strides; j++) {
                    stride = get_stride(j);
                    if (stride > (num_ptrs / 2)) {
                        fprintf(outf, "\t-");
                        fprintf(stderr, "%lu\t%" PRIuPTR "\t%" PRIuPTR "\t--\t(stride is more than half the number of elements)\n",
                                sz, stride, num_trials);
                    } else {
                        init_ptrs_with_constant_stride(ptrs, num_ptrs,
                                                       stride, stride_direction);
                        warn_if_cycle_too_short(ptrs, num_ptrs);
                        elapsed_time = one_experiment(ptrs, num_trials);
                        avg_nsec_per_iteration = ((1000000000.0 * elapsed_time)
                                                  / num_trials);
                        fprintf(outf, "\t%.3f", avg_nsec_per_iteration);
                        fprintf(stderr, "%lu\t%" PRIuPTR "\t%" PRIuPTR "\t%.3f\n",
                                sz, stride, num_trials, avg_nsec_per_iteration);
                    }
                }
            } else {
                switch (access_pattern) {
                case 2:
                    // Pseudo-random pattern #1
                    init_ptrs_random_pattern_1(ptrs, num_ptrs);
                    warn_if_cycle_too_short(ptrs, num_ptrs);
                    elapsed_time = one_experiment(ptrs, num_trials);
                    avg_nsec_per_iteration = ((1000000000.0 * elapsed_time)
                                              / num_trials);
                    fprintf(outf, "\t%.3f", avg_nsec_per_iteration);
                    fprintf(stderr, "%lu\t%d\t%" PRIuPTR "\t%.3f\n",
                            sz, access_pattern, num_trials,
                            avg_nsec_per_iteration);
                    break;
                default:
                    fprintf(stderr, "Unsupported value access_pattern=%d\n",
                            access_pattern);
                    exit(1);
                }
            }
            fprintf(outf, "\n");
            fflush(outf);
            fflush(stderr);
            free(ptrs);
        }
    }
    fclose(outf);
    exit(0);
}
