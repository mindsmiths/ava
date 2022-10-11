from typing import List
from typing import Dict

from com.mindsmiths.pairingalgorithm.api.api import EmployeeAvailability
from com.mindsmiths.pairingalgorithm.api.api import Days
from com.mindsmiths.pairingalgorithm.api.api import Matches
from com.mindsmiths.pairingalgorithm.pairing_algorithm import PairingAlgorithm

from . import Neuron
from . import familiarity_answers
from . import id_to_name


class Simulation():
    pairing_algorithm: PairingAlgorithm
    employee_availabilities: List[EmployeeAvailability]
    available_days: List[Days]
    employee_connections_neuron: Dict[str, Dict[str, Neuron]]
    employee_connections_float: Dict[str, Dict[str, float]]
    matches: Matches

    def __init__(self):
        self.pairing_algorithm = PairingAlgorithm()

    # called only once at the start of the simulation

    def initial_neuron_charge(self):
        for first, familiarity in familiarity_answers.items():
            for second, value in familiarity.items:
                self.employee_connections_neuron[first][second].charge(
                    10 * value)

    def neuron_to_float(self):
        for first, connections in self.employee_connections_neuron.items():
            for second, neuron in connections:
                self.employee_connections_float[first][second] = neuron.value

    def get_available_days(self):
        return [e.value for e in Days]

    def fill_employee_availabilites(self):
        self.employee_availabilities = [EmployeeAvailability(
            employeeId=key, availableDays=self.get_available_days()) for key in self.employeeConnectionStrengths.keys()]

    def call_pairing(self):
        self.matches = self.pairing_algorithm.generate_pairs(
            self.employee_availabilities, self.employee_connections_float)

    def store_data(self):   # save data for graph
        print("")

    def run(self, weeks):
        self.initial_neuron_charge()
        for i in range(weeks):
            print("")


def simulate(weeks):
    simulation = Simulation()
    simulation.run()
