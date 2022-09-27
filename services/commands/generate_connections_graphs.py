from collections import defaultdict
import os

import matplotlib.pyplot as plt
import networkx as nx

from forge_cli.admin import cli
from forge.utils.mongo import MongoClientKeeper
import forge

@cli.command()
def generate_connections_graphs():
    forge.setup("rule_engine")
    keeper = MongoClientKeeper()
    culture_master = keeper.ruleEngineDB.summary.find_one(
        {"agentId": "CULTURE_MASTER"})

    ava_connection_strengths = culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employeeConnectionStrengths"]
    G = nx.Graph()
    edges = []

    for i in ava_connection_strengths:
        G.add_node(i)
        for j in ava_connection_strengths[i]:
            if i == j:
                continue
            weight = ava_connection_strengths[i][j]
            if weight > 80:
                edges.append((i, j))
                G.add_edge(i, j, weight=weight)

    graphs_path = "./services/commands/connections_graphs/"
    if not os.path.exists(graphs_path):
        os.makedirs(graphs_path)

    edges_map = defaultdict(list)
    for edge in edges:
        edges_map[edge[0]].append(edge[1])
    
    for i in ava_connection_strengths:
        new_graph = G.copy()
        current_employee = i
        layout = nx.spring_layout(G,k = 15)

        color_map = []
        for node in new_graph:
            if node == current_employee:
                color_map.append('red')
            else:
                color_map.append('aquamarine') 

        edge_color_map = []
        edges_to_remove = []
        for edge in new_graph.edges():
            if current_employee in edge:
                if edge[0] in edges_map.get(edge[1], []) and edge[1] in edges_map.get(edge[0], []):
                    edge_color_map.append('red')
                else:
                    edge_color_map.append('#f1c40f')
            else:
                if edge[0] in edges_map.get(edge[1], []) and edge[1] in edges_map.get(edge[0], []):
                    edge_color_map.append('#c4c4c4')
                else: 
                    edges_to_remove.append(edge)

        for edge in edges_to_remove:
            new_graph.remove_edge(edge[0], edge[1])

        plt.show()
        nx.draw(new_graph, with_labels=True, node_color=color_map, width=0.6,
                edge_color=edge_color_map, pos=layout, node_size=100)
        plt.savefig(graphs_path + "{}.png".format(i), format="PNG")
        plt.close()

