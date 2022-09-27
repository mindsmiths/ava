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
            self,
            employeeAvailabilities: List[EmployeeAvailability],
            employeeConnectionStrengths: Dict[str, Dict[str, float]],
            employeeMatchHistories: Dict[str, List[str]]) -> Matches:
        all_matches: List[Match] = []
        edges = list()
        already_matched = set()
        not_matched = set()
        availability_intersections = {}

        print("successfully received employee match histories")
        print(employeeMatchHistories)

        employeeAvailabilities_ = {}
        for availability in employeeAvailabilities:
            employeeAvailabilities_[
                availability.employeeId] = availability.availableDays

        # blossom requires vertices to be labaled starting from 0
        employee_id_mapping = {}
        for index, employee_id in enumerate(employeeConnectionStrengths):
            employee_id_mapping[index] = employee_id

        # FIND COMPATIBILITY BETWEEN ALL EMPLOYEES
        pairs = combinations(employee_id_mapping.keys(), 2)
        for pair in pairs:
            weight = 1000
            employee_pair = list(
                (employee_id_mapping[pair[0]], employee_id_mapping[pair[1]]))
            employee_pair.sort()
            # check if a and b have days when both are available
            first_availability = employeeAvailabilities_[
                employee_id_mapping[pair[0]]]
            second_availability = employeeAvailabilities_[
                employee_id_mapping[pair[1]]]
            intersection = set(first_availability) &\
                set(second_availability)
            availability_intersections[tuple(employee_pair)] = intersection
            if not intersection:
                continue
            # calculate lunch recency
            match_history = employeeMatchHistories[employee_id_mapping[pair[0]]]
            lunch_recency = 1
            for index, past_match in reversed(list(enumerate(match_history))):
                if past_match == employee_id_mapping[pair[1]]:
                    lunch_recency = index / \
                        (len(match_history)*len(employeeAvailabilities))
                    break
            # calculte connection strength
            first_score_second = employeeConnectionStrengths[
                employee_id_mapping[pair[0]]][employee_id_mapping[pair[1]]]
            second_score_first = employeeConnectionStrengths[
                employee_id_mapping[pair[1]]][employee_id_mapping[pair[0]]]

            weight = weight - (first_score_second + second_score_first)
            weight = weight * lunch_recency
            if weight == 0:
                weight = 1
            # create tuple readable to blossom algorithm
            edges.append((pair[0], pair[1], weight))

        # RUN BLOSSOM
        matching = max_weight_matching(edges, False)
        for employee, match in enumerate(matching):
            if match == -1:   # match was not found
                not_matched.add(employee_id_mapping[employee])
                continue
            first = employee_id_mapping[employee]
            second = employee_id_mapping[match]
            if first in already_matched and second in already_matched:
                continue
            employee_pair = [first, second]
            employee_pair.sort()
            already_matched.add(first)
            already_matched.add(second)
            day = availability_intersections[tuple(employee_pair)].pop()
            match = Match(first=first, second=second, day=day)
            all_matches.append(match)

        # DOUBLE LUNCHES
        already_matched_twice = set()
        not_matched_ = list(not_matched)
        already_matched_ = list(already_matched)
        for first in not_matched_:
            for second in already_matched_:
                if second in already_matched_twice:
                    continue
                employee_pair = [first, second]
                employee_pair.sort()
                if availability_intersections[tuple(employee_pair)]:
                    day = availability_intersections[tuple(
                        employee_pair)].pop()
                    match = Match(first=first, second=second, day=day)
                    all_matches.append(match)
                    already_matched.add(first)
                    already_matched.add(second)
                    not_matched.remove(first)
                    already_matched_twice.add(second)
                    break

        return Matches(allMatches=all_matches)
