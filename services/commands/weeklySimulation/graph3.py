import networkx as nx
import networkx.algorithms.community as community
import matplotlib.pyplot as plt
from typing import Optional
from netgraph import Graph

from itertools import combinations
from typing import List, Dict
# from test_data import employeeConnectionStrengths
from .initial_data import id_to_name

TRESHOLD = 1.4
graphs_path = "./services/armory/public/"


def draw_connections_graph(employeeConnectionStrengths: Dict[str, Dict[str, float]], j: Optional[int] = None):
    G = nx.Graph()
    colors = ['green', 'orange', 'red', 'blue', 'yellow', 'purple']

    pairs = combinations(employeeConnectionStrengths.keys(), 2)
    employees = set()
    for pair in pairs:
        if pair[0] in employeeConnectionStrengths and pair[1] in employeeConnectionStrengths[pair[0]] and\
                pair[1] in employeeConnectionStrengths and pair[0] in employeeConnectionStrengths[pair[1]]:
            weight = (employeeConnectionStrengths[pair[0]][pair[1]] + employeeConnectionStrengths[pair[1]][pair[0]])
            if weight > TRESHOLD:
                employees.add(pair[0])
                employees.add(pair[1])
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

    community_size = len(communities)

    for i in employeeConnectionStrengths.keys():
        if i not in employees:
            node_to_community[i] = community_size 
            G.add_node(i)
            node_color[i] = 'grey'
            community_size += 1

    id_to_name_tmp = id_to_name.copy()
    for employee_id in id_to_name_tmp:
        if employee_id not in G.nodes:
            id_to_name.pop(employee_id)

    Graph(G,
          node_color=node_color,
          node_edge_width=0.1,
          node_layout='community',
          node_layout_kwargs=dict(node_to_community=node_to_community),
          #node_labels=id_to_name,
          node_label_fontdict=dict(size=10, clip_on=False),
          node_size=6,
          edge_alpha=0.5,
          edge_width=0.25,
          )

    plt.show()
    # save it
    # plt.close()
    plt.savefig(f"{graphs_path}connection_graph{j if j is not None else ''}.png", format="PNG")
    plt.close()

#draw_connections_graph()
