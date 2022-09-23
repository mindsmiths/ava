import forge
from forge.utils.mongo import MongoClientKeeper
from forge_cli.admin import cli

import pandas as pd
import networkx as nx
from pyvis.network import Network
import matplotlib.pyplot as plt
import os


@cli.command()
def generate_connections_graphs():
    forge.setup("rule_engine")
    keeper = MongoClientKeeper()
    culture_master = keeper.ruleEngineDB.summary.find_one(
        {"agentId": "CULTURE_MASTER"})

    ava_connection_strengths = pd.DataFrame.from_dict(
        culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employeeConnectionStrengths"]
    )
    employee_ids = ava_connection_strengths.columns
    G = nx.Graph()
    num_of_employees = len(employee_ids)

    for i in range(num_of_employees):
        G.add_node(employee_ids[i])
        for j in range(i+1, num_of_employees):
            score = 0
            a = ava_connection_strengths[employee_ids[i]][employee_ids[j]]
            b = ava_connection_strengths[employee_ids[j]][employee_ids[i]]

            if a >= 50 and b >= 50:
                score = (a+b)/2
                if score > 80:
                    G.add_edge(employee_ids[i], employee_ids[j], weight=score)

    graphs_path = "./services/commands/connections_graphs/"
    if not os.path.exists(graphs_path):
        os.makedirs(graphs_path)

    for i in range(num_of_employees):
        layout = nx.spring_layout(G, k = 10)
        
        color_map = []
        for node in G:
            if node == employee_ids[i]:
                color_map.append('red')
            else:
                color_map.append('royalblue')
        
        edge_color_map = []
        for edge in G.edges():
            if employee_ids[i] in edge:
                edge_color_map.append('red')
            else:
                edge_color_map.append('royalblue')

        plt.show()
        nx.draw(G, with_labels=False, node_color=color_map, width=2,
                edge_color=edge_color_map, pos=layout)
        plt.savefig(graphs_path + "{}.png".format(employee_ids[i]), format="PNG")
        plt.close()
