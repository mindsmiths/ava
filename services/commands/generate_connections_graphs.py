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
    
    avaConnectionStrengths = pd.DataFrame.from_dict(
        culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["avaConnectionStrengths"]
    )
    employeeIDs = avaConnectionStrengths.columns
    G2 = nx.Graph()
    numOfEmployees = len(employeeIDs)

    for i in range(numOfEmployees):
        G2.add_node(employeeIDs[i])
        for j in range(i+1, numOfEmployees):
            score = 0
            a = avaConnectionStrengths[employeeIDs[i]][employeeIDs[j]]
            b = avaConnectionStrengths[employeeIDs[j]][employeeIDs[i]]
        
            if a>=50 and b>=50:
                score = (a+b)/2
                if score > 80:
                    G2.add_edge(employeeIDs[i],employeeIDs[j],weight = score/numOfEmployees**2)
    
    graphs_path = "/app/services/commands/connections_graphs/"
    if not os.path.exists(graphs_path):
        os.makedirs(graphs_path)

    layout = nx.planar_layout(G2)
    for i in range(numOfEmployees):
        color_map = numOfEmployees * ["royalblue"]
        color_map[i] = "red"
        plt.show()
        nx.draw(G2, with_labels = False , node_color=color_map, width=2, edge_color = "royalblue", pos = layout)
        plt.savefig("/app/services/commands/connections_graphs/{}.png".format(i+1), format="PNG")
        plt.close()