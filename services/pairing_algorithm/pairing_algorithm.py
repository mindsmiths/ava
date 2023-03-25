import logging
import random
from collections import defaultdict
from itertools import combinations
from typing import Dict
from typing import List

from forge.conf import settings as forge_settings
from forge.core.api.decorators import api
from forge.core.base import BaseService

from .api import EmployeeAvailability, LunchCompatibilities, LunchCompatibilityEdge
from .api import Match
from .api import Matches
from .blossom_algorithm import max_weight_matching

logger = logging.getLogger(forge_settings.DEFAULT_LOGGER)


class PairingAlgorithm(BaseService):

    @api
    async def generate_pairs(self, employeeAvailabilities: List[EmployeeAvailability],
                             employeeConnectionStrengths: Dict[str, Dict[str, float]]) -> Matches:
        all_matches: List[Match] = []
        edges = list()
        lunch_compatibilities = []
        already_matched = set()
        not_matched = set()
        availability_intersections = {}
        employee_availabilities = defaultdict(list)
        connection_strengths = {}
        employee_id_mapping = list(employeeConnectionStrengths)

        # blossom requires vertices to be labeled starting from 0
        availability_dict = {a.employeeId: a.availableDays for a in employeeAvailabilities}
        for i, employee_id in enumerate(employee_id_mapping):
            employee_availabilities[i] = availability_dict[employee_id]
            connection_strengths[i] = {j: employeeConnectionStrengths[employee_id][emp_id] for j, emp_id in
                                       enumerate(employee_id_mapping) if
                                       emp_id in employeeConnectionStrengths[employee_id]}

        # FIND COMPATIBILITY BETWEEN ALL EMPLOYEES
        pairs = combinations(range(len(employee_id_mapping)), 2)
        for pair in pairs:
            # check if a and b have days when both are available
            intersection = list(set(employee_availabilities[pair[0]]) & set(employee_availabilities[pair[1]]))
            random.shuffle(intersection)
            availability_intersections[tuple(sorted([pair[0], pair[1]]))] = intersection
            if not intersection:
                continue

            # calculate connection strength
            strengths = (connection_strengths[pair[0]].get(pair[1], 0.0),
                         connection_strengths[pair[1]].get(pair[0], 0.0))

            connection_strength = sum(strengths) / 2
            # calculate compatibility
            compatibility = self.calculate_compatibility(connection_strength)
            # create tuple readable to blossom algorithm
            edges.append((pair[0], pair[1], compatibility))
            lunch_compatibilities.append(
                LunchCompatibilityEdge(first=employee_id_mapping[pair[0]], second=employee_id_mapping[pair[1]],
                                       edgeWeight=connection_strength))

        if LunchCompatibilities.exists():
            compatibilities = LunchCompatibilities.get()
            compatibilities.edges = lunch_compatibilities
            compatibilities.save()
        else:
            LunchCompatibilities(edges=lunch_compatibilities).save()

        # RUN BLOSSOM
        matching = max_weight_matching(edges, False)
        for employee, match in enumerate(matching):
            if match == -1:  # match was not found
                not_matched.add(employee)
                continue
            if employee in already_matched and match in already_matched:
                continue
            already_matched.add(employee)
            already_matched.add(match)
            day = availability_intersections[tuple(sorted([employee, match]))].pop()
            for pair in availability_intersections:
                if employee in pair or match in pair:
                    if day in availability_intersections[pair]:
                        availability_intersections[pair].remove(day)
            match = Match(first=employee_id_mapping[employee], second=employee_id_mapping[match], day=day)
            all_matches.append(match)
        # DOUBLE LUNCHES
        already_matched_twice = set()
        not_matched_ = list(not_matched)
        already_matched_ = list(already_matched)
        for first in not_matched_:
            for second in already_matched_:
                if second in already_matched_twice:
                    continue
                pair = tuple(sorted([first, second]))

                if availability_intersections[pair]:
                    day = availability_intersections[pair].pop()
                    match = Match(first=employee_id_mapping[first], second=employee_id_mapping[second], day=day)
                    all_matches.append(match)
                    already_matched.add(first)
                    already_matched.add(second)
                    not_matched.remove(first)
                    already_matched_twice.add(second)
                    break

        return Matches(allMatches=all_matches)

    def calculate_compatibility(self, connection_strength: float) -> float:
        compatibility = 1000 * (1 - connection_strength) + 1
        return compatibility
