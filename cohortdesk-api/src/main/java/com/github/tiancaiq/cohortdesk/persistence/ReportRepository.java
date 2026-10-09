package com.github.tiancaiq.cohortdesk.persistence;

import com.github.tiancaiq.cohortdesk.model.vo.ClazzStudentCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.JobCountVO;
import com.github.tiancaiq.cohortdesk.model.vo.OptionVO;
import com.github.tiancaiq.cohortdesk.model.vo.StudentSummaryVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
public class ReportRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<JobCountVO> employeeJobs() {
        List<JobCountVO> result = new ArrayList<>();
        for (Object[] row : entityManager.createQuery(
                "select e.job, count(e) from Emp e group by e.job", Object[].class).getResultList()) {
            Integer job = (Integer) row[0];
            JobCountVO item = new JobCountVO();
            item.setJob(job == null ? "Unassigned Role" : switch (job) {
                case 1 -> "Cohort Lead";
                case 2 -> "Instructor";
                case 3 -> "Student Affairs Manager";
                case 4 -> "Curriculum Manager";
                case 5 -> "Advisor";
                default -> "Other";
            });
            item.setCount(((Number) row[1]).intValue());
            result.add(item);
        }
        result.sort(Comparator.comparing(JobCountVO::getCount).thenComparing(JobCountVO::getJob));
        return result;
    }

    public List<OptionVO> employeeGenders() {
        List<OptionVO> result = new ArrayList<>();
        for (Object[] row : entityManager.createQuery(
                "select e.gender, count(e) from Emp e group by e.gender", Object[].class).getResultList()) {
            Integer gender = (Integer) row[0];
            result.add(new OptionVO(gender == null ? "Other" : switch (gender) {
                case 1 -> "Male";
                case 2 -> "Female";
                default -> "Other";
            }, ((Number) row[1]).longValue()));
        }
        result.sort(Comparator.comparing(OptionVO::getValue).thenComparing(OptionVO::getName));
        return result;
    }

    public List<ClazzStudentCountVO> cohortEnrollment() {
        List<ClazzStudentCountVO> result = new ArrayList<>();
        for (Object[] row : entityManager.createQuery(
                "select c.name, count(s) from Clazz c left join Student s on s.clazzId = c.id " +
                "group by c.name order by count(s) desc, c.name", Object[].class).getResultList()) {
            ClazzStudentCountVO item = new ClazzStudentCountVO();
            item.setClazzName((String) row[0]);
            item.setStudentCount(((Number) row[1]).intValue());
            result.add(item);
        }
        return result;
    }

    public List<OptionVO> studentDegrees() {
        List<OptionVO> result = new ArrayList<>();
        for (Object[] row : entityManager.createQuery(
                "select s.degree, count(s) from Student s group by s.degree order by s.degree",
                Object[].class).getResultList()) {
            Integer degree = (Integer) row[0];
            result.add(new OptionVO(degree == null ? "Other" : switch (degree) {
                case 1 -> "Middle School";
                case 2 -> "High School";
                case 3 -> "Associate Degree";
                case 4 -> "Bachelor Degree";
                case 5 -> "Master Degree";
                default -> "Other";
            }, ((Number) row[1]).longValue()));
        }
        return result;
    }

    public StudentSummaryVO studentSummary() {
        StudentSummaryVO result = new StudentSummaryVO();
        result.setTotalStudent(count("select count(s) from Student s"));
        result.setStudentHaveNoClazz(count(
                "select count(s) from Student s where not exists " +
                "(select c.id from Clazz c where c.id = s.clazzId)"));
        result.setTotalClazz(count("select count(c) from Clazz c"));
        LocalDate today = LocalDate.now();
        result.setOpenClazzCount(entityManager.createQuery(
                "select count(c) from Clazz c where c.beginDate <= :today and c.endDate >= :today",
                Long.class).setParameter("today", today).getSingleResult().intValue());
        result.setEndClazzCount(entityManager.createQuery(
                "select count(c) from Clazz c where c.endDate < :today", Long.class)
                .setParameter("today", today).getSingleResult().intValue());
        result.setNotStartClazzCount(entityManager.createQuery(
                "select count(c) from Clazz c where c.beginDate > :today", Long.class)
                .setParameter("today", today).getSingleResult().intValue());
        return result;
    }

    private int count(String jpql) {
        return entityManager.createQuery(jpql, Long.class).getSingleResult().intValue();
    }
}
