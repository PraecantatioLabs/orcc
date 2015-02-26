/*
 * Copyright (c) 2009, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the IETR/INSA of Rennes nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

// for MSVC
#define _CRT_SECURE_NO_DEPRECATE
#define _CRT_NONSTDC_NO_WARNINGS

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "util.h"
#include "options.h"
#include "trace.h"
#include "native.h"
#include "profiling.h"

#ifdef ROXML_ENABLE
#include "serialize.h"
#endif

extern char *optarg;
extern int getopt(int nargc, char * const *nargv, const char *ostr);

options_t *opt;

// compute number of errors in the program
int compareErrors = 0;
    const char *ostr = "ra";

static char *program;
static const char *usage =
    "Dataflow program generated by Orcc -- See orcc.sf.net for more information\n"

    "\nUsage: %s [options]\n"

    "\nCommon arguments:\n"
    "-i <file>          Specify an input file.\n"
    "-v <level>         Set the verbosity.\n"
    "   The possible values are: {Default : 1}\n"
    "       1 : summary and results\n"
    "       2 : debug informations\n"
    "-h                 Print this message.\n"

    "\nDebug arguments:\n"
    "-z                 Print the firings.\n"
    // "-t <trace directory>       Specify an output directory for the FIFO trace files.\n"

    "\nVideo-specific arguments:\n"
    "-f <nb frames>     Set the number of frames to decode before exiting.\n"
    "-n                 Ensure that the display will not be initialized (useful on non-graphic terminals).\n"
    "-o <file>          Check the output stream with the reference file specified in argument (usually YUV).\n"

    "\nRuntime arguments:\n"
    "-p <file>          Filename to write the profiling information.\n"
    "-m <file>          Define a predefined actor mapping on multi-core platforms using the given XML file.\n"
    "-q <file>          Filename to write the run-time actor mapping.\n"
    "-c <nb cores>      Specify the number of processor cores to use.\n"
    "-r <nb frames>     Specify the number of frames before mapping or between each mapping {Default : 10}.\n"
    "-a                 Do a new mapping every <nb frames> setted by previous option.\n"
    "-s <strategy>      Specify the run-time actor mapping strategy.\n"
    "   The possible values are: {Default : ROUND_ROBIN}\n"
#ifdef METIS_ENABLE
    "       MR   : METIS Recursive graph partition mapping\n"
    "       MKCV : METIS KWay graph partition mapping (Optimize Communication volume)\n"
    "       MKEC : METIS KWay graph partition mapping (Optimize Edge-cut)\n"
#endif /* METIS_ENABLE */
    "       RR   : A simple Round-Robin mapping\n"
    "       WLB  : Weighted Load Balancing\n"
    "       KLR  : Kernighan Lin Refinement Weighted Load Balancing\n"

    "\nOther specific arguments:\n"
    "Depending on how the application has been designed, one of these arguments can be used.\n"
    "-l <nb loops>      Set the number of readings of the input file before exiting.\n"
    "-d <directory>     Set the path when multiple input files are required.\n"
    "-w <file>          Specify a file to write the output stream.\n";

void print_usage() {
    printf(usage, program);
    fflush(stdout);
}

#ifdef _MSC_VER
void pause() {
    system("pause");
}
#endif

/////////////////////////////////////
// initializes APR and parses options
options_t* init_orcc(int argc, char *argv[]) {
    // every command line option must be followed by ':' if it takes an
    // argument, and '::' if this argument is optional
    const char *ostr = "i:no:d:m:q:f:w:l:zr:ac:s:v:p:h";
    int c;

        /////////////////////////////////////////////////// MCH
                char * fichero2;
                fichero2=(char*)malloc(255);
                char * fichero3;
                fichero3=(char*)malloc(255);
                char * fichero4;
                fichero4=(char*)malloc(255);
                strcpy (fichero2,optarg);
                strcpy (fichero3,optarg);
                strcpy (fichero4,optarg);
    ////////////////////////////////////////////////////// MCH

    opt = set_default_options();

    program = argv[0];

#ifdef _MSC_VER
    atexit(&pause);
#endif
    atexit(atexit_actions);
    init_native_context();

    while ((c = getopt(argc, argv, ostr)) != -1) {
        switch (c) {
        case '?': // BADCH
            print_orcc_error(ORCC_ERR_BAD_ARGS);
            print_usage();
            exit(ORCC_ERR_BAD_ARGS);
        case ':': // BADARG
            print_orcc_error(ORCC_ERR_BAD_ARGS);
            print_usage();
            exit(ORCC_ERR_BAD_ARGS);
        case 'd':
           // opt->input_directory = strdup(optarg); //MCH
            break;
        case 'i':
            strcpy(fichero2,argv[2]);  //MCH
            opt->input_file = fichero2;
            //opt->input_file = strdup(optarg);//MCH
            break;
        case 'l':
            opt->nbLoops = strtoul(optarg, NULL, 10);
            break;
        case 'f':
            opt->nbFrames = strtoul(optarg, NULL, 10);
            break;
        case 'c':
            set_nb_processors(optarg, opt);
            opt->enable_dynamic_mapping = TRUE;
            break;
        case 's':
            set_mapping_strategy(optarg, opt);
            break;
        case 'm':
            strcpy(fichero4,argv[6]);  //MCH
            opt->mapping_input_file = fichero4;
           // opt->mapping_input_file = strdup(optarg); //MCH
            break;
        case 'q':
            //opt->mapping_output_file = strdup(optarg); //MCH
            break;
        case 'r':
            opt->nbProfiledFrames = strtoul(optarg, NULL, 10);
            break;
        case 'a':
            opt->mapping_repetition = REMAP_ALWAYS;
            break;
        case 'n':
            opt->display_flags = DISPLAY_DISABLE;
            break;
        case 'o':
            //opt->yuv_file = strdup(optarg); //MCH
            break;
        case 'w':
            strcpy(fichero3,argv[4]);  //MCH
            opt->write_file = fichero3;
           // opt->write_file = strdup(optarg);//MCH
            break;
        case 'p':
            //opt->profiling_file = strdup(optarg); //MCH
            break;
        case 'v':
            set_verbose_level(optarg, opt);
            break;
        case 'z':
            opt->print_firings = TRUE;
            break;
        case 'h':
            print_usage();
            exit(ORCC_OK);
        default:
            print_orcc_error(ORCC_ERR_BAD_ARGS);
            print_usage();
            exit(ORCC_ERR_BAD_ARGS);
        }
    }

    return opt;
}

extern network_t network;

// Actions to do when exting properly
void atexit_actions() {
    if (opt->profiling_file != NULL) {
        compute_workloads(&network);
#ifdef ROXML_ENABLE
        save_profiling(opt->profiling_file, &network);
#endif
    }
}
