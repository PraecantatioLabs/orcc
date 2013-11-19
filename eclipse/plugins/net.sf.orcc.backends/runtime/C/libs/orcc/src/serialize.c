/*
 * Copyright (c) 2013, INSA of Rennes
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
 *   * Neither the name of INSA Rennes nor the names of its
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

#include <assert.h>

#include "serialize.h"
#include "roxml.h"
#include "trace.h"
#include "dataflow.h"
#include "mapping.h"


/**
 * Generate some mapping structure from an XCF file.
 */
mappings_set_t* compute_mappings_from_file(char *xcf_file, actor_t **actors, int actors_size) {
    int i, j, k, size;
    char *nb, *name;
    node_t *configuration, *partitioning, *partition, *instance, *attribute;
    mappings_set_t *mappings_set = (mappings_set_t *) malloc(sizeof(mappings_set_t));

    configuration = roxml_load_doc(xcf_file);
    if (configuration == NULL) {
        printf("I/O error when reading mapping file.\n");
        exit(1);
    }

    mappings_set->size = roxml_get_chld_nb(configuration);
    mappings_set->mappings = (mapping_t **) malloc(mappings_set->size * sizeof(mapping_t *));

    for (i = 0; i < mappings_set->size; i++) {
        partitioning = roxml_get_chld(configuration, NULL, i);
        name = roxml_get_name(partitioning, NULL, 0);

        mappings_set->mappings[i] = allocate_mapping(
                roxml_get_chld_nb(partitioning));

        for (j = 0; j < mappings_set->mappings[i]->number_of_threads; j++) {
            partition = roxml_get_chld(partitioning, NULL, j);
            name = roxml_get_name(partition, NULL, 0);
            mappings_set->mappings[i]->partitions_size[j] = roxml_get_chld_nb(
                    partition);

            attribute = roxml_get_attr(partition, "id", 0);
            nb = roxml_get_content(attribute, NULL, 0, &size);
            mappings_set->mappings[i]->threads_affinities[j] = atoi(nb);

            mappings_set->mappings[i]->partitions_of_actors[j]
                    = (actor_t **) malloc(
                            mappings_set->mappings[i]->partitions_size[j]
                                    * sizeof(actor_t *));

            for (k = 0; k < mappings_set->mappings[i]->partitions_size[j]; k++) {
                instance = roxml_get_chld(partition, NULL, k);
                name = roxml_get_name(instance, NULL, 0);
                attribute = roxml_get_attr(instance, "id", 0);
                name = roxml_get_content(attribute, NULL, 0, &size);
                mappings_set->mappings[i]->partitions_of_actors[j][k]
                        = find_actor_by_name(actors, name, actors_size);
            }
        }
    }
    roxml_close(configuration);

    return mappings_set;
}

/**
 * Save network's workloads from instrumentation to a file
 * that could be used for mapping.
 */
void save_instrumentation(char* fileName, network_t network) {
    int i = 0;
    double total_workload = 0;

    node_t* rootNode = roxml_add_node(NULL, 0, ROXML_PI_NODE, "xml", "version=\"1.0\" encoding=\"UTF-8\"");
    if (rootNode == NULL) {
        printf("ORCC_ERR_ROXML_NODE_ROOT");
    }

    node_t* xdfNode = roxml_add_node(rootNode, 0, ROXML_ELM_NODE, "XDF", NULL);
    if (xdfNode == NULL) {
        printf("ORCC_ERR_ROXML_NODE_CONF");
    }
    /*!TODO : get Network's name properly */
    roxml_add_node(xdfNode, 0, ROXML_ATTR_NODE, "name", network.name);

    for (i=0; i < network.nb_actors; i++) {
        total_workload += network.actors[i]->workload;
    }
    for (i=0; i < network.nb_actors; i++) {
        node_t* instanceNode = roxml_add_node(xdfNode, 0, ROXML_ELM_NODE, "Instance", NULL);
        roxml_add_node(instanceNode, 0, ROXML_ATTR_NODE, "id", network.actors[i]->name);
        char* workload = (char*) malloc(sizeof(workload));
        sprintf(workload, "%.2lf", 1+network.actors[i]->workload*100/total_workload);
        roxml_add_node(instanceNode, 0, ROXML_ATTR_NODE, "workload", workload);
    }

    total_workload = 0;
    for (i=0; i < network.nb_connections; i++) {
        total_workload += network.connections[i]->workload;
    }
    for (i=0; i < network.nb_connections; i++) {
        node_t* connectionNode = roxml_add_node(xdfNode, 0, ROXML_ELM_NODE, "Connection", NULL);
        roxml_add_node(connectionNode, 0, ROXML_ATTR_NODE, "src", network.connections[i]->src->name);
        roxml_add_node(connectionNode, 0, ROXML_ATTR_NODE, "dst", network.connections[i]->dst->name);
        char* workload = (char*) malloc(sizeof(workload));
        sprintf(workload, "%d", 1+(int)(network.connections[i]->workload*100000/total_workload));
        roxml_add_node(connectionNode, 0, ROXML_ATTR_NODE, "workload", workload);
    }

    roxml_commit_changes(rootNode, fileName, NULL, 1);
    roxml_close(rootNode);
}

int load_network(char *fileName, network_t *network) {
    assert(fileName != NULL);
    assert(network != NULL);
    int ret = ORCC_OK;
    int i;

    node_t* rootNode = roxml_load_doc(fileName);

    if (rootNode == NULL) {
        check_orcc_error(ORCC_ERR_ROXML_OPEN);
    }

    network->nb_actors = 0;
    network->nb_connections = 0;

    while (1) {
        node_t* actorNode = roxml_get_chld(rootNode, NULL, network->nb_actors + network->nb_connections);

        if (actorNode == NULL) {
            break;
        }

        char* nodeName = roxml_get_name(actorNode, NULL, 0);
        if (strcmp(nodeName, "Instance") == 0) {
            network->nb_actors++;
        }
        else if (strcmp(nodeName, "Connection") == 0) {
            network->nb_connections++;
        }
        else {
            break;
        }
    }

    network->actors = (actor_t**) malloc(network->nb_actors * sizeof(actor_t*));
    network->connections = (connection_t**) malloc(network->nb_connections * sizeof(connection_t*));
    for (i=0; i < network->nb_connections; i++) {
        network->actors[i] = (actor_t*) malloc(sizeof(actor_t*));
        network->connections[i] = (connection_t*) malloc(sizeof(connection_t*));
    }
    for (i=0; i < network->nb_connections; i++) {
        network->connections[i]->src = (actor_t*) malloc(sizeof(actor_t));
        network->connections[i]->dst = (actor_t*) malloc(sizeof(actor_t));
    }

    print_orcc_trace(ORCC_VL_VERBOSE_2, "DEBUG : Loading network");
    for (i = 0; i < network->nb_actors; i++) {
        node_t* actorNode = roxml_get_chld(rootNode, NULL, i);

        if (actorNode == NULL) {
            break;
        }

        char* nodeName = roxml_get_name(actorNode, NULL, 0);
        if (strcmp(nodeName, "Instance") == 0) {
            node_t* nodeAttrActorId = roxml_get_attr(actorNode, "id", 0);
            network->actors[i]->name = roxml_get_content(nodeAttrActorId, NULL, 0, NULL);
            network->actors[i]->id = i;
            network->actors[i]->processor_id = 0;

            node_t* nodeAttrWorkload = roxml_get_attr(actorNode, "workload", 0);
            if (nodeAttrWorkload != NULL) {
                network->actors[i]->workload = atoi(roxml_get_content(nodeAttrWorkload, NULL, 0, NULL));
            } else {
                network->actors[i]->workload = 1;
            }

            if (print_trace_block(ORCC_VL_VERBOSE_2) == TRUE) {
                print_orcc_trace(ORCC_VL_VERBOSE_2, "DEBUG : Load Actor[%d]\tname = %s\tworkload = %d",
                                 i, network->actors[i]->name, network->actors[i]->workload);
            }
        }
        else {
            break;
        }
    }

    for (i = 0; i < network->nb_connections; i++) {
        node_t* connectionNode = roxml_get_chld(rootNode, NULL, i + network->nb_actors);

        if (connectionNode == NULL) {
            break;
        }

        char* nodeName = roxml_get_name(connectionNode, NULL, 0);
        if (strcmp(nodeName, "Connection") == 0) {
            node_t* nodeAttrActorSrc = roxml_get_attr(connectionNode, "src", 0);
            char *src = roxml_get_content(nodeAttrActorSrc, NULL, 0, NULL);
            network->connections[i]->src = find_actor_by_name(network->actors, src, network->nb_actors);

            node_t* nodeAttrActorDst = roxml_get_attr(connectionNode, "dst", 0);
            char *dst = roxml_get_content(nodeAttrActorDst, NULL, 0, NULL);
            network->connections[i]->dst = find_actor_by_name(network->actors, dst, network->nb_actors);

            node_t* nodeAttrWorkload = roxml_get_attr(connectionNode, "workload", 0);
            if (nodeAttrWorkload != NULL) {
                network->connections[i]->workload = atoi(roxml_get_content(nodeAttrWorkload, NULL, 0, NULL));
            } else {
                network->connections[i]->workload = 1;
            }

            if (print_trace_block(ORCC_VL_VERBOSE_2) == TRUE) {
                print_orcc_trace(ORCC_VL_VERBOSE_2, "DEBUG : Load Connection[%d]\tsrc = %s\t  dst = %s",
                                 i, network->connections[i]->src->name, network->connections[i]->dst->name);
            }

        }
        else {
            break;
        }
    }

    if (print_trace_block(ORCC_VL_VERBOSE_1) == TRUE) {
        print_orcc_trace(ORCC_VL_VERBOSE_1, "Network loaded successfully");
        print_orcc_trace(ORCC_VL_VERBOSE_1, "Number of actors is : %d", network->nb_actors);
        print_orcc_trace(ORCC_VL_VERBOSE_1, "Number of connections is : %d", network->nb_connections);
    }

    return ret;
}

int save_mapping(char* fileName, mapping_t *mapping) {
    assert(fileName != NULL);
    assert(mapping != NULL);
    int ret = ORCC_OK;
    int i, j;

    /* !TODO: if the output file already exists, backup of the file and advert the user */

    node_t* rootNode = roxml_add_node(NULL, 0, ROXML_PI_NODE, "xml", "version=\"1.0\" encoding=\"UTF-8\"");
    if (rootNode == NULL) {
        printf("ORCC_ERR_ROXML_NODE_ROOT");
    }

    node_t* configNode = roxml_add_node(rootNode, 0, ROXML_ELM_NODE, "Configuration", NULL);
    if (configNode == NULL) {
        check_orcc_error(ORCC_ERR_ROXML_NODE_CONF);
    }

    node_t* partitionNode = roxml_add_node(configNode, 0, ROXML_ELM_NODE, "Partitioning", NULL);
    if (partitionNode == NULL) {
        check_orcc_error(ORCC_ERR_ROXML_NODE_PART);
    }

    for (i = 0; i < mapping->number_of_threads; i++) {
        node_t* processorNode = roxml_add_node(partitionNode, 0, ROXML_ELM_NODE, "Partition", NULL);

        char* procId = (char*) malloc(sizeof(int));
        sprintf(procId, "%d", i);
        roxml_add_node(processorNode, 0, ROXML_ATTR_NODE, "id", procId);

        for (j = 0; j < mapping->partitions_size[i]; j++) {
            node_t* instanceNode = roxml_add_node(processorNode, 0, ROXML_ELM_NODE, "Instance", NULL);
            roxml_add_node(instanceNode, 0, ROXML_ATTR_NODE, "id", mapping->partitions_of_actors[i][j]->name);
        }
    }

    roxml_commit_changes(rootNode, fileName, NULL, 1);
    roxml_close(rootNode);

    print_orcc_trace(ORCC_VL_VERBOSE_1, "Mapping saved successfully\n");
    return ret;
}