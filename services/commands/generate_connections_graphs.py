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
    culture_master = keeper.ruleEngineDB.summary.find_one({"agentId": "CULTURE_MASTER"})

    ava_connection_strengths = pd.DataFrame.from_dict(
        culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["avaConnectionStrengths"]
    )
    employee_ids = ava_connection_strengths.columns
    G2 = nx.Graph()
    num_of_employees = len(employee_ids)

    for i in range(num_of_employees):
        G2.add_node(employee_ids[i])
        for j in range(i+1, num_of_employees):
            score = 0
            a = ava_connection_strengths[employee_ids[i]][employee_ids[j]]
            b = ava_connection_strengths[employee_ids[j]][employee_ids[i]]
        
            if a>=50 and b>=50:
                score = (a+b)/2
                if score > 80:
                    G2.add_edge(employee_ids[i],employee_ids[j],weight = score)
    
    graphs_path = "./services/commands/connections_graphs/"
    if not os.path.exists(graphs_path):
        os.makedirs(graphs_path)

    layout = nx.planar_layout(G2)
    for i in range(num_of_employees):
        color_map = num_of_employees * ["royalblue"]
        color_map[i] = "red"
        plt.show()
        nx.draw(G2, with_labels = False , node_color=color_map, width=2, edge_color = "royalblue", pos = layout)
        plt.savefig("/app/services/commands/connections_graphs/{}.png".format(i+1), format="PNG")
        plt.close()