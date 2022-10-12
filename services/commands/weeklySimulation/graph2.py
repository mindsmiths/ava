import networkx as nx
import networkx.algorithms.community as community
import matplotlib.pyplot as plt
import random

from itertools import combinations

from test_data import employeeConnectionStrengths
from test_data import id_to_name


def draw_connections_graph():
    G = nx.Graph()
    N = nx.Graph()
    colors = ['green', 'orange', 'red', 'blue', 'yellow', 'purple']

    # initialize G
    pairs = combinations(employeeConnectionStrengths.keys(), 2)
    for pair in pairs:
        if pair[0] in employeeConnectionStrengths and pair[1] in employeeConnectionStrengths[pair[0]] and \
                pair[1] in employeeConnectionStrengths and pair[0] in employeeConnectionStrengths[pair[1]]:
            weight = (employeeConnectionStrengths[pair[0]][pair[1]] + employeeConnectionStrengths[pair[1]][pair[0]])
            if weight >= 6:
                G.add_edge(pair[0], pair[1], weight=weight)

    # create communities and color the nodes
    communities = community.louvain_communities(G, seed=7, resolution=1)
    for comm in communities:
        for el in comm:
            print(id_to_name[el], end=', ')
        print()
    node_to_community = dict()
    community_to_color = dict()
    ind = 0
    for community_id, comm in enumerate(communities):
        for node in comm:
            node_to_community[node] = community_id
        community_to_color[community_id] = colors[ind]
        ind += 1
        if ind > len(colors) - 1:
            ind = 0
    node_color = []
    for node in G.nodes:
        if node in node_to_community:
            node_color.append(community_to_color[node_to_community[node]])
        else:
            node_color.append("white")

    # initialize N
    for employee in id_to_name:
        if employee not in G.nodes and employeeConnectionStrengths[employee]:
            N.add_node(employee)
    pairs = combinations(N.nodes, 2)
    for pair in pairs:
        N.add_edge(pair[0], pair[1], weight=random.random())

    # clean G node labels
    id_to_name_g = id_to_name.copy()
    for employee_id in id_to_name:
        if employee_id not in G.nodes:
            id_to_name_g.pop(employee_id)

    # draw G
    plt.subplot(121)
    nx.make_max_clique_graph(G)
    nx.draw(G, with_labels=True, labels=id_to_name_g, node_color=node_color, node_size=400)

    # clean N node labels
    id_to_name_n = id_to_name.copy()
    for employee_id in id_to_name:
        if employee_id not in N.nodes:
            id_to_name_n.pop(employee_id)

    # draw N
    ax = plt.subplot(122)
    pos = nx.spring_layout(N, seed=7)
    nx.draw_networkx_nodes(N.nodes, pos)
    nx.draw_networkx_labels(N, pos, labels=id_to_name_n)

    # show graphs
    ax.axis("off")
    plt.show()
    # save it
    # plt.close()


draw_connections_graph()