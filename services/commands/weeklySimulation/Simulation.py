from typing import List, Dict

from services.pairing_algorithm.api.api import EmployeeAvailability
from services.pairing_algorithm.api.api import Days
from services.pairing_algorithm.api.api import Matches
from services.pairing_algorithm.pairing_algorithm import PairingAlgorithm

from .Neuron import Neuron
from .initial_data import familiarity_answers
from .initial_data import id_to_name
import json

CHARGE_RATE = 2
DECAY_RATE = 0.1


class Simulation:
    pairing_algorithm: PairingAlgorithm
    employee_availabilities: List[EmployeeAvailability]
    available_days: List[Days]
    employee_connections_neuron: Dict[str, Dict[str, Neuron]]
    employee_connections_float: Dict[str, Dict[str, float]]
    sorted_data: Dict[str, Dict[str, float]]
    matches: Matches

    def __init__(self):
        self.pairing_algorithm = PairingAlgorithm()
        self.initial_neuron_charge()
        self.neuron_to_float()
        self.get_available_days()
        self.fill_employee_availabilites()
        # self.new_data_generation()

    def new_data_generation(self):
        self.sorted_data = {}
        for first, familiarity in familiarity_answers.items():
            self.sorted_data[first] = {}
            for i in range(28):
                self.sorted_data[first][str(
                    i+1)] = familiarity.get(str(i+1), 0.0)

        print(self.sorted_data)

    def initial_neuron_charge(self):
        self.employee_connections_neuron = {}
        for first, familiarity in familiarity_answers.items():
            self.employee_connections_neuron[first] = {}
            for second, value in familiarity.items():
                neuron = Neuron(1, 10)
                neuron.charge(10 * value)
                self.employee_connections_neuron[first][second] = neuron

    def neuron_charge(self):
        for match in self.matches.allMatches:
            neuron = self.employee_connections_neuron[match.first][match.second]
            neuron.charge(10 * CHARGE_RATE)
            self.employee_connections_neuron[match.first][match.second] = neuron
            neuron = self.employee_connections_neuron[match.second][match.first]
            neuron.charge(10 * CHARGE_RATE)
            self.employee_connections_neuron[match.second][match.first] = neuron
            self.neuron_to_float()

    def neuron_decay(self):
        for first, connections in self.employee_connections_neuron.items():
            for second, neuron in connections.items():
                neuron = self.employee_connections_neuron[first][second]
                neuron.decay(10 * DECAY_RATE)
                self.employee_connections_neuron[first][second] = neuron
        self.neuron_to_float()

    def neuron_to_float(self):
        self.employee_connections_float = {}
        for first, connections in self.employee_connections_neuron.items():
            self.employee_connections_float[first] = {}
            for second, neuron in connections.items():
                self.employee_connections_float[first][second] = neuron.value

    def get_available_days(self):
        return [e.value for e in Days]

    def fill_employee_availabilites(self):
        self.employee_availabilities = [EmployeeAvailability(
            employeeId=key, availableDays=self.get_available_days()) for key in self.employee_connections_neuron.keys()]

    def call_pairing(self):
        self.matches = self.pairing_algorithm.generate_pairs(
            self.employee_availabilities, self.employee_connections_float)

    def store_data(self, i: int):  # save data for graph
        filename = f'employee_connections_neuron{i}.json'
        with open(filename, 'w') as file_object:
            json.dump(self.employee_connections_float, file_object)

    def run(self, weeks):
        for i in range(weeks):
            self.call_pairing()
            print(f"{i+1}. tjedan: ")
            for match in self.matches.allMatches:
                if (match.first == '11'):
                    print(match)
            self.store_data(i)
            self.neuron_charge()
            self.neuron_decay()
