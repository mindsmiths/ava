from forge.core.db import EmittableDataModel


class Employee(EmittableDataModel):
    id: str
    firstName: str
    lastName: str
    email: str
    active: bool

    @classmethod
    def get_service_name(cls) -> str:
        return "employee_manager"
