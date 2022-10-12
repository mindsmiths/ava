import networkx as nx
import networkx.algorithms.community as community
import matplotlib.pyplot as plt

from netgraph import Graph

from itertools import combinations

from test_data import employeeConnectionStrengths
from test_data import id_to_name

TRESHOLD = 4


def draw_connections_graph():
    G = nx.Graph()
    colors = ['green', 'orange', 'red', 'blue', 'yellow', 'purple']

    pairs = combinations(employeeConnectionStrengths.keys(), 2)
    for pair in pairs:
        if pair[0] in employeeConnectionStrengths and pair[1] in employeeConnectionStrengths[pair[0]] and\
                pair[1] in employeeConnectionStrengths and pair[0] in employeeConnectionStrengths[pair[1]]:
            weight = (employeeConnectionStrengths[pair[0]][pair[1]] + employeeConnectionStrengths[pair[1]][pair[0]])
            if weight > TRESHOLD:
                G.add_edge(pair[0], pair[1], weight=weight)

    communities = community.louvain_communities(G, seed=7, resolution=1)
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

    node_color = {node: community_to_color[community_id] for node, community_id in node_to_community.items()}

    id_to_name_tmp = id_to_name.copy()
    for employee_id in id_to_name_tmp:
        if employee_id not in G.nodes:
            id_to_name.pop(employee_id)

    Graph(G,
          node_color=node_color,
          node_edge_width=0.1,
          node_layout='community',
          node_layout_kwargs=dict(node_to_community=node_to_community),
          node_labels=id_to_name,
          node_label_fontdict=dict(size=15, clip_on=False),
          node_size=6,
          edge_alpha=0.5,
          edge_width=0.25,
          )

    plt.show()
    # save it
    # plt.close()


draw_connections_graph()