package com.github.tiancaiq.cohortdesk.persistence;

import com.github.tiancaiq.cohortdesk.model.Clazz;
import com.github.tiancaiq.cohortdesk.model.Dept;
import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.EmpExpr;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.Student;
import com.github.tiancaiq.cohortdesk.model.dto.ClazzQueryParam;
import com.github.tiancaiq.cohortdesk.model.dto.EmpQueryParam;
import com.github.tiancaiq.cohortdesk.model.dto.StudentQueryParam;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CoreRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private <T> PageResult page(String select, String count, String order, Map<String, Object> params,
                                Class<T> type, Integer page, Integer size) {
        int pageNumber = Math.max(1, page == null ? 1 : page);
        int pageSize = Math.max(1, size == null ? 10 : size);
        TypedQuery<T> rows = entityManager.createQuery(select + order, type);
        TypedQuery<Long> total = entityManager.createQuery(count, Long.class);
        params.forEach((key, value) -> {
            rows.setParameter(key, value);
            total.setParameter(key, value);
        });
        rows.setFirstResult((pageNumber - 1) * pageSize);
        rows.setMaxResults(pageSize);
        return new PageResult(total.getSingleResult(), rows.getResultList());
    }

    public PageResult pageCohorts(ClazzQueryParam filter) {
        StringBuilder where = new StringBuilder(" where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (filter.getName() != null && !filter.getName().isBlank()) {
            where.append(" and lower(c.name) like :name");
            params.put("name", "%" + filter.getName().toLowerCase() + "%");
        }
        if (filter.getBegin() != null) {
            where.append(" and c.beginDate >= :begin");
            params.put("begin", filter.getBegin());
        }
        if (filter.getEnd() != null) {
            where.append(" and c.endDate <= :end");
            params.put("end", filter.getEnd());
        }
        PageResult result = page("select c from Clazz c" + where,
                "select count(c) from Clazz c" + where,
                " order by c.beginDate desc, c.endDate desc, c.name, c.id",
                params, Clazz.class, filter.getPage(), filter.getPageSize());
        ((List<Clazz>) result.getRows()).forEach(this::enrich);
        return result;
    }

    public List<Clazz> listCohorts() {
        List<Clazz> rows = entityManager.createQuery(
                "select c from Clazz c order by c.beginDate desc, c.endDate desc, c.name, c.id",
                Clazz.class).getResultList();
        rows.forEach(this::enrich);
        return rows;
    }

    public Clazz findCohort(Integer id) {
        Clazz row = entityManager.find(Clazz.class, id);
        if (row != null) enrich(row);
        return row;
    }

    private void enrich(Clazz cohort) {
        Emp master = cohort.getMasterId() == null ? null : entityManager.find(Emp.class, cohort.getMasterId());
        cohort.setMasterName(master == null ? null : master.getName());
        LocalDate today = LocalDate.now();
        cohort.setStatus(today.isBefore(cohort.getBeginDate()) ? "Not Started"
                : today.isAfter(cohort.getEndDate()) ? "Completed" : "In Progress");
    }

    public void saveCohort(Clazz row) { entityManager.persist(row); }

    public boolean updateCohort(Clazz incoming) {
        Clazz row = entityManager.find(Clazz.class, incoming.getId());
        if (row == null) return false;
        if (hasText(incoming.getName())) row.setName(incoming.getName());
        if (hasText(incoming.getRoom())) row.setRoom(incoming.getRoom());
        if (incoming.getBeginDate() != null) row.setBeginDate(incoming.getBeginDate());
        if (incoming.getEndDate() != null) row.setEndDate(incoming.getEndDate());
        if (incoming.getMasterId() != null) row.setMasterId(incoming.getMasterId());
        if (incoming.getSubject() != null) row.setSubject(incoming.getSubject());
        row.setUpdateTime(incoming.getUpdateTime());
        return true;
    }

    public boolean deleteCohort(Integer id) {
        Clazz row = entityManager.find(Clazz.class, id);
        if (row == null) return false;
        entityManager.remove(row);
        return true;
    }

    public List<Dept> listDepartments() {
        return entityManager.createQuery("select d from Dept d order by d.name, d.id", Dept.class).getResultList();
    }

    public Dept findDepartment(Integer id) { return entityManager.find(Dept.class, id); }
    public void saveDepartment(Dept row) { entityManager.persist(row); }

    public boolean updateDepartment(Dept incoming) {
        Dept row = entityManager.find(Dept.class, incoming.getId());
        if (row == null) return false;
        if (incoming.getName() != null) row.setName(incoming.getName());
        row.setUpdateTime(incoming.getUpdateTime());
        return true;
    }

    public boolean deleteDepartment(Integer id) {
        Dept row = entityManager.find(Dept.class, id);
        if (row == null) return false;
        entityManager.remove(row);
        return true;
    }

    public PageResult pageEmployees(EmpQueryParam filter) {
        StringBuilder where = new StringBuilder(" where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (hasText(filter.getName())) {
            where.append(" and lower(e.name) like :name");
            params.put("name", "%" + filter.getName().toLowerCase() + "%");
        }
        if (filter.getGender() != null) {
            where.append(" and e.gender = :gender");
            params.put("gender", filter.getGender());
        }
        if (filter.getBegin() != null) {
            where.append(" and e.entryDate >= :begin");
            params.put("begin", filter.getBegin());
        }
        if (filter.getEnd() != null) {
            where.append(" and e.entryDate <= :end");
            params.put("end", filter.getEnd());
        }
        PageResult result = page("select e from Emp e" + where,
                "select count(e) from Emp e" + where, " order by e.name, e.id",
                params, Emp.class, filter.getPage(), filter.getPageSize());
        ((List<Emp>) result.getRows()).forEach(this::enrich);
        return result;
    }

    public List<Emp> listEmployees() {
        List<Emp> rows = entityManager.createQuery("select e from Emp e order by e.name, e.id", Emp.class)
                .getResultList();
        rows.forEach(this::enrich);
        return rows;
    }

    public Emp findEmployee(Integer id) { return entityManager.find(Emp.class, id); }

    private void enrich(Emp employee) {
        Dept department = employee.getDeptId() == null ? null : entityManager.find(Dept.class, employee.getDeptId());
        employee.setDeptName(department == null ? null : department.getName());
    }

    public void saveEmployee(Emp row) {
        if (row.getPassword() == null) row.setPassword("123456");
        entityManager.persist(row);
    }

    public boolean updateEmployee(Emp incoming) {
        Emp row = entityManager.find(Emp.class, incoming.getId());
        if (row == null) return false;
        if (hasText(incoming.getUsername())) row.setUsername(incoming.getUsername());
        if (hasText(incoming.getName())) row.setName(incoming.getName());
        if (incoming.getGender() != null) row.setGender(incoming.getGender());
        if (hasText(incoming.getPhone())) row.setPhone(incoming.getPhone());
        if (incoming.getJob() != null) row.setJob(incoming.getJob());
        if (incoming.getSalary() != null) row.setSalary(incoming.getSalary());
        if (hasText(incoming.getImage())) row.setImage(incoming.getImage());
        if (incoming.getEntryDate() != null) row.setEntryDate(incoming.getEntryDate());
        if (incoming.getDeptId() != null) row.setDeptId(incoming.getDeptId());
        row.setUpdateTime(incoming.getUpdateTime());
        return true;
    }

    public int deleteEmployees(List<Integer> ids) {
        if (ids.isEmpty()) return 0;
        return entityManager.createQuery("delete from Emp e where e.id in :ids")
                .setParameter("ids", ids).executeUpdate();
    }

    public List<Emp> employeesInDepartment(Integer id) {
        return entityManager.createQuery("select e from Emp e where e.deptId = :id order by e.name, e.id", Emp.class)
                .setParameter("id", id).getResultList();
    }

    public Emp authenticate(String username, String password) {
        return entityManager.createQuery(
                "select e from Emp e where e.username = :username and e.password = :password", Emp.class)
                .setParameter("username", username).setParameter("password", password)
                .getResultStream().findFirst().orElse(null);
    }

    public void updatePassword(Integer id, String password) {
        entityManager.find(Emp.class, id).setPassword(password);
    }

    public void saveExperience(List<EmpExpr> rows) { rows.forEach(entityManager::persist); }

    public void deleteExperience(List<Integer> employeeIds) {
        if (!employeeIds.isEmpty()) {
            entityManager.createQuery("delete from EmpExpr x where x.empId in :ids")
                    .setParameter("ids", employeeIds).executeUpdate();
        }
    }

    public List<EmpExpr> experienceFor(Integer employeeId) {
        return entityManager.createQuery(
                "select x from EmpExpr x where x.empId = :id order by x.begin desc", EmpExpr.class)
                .setParameter("id", employeeId).getResultList();
    }

    public PageResult pageStudents(StudentQueryParam filter) {
        StringBuilder where = new StringBuilder(" where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (hasText(filter.getName())) {
            where.append(" and lower(s.name) like :name");
            params.put("name", "%" + filter.getName().toLowerCase() + "%");
        }
        if (filter.getDegree() != null) {
            where.append(" and s.degree = :degree");
            params.put("degree", filter.getDegree());
        }
        if (filter.getClazzId() != null) {
            where.append(" and s.clazzId = :cohort");
            params.put("cohort", filter.getClazzId());
        }
        PageResult result = page("select s from Student s" + where,
                "select count(s) from Student s" + where, " order by s.name, s.id",
                params, Student.class, filter.getPage(), filter.getPageSize());
        ((List<Student>) result.getRows()).forEach(this::enrich);
        return result;
    }

    public List<Student> listStudents() {
        List<Student> rows = entityManager.createQuery(
                "select s from Student s order by s.name, s.id", Student.class).getResultList();
        rows.forEach(this::enrich);
        return rows;
    }

    public Student findStudent(Integer id) {
        Student row = entityManager.find(Student.class, id);
        if (row != null) enrich(row);
        return row;
    }

    private void enrich(Student student) {
        Clazz cohort = student.getClazzId() == null ? null : entityManager.find(Clazz.class, student.getClazzId());
        student.setClazzName(cohort == null ? null : cohort.getName());
    }

    public void saveStudent(Student row) { entityManager.persist(row); }

    public boolean updateStudent(Student incoming) {
        Student row = entityManager.find(Student.class, incoming.getId());
        if (row == null) return false;
        if (hasText(incoming.getName())) row.setName(incoming.getName());
        if (hasText(incoming.getNo())) row.setNo(incoming.getNo());
        if (incoming.getGender() != null) row.setGender(incoming.getGender());
        if (hasText(incoming.getPhone())) row.setPhone(incoming.getPhone());
        if (hasText(incoming.getIdCard())) row.setIdCard(incoming.getIdCard());
        if (incoming.getIsCollege() != null) row.setIsCollege(incoming.getIsCollege());
        if (hasText(incoming.getAddress())) row.setAddress(incoming.getAddress());
        if (incoming.getDegree() != null) row.setDegree(incoming.getDegree());
        if (incoming.getGraduationDate() != null) row.setGraduationDate(incoming.getGraduationDate());
        if (incoming.getClazzId() != null) row.setClazzId(incoming.getClazzId());
        if (incoming.getViolationCount() != null) row.setViolationCount(incoming.getViolationCount());
        if (incoming.getViolationScore() != null) row.setViolationScore(incoming.getViolationScore());
        row.setUpdateTime(incoming.getUpdateTime());
        return true;
    }

    public int deleteStudents(List<Integer> ids) {
        if (ids.isEmpty()) return 0;
        return entityManager.createQuery("delete from Student s where s.id in :ids")
                .setParameter("ids", ids).executeUpdate();
    }

    public List<Student> studentsInCohort(Integer id) {
        return entityManager.createQuery(
                "select s from Student s where s.clazzId = :id order by s.name, s.id", Student.class)
                .setParameter("id", id).getResultList();
    }

    private boolean hasText(String value) { return value != null && !value.isBlank(); }
}
