import os

import matplotlib.cm as cm
import matplotlib.pyplot as plt
import networkx as nx
import networkx.algorithms.community as nx_comm

from forge_cli.admin import cli
from forge.utils.mongo import MongoClientKeeper
import forge

class LunchCompatibilityEdge:
    first: str
    second: str
    edgeWeight: float

    def __init__(self, first, second, edgeWeight):
        self.first = first
        self.second = second
        self.edgeWeight = edgeWeight

@cli.command()
def generate_connections_graphs():
    graphs_path = "./services/commands/connections_graphs/"
    if not os.path.exists(graphs_path):
        os.makedirs(graphs_path)

    forge.setup("rule_engine")
    keeper = MongoClientKeeper()
    culture_master = keeper.ruleEngineDB.summary.find_one({"agentId": "CULTURE_MASTER"})
    # ava_connection_strengths = culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employeeConnectionStrengths"] # TODO:
    matches = [LunchCompatibilityEdge(first='1', second='2', edgeWeight='300.0'), LunchCompatibilityEdge(first='1', second='3', edgeWeight='800.0')]
    # ava_employees = culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employees"]
    ava_employees = {'1': 'miho', '2': 'Filip', '3': 'Borna'}

    all_ids = set()
    for emp in matches:
        all_ids.add(emp.first)
        all_ids.add(emp.second)

    ava_name_keeper = {}
    for id, name in ava_employees.items():
        if id not in all_ids:
            # Employee didn't answer
            continue
        else:
            ava_name_keeper[id] = name

    G = nx.Graph()

    for match in matches:
        G.add_node(ava_name_keeper[match.first])
        G.add_node(ava_name_keeper[match.second])

    for match in matches:
        print(float(match.edgeWeight))
        G.add_edge(ava_name_keeper[match.first], ava_name_keeper[match.second], width=float(match.edgeWeight))

    nx.draw(G, node_size=450, with_labels=True)

    plt.axis('equal')
    plt.show()
    plt.savefig(f"{graphs_path}test2.png", format="PNG")
    plt.close()
