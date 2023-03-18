import os
from pprint import pprint

import matplotlib.pyplot as plt
import networkx as nx
from forge.utils import random_string

from forge_cli.admin import cli
from forge.utils.mongo import MongoClientKeeper
import forge


@cli.command()
def generate_connections_graphs():
    graphs_path = "./services/armory/public/"
    if not os.path.exists(graphs_path):
        os.makedirs(graphs_path)

    forge.setup("rule_engine")
    keeper = MongoClientKeeper()
    culture_master = keeper.ruleEngineDB.summary.find_one({"agentId": "CULTURE_MASTER"})
    pprint(culture_master)
    employee_dictionary = keeper.pairingAlgorithmDB.lunchCompatibilities.find_one({})
    ava_employees = culture_master["facts"]["agents#CultureMaster"]["CULTURE_MASTER"]["employees"]

    matches = employee_dictionary["edges"]

    all_ids = set()
    for emp in matches:
        all_ids.add(emp["first"])
        all_ids.add(emp["second"])

    ava_name_keeper = {}
    for _, val in ava_employees.items():
        if val["id"] not in all_ids:
            # Employee didn't answer
            continue
        else:
            ava_name_keeper[val["id"]] = val["firstName"] + " " + val["lastName"] + " " + random_string(5)

    G = nx.Graph()

    for match in matches:
        G.add_node(ava_name_keeper[match["first"]])
        G.add_node(ava_name_keeper[match["second"]])

    for match in matches:
        G.add_edge(ava_name_keeper[match["first"]], ava_name_keeper[match["second"]], width=float(match["edgeWeight"]))

    print(f'Saving {graphs_path}connection_graph.png ... ')
    nx.draw(G, node_size=450, with_labels=True)

    plt.axis('equal')
    plt.show()
    plt.savefig(f"{graphs_path}connection_graph.png", format="PNG")
    plt.close()
