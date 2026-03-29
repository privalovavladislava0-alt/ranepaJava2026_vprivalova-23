package ru.ranepa.service;

import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EmployeeService {

    private static final int SCALE = 2; //при расчете средней зарплаьы

    private final EmployeeRepository employeeRepository;

    //конструктор для внедрения зависимости (сервис не создает репозиторий сам, а получает его через конструктор)
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    //расчет средней зарплаты
    public BigDecimal calculateAverageSalary() {
        Iterable<Employee> allEmployees = employeeRepository.findAll(); //интерфейс коллекции, по которой можно пройти в цикле

        List<Employee> employeeList = new ArrayList<>();
        allEmployees.forEach(employeeList::add);

        if (employeeList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumSalary = BigDecimal.ZERO;
        for (Employee employee : employeeList) { //перебор коллекции
            sumSalary = sumSalary.add(employee.getSalary());
        }

        //делим сумму на количество, округляем до 2 знаков
        return sumSalary.divide(BigDecimal.valueOf(employeeList.size()), SCALE, RoundingMode.HALF_UP);
    }

    //поиск самого высокооплачиваемого сотрудника
    public Optional<Employee> findTopEmployee() {
        Iterable<Employee> allEmployees = employeeRepository.findAll();

        List <Employee> employeeList = new ArrayList<>();
        allEmployees.forEach(employeeList::add);

        if (employeeList.isEmpty()) {
            return Optional.empty();
        }

        Employee topEmployee = employeeList.getFirst(); //ссылка на объект по индексу
        for (Employee employee : employeeList) {
            if (employee.getSalary().compareTo(topEmployee.getSalary()) > 0) {
                topEmployee = employee;
            }
        }
        return Optional.of(topEmployee);
    }

    //фильтрация сотрудников по должности
    public List <Employee> filterByPosition(String position) {
        Iterable<Employee> allEmployees = employeeRepository.findAll();

        List<Employee> filteredEmployees = new ArrayList<>();

        for (Employee employee : allEmployees) { //на каждой итерации переменная получает ссылку на след сотрудника
            //сравниваем должность без учета регистра
            if (employee.getPosition().equalsIgnoreCase(position)) {
                filteredEmployees.add(employee);
            }
        }
        return filteredEmployees;
    }

    //получить всех сотрудников
    public List<Employee> getAllEmployees() {
        Iterable<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> employeeList = new ArrayList<>();
        allEmployees.forEach(employeeList::add); //метод-ссылка: вызов метода у объекта
        return employeeList;
    }

    //инкапсуляция - работаем только с сервисом, а не напрямую с репозиторием
    //сохранить сотрудника
    public String saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    //найти сотрудника по ID
    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    //удалить сотрудника по ID
    public String deleteEmployee(Long id) {
        return employeeRepository.delete(id);
    }

    //сортировка сотрудников по имени
    public List<Employee> sortByName() {
        List<Employee> employees = getAllEmployees();
        employees.sort(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER));
        return employees;
    }

    //сортировка сотрудников по дате приема
    public List<Employee> sortByHireDate() {
        List<Employee> employees = getAllEmployees();
        employees.sort(Comparator.comparing(Employee::getHireDate));
        return employees;
    }

    //сортировка сотрудников по зарплате
    public List<Employee> sortBySalary() {
        List<Employee> employees = getAllEmployees();
        employees.sort(Comparator.comparing(Employee::getSalary));
        return employees;
    }

    //сохранение списка сотрудников в файл
    public void saveToFile(String filename) {
        List<Employee> employees = getAllEmployees();

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
            //записываем заголовок csv
            writer.println("ID,Name,Position,Salary,HireDate");

            //записываем каждого сотрудника
            for (Employee emp : employees) {
                writer.printf("%d,%s,%s,%.2f,%s%n",
                        emp.getId(),
                        emp.getName(),
                        emp.getPosition(),
                        emp.getSalary(),
                        emp.getHireDate());
            }

            System.out.println("Successfully saved " + employees.size() + " employees to " + filename);
        } catch (java.io.IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }
}
