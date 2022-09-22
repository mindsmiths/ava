import forge
from forge.utils.mongo import MongoClientKeeper
from forge_cli.admin import cli

import pandas as pd
import networkx as nx
from pyvis.network import Network
import matplotlib.pyplot as plt

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
    for i in range(len(employeeIDs)):
        G2.add_node(employeeIDs[i])
        for j in range(i+1, len(employeeIDs)):
            score = 0
            a = avaConnectionStrengths[employeeIDs[i]][employeeIDs[j]]
            b = avaConnectionStrengths[employeeIDs[j]][employeeIDs[i]]
        
            if a>=50 and b>=50:
                score = (a+b)/2
                if score > 80:
                    G2.add_edge(employeeIDs[i],employeeIDs[j],weight=score)
                    print(employeeIDs[i],employeeIDs[j],score)
    
    for i in range(len(employeeIDs)):
        color_map = len(employeeIDs) * ["royalblue"]
        color_map[i] = "red"
        plt.show()
        nx.draw(G2, with_labels = False , node_color=color_map, width=2, edge_color = "royalblue")
        plt.savefig("/app/services/commands/{}.png".format(i+1), format="PNG")
        plt.close()

generate_connections_graphs()