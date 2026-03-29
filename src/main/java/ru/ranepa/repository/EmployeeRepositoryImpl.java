package ru.ranepa.repository;

import ru.ranepa.model.Employee;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
//implements - реализуем некоторый интерфейс (как именно сохранять, извлекать и тд)
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final Map<Long, Employee> employees = new HashMap<>();
    private static Long nextId = 1L; //поле принадлежит классу, а не конкретному объекту. Литерал типа Long


    @Override
    public String save(Employee employee) {
        employee.setId(nextId++);
        employees.put(employee.getId(), employee); //основной метод HashMap (ключ, значение)
        return "Employee " + employee.getId() + " was saved successfully";
    }

    @Override
    public Optional<Employee> findById(Long id) {
        //используем Optional.ofNullable, чтобы корректно обрабатывать отсутствие сотрудника. Либо содержит значение, либо пуст
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public Iterable<Employee> findAll() {
        //возвращаем все значения (без ключей) из Map как коллекцию
        return employees.values();
    }

    @Override
    public String delete(Long id) {
        //проверяем, существует ли сотрудник с таким ID
        if (employees.containsKey(id)) {
            employees.remove(id);
            return "Employee with ID " + id + " was deleted successfully";
        } else {
            return "Employee with ID " + id + " not found";
        }
    }
}
