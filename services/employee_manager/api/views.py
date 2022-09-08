from forge.api import DBView


class Employee(DBView):
    name: str
    surname: str

    @classmethod
    def get_service_name(cls) -> str:
        return "employee_manager"
