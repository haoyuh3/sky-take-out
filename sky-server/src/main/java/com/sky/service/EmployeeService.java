package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO  Data transfer object containing the employee's login credentials
     * @return Employee object containing employee details.
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO Data transfer object containing the employee's login credentials (e.g., username and password).
     * @return PageResult containing the paginated list of employees.
     */
    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     * @param status Employee status (e.g., active, inactive).
     * @param id employee id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工信息
     * @param id employee id
     * @return Employee object containing employee details.
     */
    Employee getById(Long id);

    /**
     * 修改员工信息
     * @param employeeDTO Data transfer object containing the employee's details (e.g., name, phone, etc.).
     */
    void update(EmployeeDTO employeeDTO);
}
