import networkx as nx
import matplotlib.pyplot as plt
from typing import List, Dict
from itertools import combinations

# from test_data import employeeConnectionStrengths
from .initial_data import id_to_name

MAX_CONNECTION_STRENGTH = 6
graphs_path = "./services/armory/public/"


def draw_connections_graph(employeeConnectionStrengths: Dict[str, Dict[str, float]]):
    G = nx.Graph()

    pairs = combinations(employeeConnectionStrengths.keys(), 2)
    for pair in pairs:
        if pair[0] in employeeConnectionStrengths and pair[1] in employeeConnectionStrengths[pair[0]] and \
                pair[1] in employeeConnectionStrengths and pair[0] in employeeConnectionStrengths[pair[1]]:
            weight = (employeeConnectionStrengths[pair[0]][pair[1]] + employeeConnectionStrengths[pair[1]][pair[0]])
            G.add_edge(pair[0], pair[1], weight=weight)

    edge_large = [(u, v) for (u, v, d) in G.edges(data=True) if d["weight"] > 0.8 * MAX_CONNECTION_STRENGTH]
    edge_small = [(u, v) for (u, v, d) in G.edges(data=True) if
                  0.8 * MAX_CONNECTION_STRENGTH >= d["weight"] > 0.6 * MAX_CONNECTION_STRENGTH]

    id_to_name_tmp = id_to_name.copy()
    for employee_id in id_to_name_tmp:
        if employee_id not in G.nodes:
            id_to_name.pop(employee_id)

    pos = nx.circular_layout(G)
    nx.draw_networkx_labels(G, pos, labels=id_to_name, horizontalalignment="center")
    nx.draw_networkx_nodes(G, pos, node_size=400, node_color="#FF8C00")
    nx.draw_networkx_edges(G, pos, edgelist=edge_large, width=1.5, edge_color="green")
    nx.draw_networkx_edges(G, pos, edgelist=edge_small, width=0.5, alpha=0.5, edge_color="b", style="dashed")
    plt.axis("equal")
    plt.show()
    plt.savefig(f"{graphs_path}connection_graph.png", format="PNG")
    plt.close()
    # save it
    # plt.close()
    # draw_connections_graph()