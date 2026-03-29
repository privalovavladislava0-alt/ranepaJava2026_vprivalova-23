import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;
import ru.ranepa.repository.EmployeeRepositoryImpl;
import ru.ranepa.service.EmployeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {
    private EmployeeService employeeService;
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository = new EmployeeRepositoryImpl();
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    void shouldCalculateAverageSalary() {
        Employee emp1 = new Employee("Petr Sokolov", "Developer", 100.0, LocalDate.now());
        Employee emp2 = new Employee("Eva Morozova", "Manager", 200.0, LocalDate.now());
        Employee emp3 = new Employee("Artem Privalov", "QA", 300.0, LocalDate.now());

        employeeService.saveEmployee(emp1);
        employeeService.saveEmployee(emp2);
        employeeService.saveEmployee(emp3);

        BigDecimal average = employeeService.calculateAverageSalary();

        assertEquals(200.0, average.doubleValue(), "Average salary should be 200");
    }

    @Test
    void shouldFindTopEmployee() {
        Employee emp1 = new Employee("Petr Sokolov", "Developer", 100.0, LocalDate.now());
        Employee emp2 = new Employee("Eva Morozova", "Manager", 500.0, LocalDate.now());
        Employee emp3 = new Employee("Artem Privalov", "QA", 300.0, LocalDate.now());

        employeeService.saveEmployee(emp1);
        employeeService.saveEmployee(emp2);
        employeeService.saveEmployee(emp3);

        Optional<Employee> topEmployeeOpt = employeeService.findTopEmployee();

        assertTrue(topEmployeeOpt.isPresent(), "Top employee should be present");
        Employee topEmployee = topEmployeeOpt.get();
        assertEquals("Eva Morozova", topEmployee.getName(), "Eva Morozova should be top employee");
        assertEquals(500.0, topEmployee.getSalary().doubleValue(), "Salary should be 500");
    }

    @Test
    void shouldFilterByPosition() {
        Employee emp1 = new Employee("Petr Sokolov", "Developer", 100.0, LocalDate.now());
        Employee emp2 = new Employee("Eva Morozova", "Manager", 200.0, LocalDate.now());
        Employee emp3 = new Employee("Artem Privalov", "QA", 300.0, LocalDate.now());
        Employee emp4 = new Employee("Ivan Volkov", "Developer", 150.0, LocalDate.now());

        employeeService.saveEmployee(emp1);
        employeeService.saveEmployee(emp2);
        employeeService.saveEmployee(emp3);
        employeeService.saveEmployee(emp4);

        List<Employee> developers = employeeService.filterByPosition("Developer");

        assertEquals(2, developers.size(), "Should find 2 developers");
        assertTrue(developers.stream().allMatch(e -> e.getPosition().equalsIgnoreCase("Developer")),
                                                 "All employees should have position Developer");
    }

    @Test
    void shouldReturnZeroWhenNoEmployees() {
        BigDecimal average = employeeService.calculateAverageSalary();

        assertEquals(BigDecimal.ZERO, average, "Average should be 0 when no employees");
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoEmployeesForTopEmployee() {
        Optional<Employee> topEmployeeOpt = employeeService.findTopEmployee();

        assertTrue(topEmployeeOpt.isEmpty(), "Should return empty Optional when no employees");
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeeWithPosition() {
        Employee emp1 = new Employee("Petr Sokolov", "Developer", 100.0, LocalDate.now());
        employeeService.saveEmployee(emp1);

        List<Employee> managers = employeeService.filterByPosition("Manager");
        assertTrue(managers.isEmpty(), "Should return empty list when no managers");
    }
}
