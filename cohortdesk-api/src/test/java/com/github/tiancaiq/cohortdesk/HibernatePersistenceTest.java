package com.github.tiancaiq.cohortdesk;

import com.github.tiancaiq.cohortdesk.model.Clazz;
import com.github.tiancaiq.cohortdesk.model.Dept;
import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.EmpExpr;
import com.github.tiancaiq.cohortdesk.model.LoginLog;
import com.github.tiancaiq.cohortdesk.model.OperateLog;
import com.github.tiancaiq.cohortdesk.model.Student;
import com.github.tiancaiq.cohortdesk.model.dto.ClazzQueryParam;
import com.github.tiancaiq.cohortdesk.model.dto.EmpQueryParam;
import com.github.tiancaiq.cohortdesk.model.dto.LoginLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.dto.OperateLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.dto.StudentQueryParam;
import com.github.tiancaiq.cohortdesk.persistence.AuditRepository;
import com.github.tiancaiq.cohortdesk.persistence.CoreRepository;
import com.github.tiancaiq.cohortdesk.persistence.ReportRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class HibernatePersistenceTest {
    @Autowired CoreRepository core;
    @Autowired AuditRepository audit;
    @Autowired ReportRepository reports;
    @Autowired EntityManager entityManager;

    @Test
    void ormSupportsCrudFilteringReportsAndAuditLogs() {
        String suffix = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        if (suffix.length() < 8) suffix = "12345678";
        suffix = suffix.substring(0, 8);
        LocalDateTime now = LocalDateTime.now();

        Dept department = new Dept(null, "J" + suffix, now, now);
        core.saveDepartment(department);
        assertNotNull(department.getId());
        department.setName("K" + suffix);
        assertTrue(core.updateDepartment(department));
        assertEquals("K" + suffix, core.findDepartment(department.getId()).getName());

        Emp employee = new Emp();
        employee.setUsername("jpa" + suffix);
        employee.setName("JPA Staff");
        employee.setGender(1);
        employee.setPhone("555" + suffix);
        employee.setDeptId(department.getId());
        employee.setCreateTime(now);
        employee.setUpdateTime(now);
        core.saveEmployee(employee);
        EmpExpr experience = new EmpExpr();
        experience.setEmpId(employee.getId());
        experience.setBegin(LocalDate.of(2020, 1, 1));
        experience.setCompany("Example");
        core.saveExperience(List.of(experience));
        EmpQueryParam employeeFilter = new EmpQueryParam();
        employeeFilter.setName("JPA Staff");
        assertTrue(core.pageEmployees(employeeFilter).getTotal() >= 1);
        assertEquals(1, core.experienceFor(employee.getId()).size());
        assertEquals(employee.getId(), core.authenticate(employee.getUsername(), "123456").getId());

        Clazz cohort = new Clazz();
        cohort.setName("JPA Cohort " + suffix);
        cohort.setBeginDate(LocalDate.now().minusDays(1));
        cohort.setEndDate(LocalDate.now().plusDays(1));
        cohort.setMasterId(employee.getId());
        cohort.setSubject(1);
        cohort.setCreateTime(now);
        cohort.setUpdateTime(now);
        core.saveCohort(cohort);
        ClazzQueryParam cohortFilter = new ClazzQueryParam();
        cohortFilter.setName("JPA Cohort " + suffix);
        assertEquals(1, core.pageCohorts(cohortFilter).getTotal());
        assertEquals("JPA Staff", core.findCohort(cohort.getId()).getMasterName());

        Student student = new Student();
        student.setName("JPA Lear");
        student.setNo("9" + suffix + "0");
        student.setPhone("556" + suffix);
        student.setIdCard("9000000000" + suffix);
        student.setGender(1);
        student.setIsCollege(1);
        student.setClazzId(cohort.getId());
        student.setCreateTime(now);
        student.setUpdateTime(now);
        core.saveStudent(student);
        StudentQueryParam studentFilter = new StudentQueryParam();
        studentFilter.setClazzId(cohort.getId());
        assertEquals(1, core.pageStudents(studentFilter).getTotal());
        assertEquals(cohort.getName(), core.findStudent(student.getId()).getClazzName());
        assertTrue(reports.cohortEnrollment().stream()
                .anyMatch(row -> cohort.getName().equals(row.getClazzName()) && row.getStudentCount() == 1));

        LoginLog login = new LoginLog(null, employee.getUsername(), now, (short) 1, 1L);
        audit.saveLogin(login);
        LoginLogQueryParam loginFilter = new LoginLogQueryParam();
        loginFilter.setUsername(employee.getUsername());
        assertEquals(1, audit.pageLogins(loginFilter).getTotal());

        OperateLog operation = new OperateLog(null, employee.getId(), now,
                "Test", "saveTest", "[]", "Completed", 1L, null);
        audit.saveOperation(operation);
        OperateLogQueryParam operationFilter = new OperateLogQueryParam();
        operationFilter.setOperateEmpId(employee.getId());
        assertEquals(1, audit.pageOperations(operationFilter).getTotal());
        assertNotNull(audit.operationSummary().getTotalTypeCounts());
        assertNotNull(audit.loginSummary().getTotalSuccessFail());

        entityManager.flush();
        core.deleteExperience(List.of(employee.getId()));
        assertEquals(1, core.deleteStudents(List.of(student.getId())));
        assertTrue(core.deleteCohort(cohort.getId()));
        assertEquals(1, core.deleteEmployees(List.of(employee.getId())));
        assertTrue(core.deleteDepartment(department.getId()));
    }
}
