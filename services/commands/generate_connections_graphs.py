import forge
from forge.utils.mongo import MongoClientKeeper
from forge_cli.admin import cli

import pandas as pd
import networkx as nx
from pyvis.network import Network
import matplotlib.pyplot as plt
import os
from collections import defaultdict
from pprint import pprint


@cli.command()
def generate_connections_graphs():
    forge.setup("rule_engine")
    keeper = MongoClientKeeper()
    culture_master = keeper.ruleEngineDB.summary.find_one(
        {"agentId": "CULTURE_MASTER"})

    ava_connection_strengths =  culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employeeConnectionStrengths"]
    G = nx.Graph()
    edges = []

    for i in ava_connection_strengths:
        G.add_node(i)
        for j in ava_connection_strengths[i]:
            if i == j:
                continue
            weight = (ava_connection_strengths[i][j])
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
        current_employee = i
        layout = nx.spring_layout(G, k = 10)

        color_map = []
        for node in G:
            if node == current_employee:
                color_map.append('red')
            else:
                color_map.append('aquamarine') 

        edge_color_map = []

        for edge in G.edges():
            if current_employee in edge:
                if edge[0] in edges_map.get(edge[1], []) and edge[1] in edges_map.get(edge[0], []):
                    edge_color_map.append('red')
                else:
                    edge_color_map.append('yellow')
            else:
                edge_color_map.append('grey')

        plt.show()
        nx.draw(G, with_labels=False, node_color=color_map, width=2,
                edge_color=edge_color_map, pos=layout)
        plt.savefig(graphs_path + "{}.png".format(i), format="PNG")
        plt.close()
