import os

import matplotlib.pyplot as plt
import networkx as nx
import networkx.algorithms.community as nx_comm
import matplotlib.cm as cm

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
        G.add_node(current_employee)

        color_map = ["red"]
        edge_color_map = {}
        edge_color_list = []
        
        for other_employee in ava_connection_strengths[current_employee].keys():
            if other_employee == current_employee:
                continue

            G.add_node(other_employee)
            color_map.append('#c8d6e5')

            if other_employee not in ava_connection_strengths.keys():
                # Employee didn't answer
                continue
            color_map

            employee_connection_strength = ava_connection_strengths[current_employee][other_employee]
            other_connection_strength = ava_connection_strengths[other_employee][current_employee]
            score = employee_connection_strength + other_connection_strength
            if employee_connection_strength > 80 and other_connection_strength > 80:
                G.add_edge(current_employee, other_employee, weight=score)
                edge_color_map[(current_employee, other_employee)] = "#2980b9"
                color_map[-1] = '#2980b9'
            elif employee_connection_strength > 80:
                G.add_edge(current_employee, other_employee, weight=score)
                edge_color_map[(current_employee, other_employee)] = "#FFC312"
                color_map[-1] ='#FFC312'

        for i in range(len(ava_connection_strengths)):
            e1 = list(ava_connection_strengths.keys())[i]
            if e1 == current_employee:
                continue

            for j in range(i+1, len(ava_connection_strengths)):
                e2 = list(ava_connection_strengths.keys())[j]
                if e2 == current_employee:
                    continue

                if ava_connection_strengths[e1][e2] > 80 and ava_connection_strengths[e2][e1] > 80:
                    score = employee_connection_strength + other_connection_strength
                    G.add_edge(e1, e2, weight=score)
                    edge_color_map[(e1, e2)] = "#d1d8e0"

        for edge in G.edges():
            edge_color_list.append(edge_color_map.get(edge, edge_color_map.get(edge[::-1])))
        """
        layout = nx_comm.louvain_communities(G)
        layout_int = []
        for i in range(len(layout)):
            set_int = set()
            for s in layout[i]:
                set_int.add(int(s))
            layout_int.append(set_int)
        print(type(layout_int))
        plt.show()
        nx.draw(G, with_labels=False, width=0.8, node_color=color_map,
                edge_color=edge_color_list, pos=layout, node_size=200)
        plt.savefig(f"{graphs_path}{current_employee}.png", format="PNG")
        plt.close()
        """
        partition = nx_comm.best_partition(G)

        # draw the graph
        pos = nx.spring_layout(G)
        # color the nodes according to their partition
        cmap = cm.get_cmap('viridis', max(partition.values()) + 1)
        nx.draw_networkx_nodes(G, pos, partition.keys(), node_size=40,
                               cmap=cmap, node_color=list(partition.values()))
        nx.draw_networkx_edges(G, pos, alpha=0.5)
        plt.show()
        plt.savefig(f"{graphs_path}{current_employee}.png", format="PNG")
        plt.close()

