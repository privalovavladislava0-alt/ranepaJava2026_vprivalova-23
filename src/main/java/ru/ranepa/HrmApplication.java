package ru.ranepa;

import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepositoryImpl;
import ru.ranepa.service.EmployeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HrmApplication {

    //метод main - статический, поэтому поля static
    private static EmployeeService employeeService;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        EmployeeRepositoryImpl employeeRepository = new EmployeeRepositoryImpl();
        employeeService = new EmployeeService(employeeRepository);

        //добавляем тестовых сотрудников
        addTestEmployees(employeeRepository);

        //цикл для отображения меню и обработки выбора пользователя
        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            //обработка выбора пользователя
            switch (choice) {
                case "1":
                    showAllEmployees();
                    break;
                case "2":
                    addEmployee();
                    break;
                case "3":
                    deleteEmployee();
                    break;
                case "4":
                    findEmployeeById();
                    break;
                case "5":
                    showStatistics();
                    break;
                case "6":
                    filterByPosition();
                    break;
                case "7":
                    showSortMenu();
                    break;
                case "8":
                    saveToFile();
                    break;
                case "9":
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-9");
            }
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    //меню с доступными действиями
    private static void printMenu() {
        System.out.println("\nHRM System Menu");
        System.out.println("1. Show all employees");
        System.out.println("2. Add employee");
        System.out.println("3. Delete employee");
        System.out.println("4. Find employee by ID");
        System.out.println("5. Show statistics");
        System.out.println("6. Filter employees by position");
        System.out.println("7. Sort employees");
        System.out.println("8. Save to file");
        System.out.println("9. Exit");
        System.out.print("Choose an option: ");
    }
    //вывод списка всех сотрудников
    private static void showAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("\nEmployee List");
        for (Employee emp : employees) {
            System.out.println(emp);
        }
        System.out.println("Total employees: " + employees.size());
    }

    //добавление нового сотрудника
    private static void addEmployee() {
        System.out.println("\nAdd new employee");

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        name = name.trim();

        System.out.print("Enter position: ");
        String position = scanner.nextLine();
        if (position == null || position.trim().isEmpty()) {
            System.out.println("Position cannot be empty.");
            return;
        }
        position = position.trim();

        System.out.print("Enter salary: ");
        double salary;
        try {
            salary = Double.parseDouble(scanner.nextLine());
            if (salary <= 0) {
                System.out.println("Salary must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid salary format. Please enter a number.");
            return;
        }

        System.out.print("Enter hire date (YYYY-MM-DD): ");
        LocalDate hireDate;
        try {
            hireDate = LocalDate.parse(scanner.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        //создаем нового сотрудника и сохраняем через сервис
        Employee newEmployee = new Employee(name, position, salary, hireDate);
        String result = employeeService.saveEmployee(newEmployee);
        System.out.println(result);
    }

    //удаление сотрудника по ID
    private static void deleteEmployee() {
        System.out.print("Enter employee ID to delete: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            String result = employeeService.deleteEmployee(id);
            System.out.println(result);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }

    //поиск сотрудника по ID с использованием Optional
    private static void findEmployeeById() {
        System.out.print("Enter employee ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            //если сотрудник найден - выводим, если нет - сообщаем
            employeeService.findEmployeeById(id).ifPresentOrElse(
                    employee -> System.out.println("Found: " + employee),
                    () -> System.out.println("Employee with ID " + id + " not found"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
        }
    }

    //статистика: средняя зарплата, топ-сотрудник, общее количество
    private static void showStatistics() {
        System.out.println("\nStatistics");

        BigDecimal averageSalary = employeeService.calculateAverageSalary();
        System.out.println("Average salary: " + averageSalary);

        employeeService.findTopEmployee().ifPresentOrElse(
                topEmployee -> System.out.println("Top employee: " + topEmployee.getName() +
                " (Salary: " + topEmployee.getSalary() + ")"),
                () -> System.out.println("No employees found."));

            System.out.println("No employees found.");
        System.out.println("Total employees: " + employeeService.getAllEmployees().size());
    }

    //фильтрация сотрудников по должности без учета регистра
    private static void filterByPosition() {
        System.out.print("Enter position to filter: ");
        String position = scanner.nextLine();

        List<Employee> filtered = employeeService.filterByPosition(position);
        if (filtered.isEmpty()) {
            System.out.println("No employees found with position: " + position);
        } else {
            System.out.println("\nEmployees with position: " + position);
            for (Employee emp : filtered) {
                System.out.println(emp);
            }
            System.out.println("Total: " + filtered.size() + " employees");
        }
    }

    //сортировка сотрудников
    private static void showSortMenu() {
        System.out.println("\nSort employees by:");
        System.out.println("1. By name (A-Z)");
        System.out.println("2. By hire date");
        System.out.println("3. By salary");
        System.out.print("Choose sort option: ");

        String sortChoice = scanner.nextLine();
        List<Employee> sortedEmployees;

        switch (sortChoice) {
            case "1":
                sortedEmployees = employeeService.sortByName();
                System.out.println("\nEmployees sorted by name:");
                break;
            case "2":
                sortedEmployees = employeeService.sortByHireDate();
                System.out.println("\nEmployees sorted by hire date:");
                break;
            case "3":
                sortedEmployees = employeeService.sortBySalary();
                System.out.println("\nEmployees sorted by salary:");
                break;
            default:
                System.out.println("Invalid option");
                return;
        }

        if (sortedEmployees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            for (Employee emp : sortedEmployees) {
                System.out.println(emp);
            }
            System.out.println("Total: " + sortedEmployees.size() + " employees");
        }
    }

    //сохранение в файл
    private static void saveToFile() {
        System.out.print("Enter filename (employee.csv): ");
        String filename = scanner.nextLine();

        employeeService.saveToFile(filename);
    }

    //тестовые данные
    private static void addTestEmployees(EmployeeRepositoryImpl repository) {
        Employee emp1 = new Employee("Petr Sokolov", "Developer", 1500.0, LocalDate.of(2025, 1,15));
        Employee emp2 = new Employee("Eva Morozova", "Manager", 1000.0, LocalDate.of(2025, 5,20));
        Employee emp3 = new Employee("Artem Privalov", "QA", 1200.0, LocalDate.of(2025, 4,23));

        repository.save(emp1);
        repository.save(emp2);
        repository.save(emp3);
    }
}