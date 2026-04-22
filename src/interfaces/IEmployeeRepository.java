package interfaces;

import models.Employee;
import java.util.List;

public interface IEmployeeRepository {
    Employee getEmployeeById(String employeeId);
    List<Employee> getAllEmployees();
    void addEmployee(Employee employee);
    void updateEmployee(Employee employee);
    void deleteEmployee(String employeeId);
}
