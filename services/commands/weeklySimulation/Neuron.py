from datetime import date
from math import exp


class Neuron:
    value: float = 0
    __R_in: float = 1
    __C: float = 10
    __last_updated_at: date = date.today()

    def __init__(self, R_in, C):
        self.__R_in = R_in
        self.__C = C

    def decay(self, amount: float):
        self._update(0, amount, 1)
        self.__last_updated_at = date.today()

    def charge(self, amount: float):
        self._update(1, amount, self.__R_in)

    def discharge(self, amount: float):
        self._update(-1, amount, self.__R_in)

    def _update(self, target: int, amount: float, resistance: float):
        self.__value += (target - self.__value) * \
            (1 - exp(-amount / (resistance * self.__C)))
