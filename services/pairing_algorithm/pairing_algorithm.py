import logging
from typing import List
from typing import Dict

from itertools import combinations

from forge.conf import settings as forge_settings
from forge.core.api import api
from forge.core.base import BaseService

from .api import Matches
from .api import EmployeeAvailability
from .api import Match

from .blossom_algorithm import max_weight_matching

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class PairingAlgorithm(BaseService):

    @api
    def generate_pairs(
            self, employeeAvailabilities: List[EmployeeAvailability],
            employeeConnectionStrengths: Dict[str, Dict[str, float]]) -> Matches:
        all_matches: List[Match] = []
        edges = list()
        already_matched = set()
        availability_intersections = {}

        employee_id_mapping = {}
        for index, employee_id in enumerate(employeeConnectionStrengths):
            employee_id_mapping[index] = int(employee_id)

        # create all possible pairs
        # combinations function treats ab and ba as one pair
        pairs = combinations(employee_id_mapping.keys(), 2)

        # find compatibility for all pairs
        for pair in pairs:
            pair = list(pair)
            pair.sort()
            # check if a and b have days when both are available
            first_availability = []
            second_availability = []
            for availability in employeeAvailabilities:
                if int(availability.employeeId) == employee_id_mapping[pair[0]]:
                    first_availability = availability.availableDays
                    break
            for availability in employeeAvailabilities:
                if int(availability.employeeId) == employee_id_mapping[pair[1]]:
                    second_availability = availability.availableDays
                    break
            intersection = set(first_availability) &\
                set(second_availability)
            if not intersection:
                continue
            availability_intersections[tuple(pair)] = intersection

            # compatibility is the average of how a scored b
            # and how b scored a translated by a 100
            first_score_second = employeeConnectionStrengths[str(
                employee_id_mapping[pair[0]])][str(employee_id_mapping[pair[1]])]
            second_score_first = employeeConnectionStrengths[str(
                employee_id_mapping[pair[1]])][str(employee_id_mapping[pair[0]])]
            weight = 100 - ((first_score_second + second_score_first) / 2)
            # weight can't be 0, because of how blossom works
            if weight == 0:
                weight = 1
            # create tuple readable to blossom algorithm
            edges.append((pair[0], pair[1], weight))

        matching = max_weight_matching(edges, False)
        for index, element in enumerate(matching):
            # convert indexes to employee ids
            first = employee_id_mapping[index]
            second = employee_id_mapping[element]
            if first in already_matched or second in already_matched:
                continue
            already_matched.add(first)
            already_matched.add(second)
            day = availability_intersections[tuple((index, element))].pop()
            match = Match(first=str(first), second=str(second), day=day)
            all_matches.append(match)

        # todo: handle people that didn't match

        # handling people that are not matchable

        return Matches(allMatches=all_matches)
