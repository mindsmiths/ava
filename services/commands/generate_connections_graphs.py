from collections import defaultdict
import os

import matplotlib.pyplot as plt
import networkx as nx

from forge_cli.admin import cli
from forge.utils.mongo import MongoClientKeeper
import forge

@cli.command()
def generate_connections_graphs():
    graphs_path = "./services/commands/connections_graphs/"
    if not os.path.exists(graphs_path):
        os.makedirs(graphs_path)

    forge.setup("rule_engine")
    keeper = MongoClientKeeper()
    culture_master = keeper.ruleEngineDB.summary.find_one({"agentId": "CULTURE_MASTER"})
    ava_connection_strengths = culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employeeConnectionStrengths"]
    
    for current_employee in ava_connection_strengths.keys():
        G = nx.Graph()

        color_map = []
        edge_color_map = {}
        edge_color_list = []
        
        for i in ava_connection_strengths.keys():
            G.add_node(i)
            if i == current_employee:
                color_map.append('red')
            else:
                color_map.append('aquamarine')

        for other_employee in ava_connection_strengths[current_employee].keys():
            if other_employee == current_employee:
                continue

            if other_employee not in ava_connection_strengths.keys():
                # Employee didn't answer
                continue

            employee_connection_strength = ava_connection_strengths[current_employee][other_employee]
            other_connection_strength = ava_connection_strengths[other_employee][current_employee]
            if employee_connection_strength > 80 and other_connection_strength > 80:
                G.add_edge(current_employee, other_employee, weight=100)
                edge_color_map[(current_employee, other_employee)] = "red"
            elif employee_connection_strength > 80:
                G.add_edge(current_employee, other_employee, weight=50)
                edge_color_map[(current_employee, other_employee)] = "yellow"

        for i in range(len(ava_connection_strengths)):
            e1 = list(ava_connection_strengths.keys())[i]
            if e1 == current_employee:
                continue

            for j in range(i+1, len(ava_connection_strengths)):
                e2 = list(ava_connection_strengths.keys())[j]
                if e2 == current_employee:
                    continue

                if ava_connection_strengths[e1][e2] > 80 and ava_connection_strengths[e2][e1] > 80:
                    G.add_edge(e1, e2, weight=100)
                    edge_color_map[(e1, e2)] = "#c4c4c4"

        for edge in G.edges():
            edge_color_list.append(edge_color_map.get(edge, edge_color_map.get(edge[::-1])))

        layout = nx.spring_layout(G, k=15)
        plt.show()
        nx.draw(G, with_labels=False, width=0.6, node_color=color_map,
                edge_color=edge_color_list, pos=layout, node_size=100)
        plt.savefig(f"{graphs_path}{current_employee}.png", format="PNG")
        plt.close()

