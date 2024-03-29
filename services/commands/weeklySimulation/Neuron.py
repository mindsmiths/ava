from datetime import date
from math import exp

from forge.utils import get_utc_date


class Neuron:
    value: float = 0
    __R_in: float = 1
    __C: float = 10
    __last_updated_at: date = get_utc_date()

    def __init__(self, R_in, C):
        self.__R_in = R_in
        self.__C = C

    def decay(self, amount: float):
        self._update(0, amount, 1)
        self.__last_updated_at = get_utc_date()

    def charge(self, amount: float):
        self._update(1, amount, self.__R_in)

    def discharge(self, amount: float):
        self._update(-1, amount, self.__R_in)

    def _update(self, target: int, amount: float, resistance: float):
        self.value += (target - self.value) * \
            (1 - exp(-amount / (resistance * self.__C)))
