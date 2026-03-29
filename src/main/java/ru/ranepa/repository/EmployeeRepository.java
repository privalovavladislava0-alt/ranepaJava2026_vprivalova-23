package ru.ranepa.repository;

import ru.ranepa.model.Employee;

import java.util.Optional;

//интерфейс - конракт. "Любой класс, который реализует меня, обязан иметь такие методы"
//только методы, без каких-либо действий: сохранить, найти, показать всех, удалить
//Alt+Enter -> implement interface
public interface EmployeeRepository {
    String save(Employee employee);
    Optional<Employee> findById(Long id); //обертка над данными, которых может и не быть
    Iterable<Employee> findAll(); //возвращает коллекцию сотрудников
    String delete(Long id);
}
