from random import randint
import numpy as np
import numpy
import collections
import networkx as nx
import sys
import os




def greedy_rec_algorithm(graph_degrees):
    # complete this function

    pass
    do_stuff()

def dp_graph_anonymization():
    # complete this function
    pass
    do_stuff()


def construct_graph(vertex_degree):
    # complete this function
    pass
    do_stuff()

if __name__ == "__main__":


    k_degree = int(sys.argv[1])
    file_graph = sys.argv[2]
    G = nx.Graph()

    if os.path.exists(file_graph):
        # if file exist
        with open(file_graph) as f:
            content = f.readlines()
        # read each line
        content = [x.strip() for x in content]
        for line in content:
            # split name inside each line
            names = line.split(",")
            start_node = names[0]
            if start_node not in G:
                G.add_node(start_node)
            for index in range(1, len(names)):
                node_to_add = names[index]
                if node_to_add not in G:
                    G.add_node(node_to_add)
                G.add_edge(start_node, node_to_add)

    # Degree arrays preparation
    d = [x[1] for x in G.degree()]
    array_index = numpy.argsort(d)[::-1]
    array_degrees = numpy.sort(d)[::-1]
    print("Array of degrees (d) : {}".format(d))
    print("Array of degrees sorted (array_degrees) : {}".format(array_degrees))

    # greedy
    vertex_degree_greedy = greedy_rec_algorithm(array_degrees)

    # dp
    vertex_degree_dp = graph_dp()

    # construct graph
    graph_greedy = construct_graph(vertex_degree_greedy)

    # construct graph
    graph_dp = construct_graph(vertex_degree_dp)
