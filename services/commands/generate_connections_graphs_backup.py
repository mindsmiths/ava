# import os

# import matplotlib.cm as cm
# import matplotlib.pyplot as plt
# import networkx as nx
# import networkx.algorithms.community as nx_comm

# from community import community_louvain
# from netgraph import Graph

# from forge_cli.admin import cli
# from forge.utils.mongo import MongoClientKeeper
# import forge

# def community_layout(g, partition):
#     """
#     Compute the layout for a modular graph.


#     Arguments:
#     ----------
#     g -- networkx.Graph or networkx.DiGraph instance
#         graph to plot

#     partition -- dict mapping int node -> int community
#         graph partitions


#     Returns:
#     --------
#     pos -- dict mapping int node -> (float x, float y)
#         node positions

#     """

#     pos_communities = _position_communities(g, partition, scale=3.)

#     pos_nodes = _position_nodes(g, partition, scale=1.)

#     # combine positions
#     pos = dict()
#     for node in g.nodes():
#         pos[node] = pos_communities[node] + pos_nodes[node]

#     return pos

# def _position_communities(g, partition, **kwargs):

#     # create a weighted graph, in which each node corresponds to a community,
#     # and each edge weight to the number of edges between communities
#     between_community_edges = _find_between_community_edges(g, partition)

#     communities = set(partition.values())
#     hypergraph = nx.DiGraph()
#     hypergraph.add_nodes_from(communities)
#     for (ci, cj), edges in between_community_edges.items():
#         hypergraph.add_edge(ci, cj, weight=len(edges))

#     # find layout for communities
#     pos_communities = nx.spring_layout(hypergraph, **kwargs)

#     # set node positions to position of community
#     pos = dict()
#     for node, community in partition.items():
#         pos[node] = pos_communities[community]

#     return pos

# def _find_between_community_edges(g, partition):

#     edges = dict()

#     for (ni, nj) in g.edges():
#         ci = partition[ni]
#         cj = partition[nj]

#         if ci != cj:
#             try:
#                 edges[(ci, cj)] += [(ni, nj)]
#             except KeyError:
#                 edges[(ci, cj)] = [(ni, nj)]

#     return edges

# def _position_nodes(g, partition, **kwargs):
#     """
#     Positions nodes within communities.
#     """

#     communities = dict()
#     for node, community in partition.items():
#         try:
#             communities[community] += [node]
#         except KeyError:
#             communities[community] = [node]

#     pos = dict()
#     for ci, nodes in communities.items():
#         subgraph = g.subgraph(nodes)
#         pos_subgraph = nx.spring_layout(subgraph, **kwargs)
#         pos.update(pos_subgraph)

#     return pos


# @cli.command() # tugy plaky
# def generate_connections_graphs():
#     graphs_path = "./services/commands/connections_graphs/"
#     if not os.path.exists(graphs_path):
#         os.makedirs(graphs_path)

#     forge.setup("rule_engine")
#     keeper = MongoClientKeeper()
#     culture_master = keeper.ruleEngineDB.summary.find_one({"agentId": "CULTURE_MASTER"})
#     ava_connection_strengths = culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employeeConnectionStrengths"]
#     ava_employees = culture_master["agents#CultureMaster"]["CULTURE_MASTER"]["employees"]
    
#     ava_name_keeper = {}
#     for employee in ava_employees.values():
#         if employee["id"] not in ava_connection_strengths.keys():
#             # Employee didn't answer
#             continue
#         else:
#             name = employee["firstName"]
#             employee_id = employee["id"]
#             ava_name_keeper[employee_id] = name

#     for current_employee in ava_connection_strengths.keys():
#         G = nx.Graph()
#         G.add_node(current_employee)

#         color_map = ["red"]
#         edge_color_map = {}
#         edge_color_list = []
        
#         for other_employee in ava_connection_strengths[current_employee].keys():
#             if other_employee == current_employee:
#                 continue

#             if other_employee not in ava_connection_strengths.keys():
#                 # Employee didn't answer
#                 continue   
#             G.add_node(other_employee)
#             color_map.append('#c8d6e5')
            
#             employee_connection_strength = ava_connection_strengths[current_employee][other_employee]
#             other_connection_strength = ava_connection_strengths[other_employee][current_employee]

#             score = employee_connection_strength + other_connection_strength
#             if employee_connection_strength > 80 and other_connection_strength > 80:
#                 G.add_edge(current_employee, other_employee, weight=score)
#                 edge_color_map[(current_employee, other_employee)] = "#2980b9"
#                 color_map[-1] = '#2980b9'
#             elif employee_connection_strength > 80 or other_connection_strength > 80:
#                 G.add_edge(current_employee, other_employee, weight=score)
#                 edge_color_map[(current_employee, other_employee)] = "#FFC312"
#                 color_map[-1] ='#FFC312'

#         for i in range(len(ava_connection_strengths)):
#             e1 = list(ava_connection_strengths.keys())[i]
#             if e1 == current_employee:
#                 continue

#             for j in range(i+1, len(ava_connection_strengths)):
#                 e2 = list(ava_connection_strengths.keys())[j]
#                 if e2 == current_employee:
#                     continue

#                 if ava_connection_strengths[e1][e2] > 80 and ava_connection_strengths[e2][e1] > 80:
#                     score = employee_connection_strength + other_connection_strength
#                     G.add_edge(e1, e2, weight=score)
#                     edge_color_map[(e1, e2)] = "#d1d8e0"

#         for edge in G.edges():
#             edge_color_list.append(edge_color_map.get(edge, edge_color_map.get(edge[::-1])))

#         partition = community_louvain.best_partition(G)
#         pos = community_layout(G, partition)
#         nx.draw_networkx_labels(G, pos, labels=ava_name_keeper, font_size=6, font_color='k', 
#                                 font_family='sans-serif', font_weight='normal', alpha=None, 
#                                 bbox=None, horizontalalignment='center', verticalalignment='center', 
#                                 ax=None, clip_on=True)
#         nx.draw(G, pos, labels=ava_name_keeper, with_labels=False, node_color=color_map, node_size=450, width=0.4,
#                 edge_color=edge_color_list)
#         plt.axis('equal')
#         plt.show()
#         plt.savefig(f"{graphs_path}{current_employee}.png", format="PNG")
#         plt.close()
