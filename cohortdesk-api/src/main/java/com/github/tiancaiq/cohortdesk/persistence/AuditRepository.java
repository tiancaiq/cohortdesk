package com.github.tiancaiq.cohortdesk.persistence;

import com.github.tiancaiq.cohortdesk.model.Emp;
import com.github.tiancaiq.cohortdesk.model.LoginLog;
import com.github.tiancaiq.cohortdesk.model.OperateLog;
import com.github.tiancaiq.cohortdesk.model.PageResult;
import com.github.tiancaiq.cohortdesk.model.dto.LoginLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.dto.OperateLogQueryParam;
import com.github.tiancaiq.cohortdesk.model.vo.LoginLogSummaryVO;
import com.github.tiancaiq.cohortdesk.model.vo.OperateLogSummaryVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AuditRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveLogin(LoginLog row) { entityManager.persist(row); }

    @Transactional
    public void saveOperation(OperateLog row) { entityManager.persist(row); }

    private <T> PageResult page(String select, String count, Map<String, Object> params,
                                Class<T> type, Integer page, Integer size) {
        TypedQuery<T> rows = entityManager.createQuery(select, type);
        TypedQuery<Long> total = entityManager.createQuery(count, Long.class);
        params.forEach((key, value) -> {
            rows.setParameter(key, value);
            total.setParameter(key, value);
        });
        rows.setFirstResult((Math.max(1, page == null ? 1 : page) - 1) * Math.max(1, size == null ? 10 : size));
        rows.setMaxResults(Math.max(1, size == null ? 10 : size));
        return new PageResult(total.getSingleResult(), rows.getResultList());
    }

    public PageResult pageLogins(LoginLogQueryParam filter) {
        StringBuilder where = new StringBuilder(" where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
            where.append(" and lower(l.username) like :username");
            params.put("username", "%" + filter.getUsername().toLowerCase() + "%");
        }
        if (filter.getBeginDate() != null) {
            where.append(" and l.loginTime >= :begin");
            params.put("begin", filter.getBeginDate().atStartOfDay());
        }
        if (filter.getEndDate() != null) {
            where.append(" and l.loginTime < :end");
            params.put("end", filter.getEndDate().plusDays(1).atStartOfDay());
        }
        return page("select l from LoginLog l" + where + " order by l.id desc",
                "select count(l) from LoginLog l" + where,
                params, LoginLog.class, filter.getPage(), filter.getPageSize());
    }

    public LoginLogSummaryVO loginSummary() {
        LoginLogSummaryVO summary = new LoginLogSummaryVO();
        var start = LocalDate.now().atStartOfDay();
        var end = LocalDate.now().plusDays(1).atStartOfDay();
        List<Object[]> today = entityManager.createQuery(
                "select l.isSuccess, count(l) from LoginLog l where l.loginTime >= :start " +
                "and l.loginTime < :end group by l.isSuccess", Object[].class)
                .setParameter("start", start).setParameter("end", end).getResultList();
        summary.setTodayLoginCount(today.stream().filter(row -> ((Number) row[0]).intValue() == 1)
                .mapToLong(row -> ((Number) row[1]).longValue()).sum());
        summary.setTodayFailCount(today.stream().filter(row -> ((Number) row[0]).intValue() == 0)
                .mapToLong(row -> ((Number) row[1]).longValue()).sum());

        List<LoginLogSummaryVO.PieChartVO> pie = entityManager.createQuery(
                "select l.isSuccess, count(l) from LoginLog l group by l.isSuccess", Object[].class)
                .getResultList().stream()
                .map(row -> new LoginLogSummaryVO.PieChartVO(
                        ((Number) row[0]).intValue() == 1 ? "Success" : "Failure",
                        ((Number) row[1]).intValue())).toList();
        summary.setTotalSuccessFail(pie);

        List<LoginLogSummaryVO.FailRankVO> rank = new ArrayList<>();
        for (Object[] row : entityManager.createQuery(
                "select l.username, count(l) from LoginLog l where l.isSuccess = 0 " +
                "group by l.username order by count(l) desc", Object[].class)
                .setMaxResults(5).getResultList()) {
            LoginLogSummaryVO.FailRankVO item = new LoginLogSummaryVO.FailRankVO();
            item.setUsername((String) row[0]);
            item.setCount(((Number) row[1]).intValue());
            rank.add(item);
        }
        summary.setFailRank(rank);
        return summary;
    }

    public PageResult pageOperations(OperateLogQueryParam filter) {
        StringBuilder where = new StringBuilder(" where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (filter.getOperateEmpId() != null) {
            where.append(" and o.operateEmpId = :employee");
            params.put("employee", filter.getOperateEmpId());
        }
        if (filter.getBeginDate() != null) {
            where.append(" and o.operateTime >= :begin");
            params.put("begin", filter.getBeginDate().atStartOfDay());
        }
        if (filter.getEndDate() != null) {
            where.append(" and o.operateTime < :end");
            params.put("end", filter.getEndDate().plusDays(1).atStartOfDay());
        }
        PageResult result = page("select o from OperateLog o" + where + " order by o.id desc",
                "select count(o) from OperateLog o" + where,
                params, OperateLog.class, filter.getPage(), filter.getPageSize());
        for (OperateLog row : (List<OperateLog>) result.getRows()) {
            Emp employee = row.getOperateEmpId() == null ? null
                    : entityManager.find(Emp.class, row.getOperateEmpId());
            row.setOperateEmpName(employee == null ? null : employee.getName());
        }
        return result;
    }

    public OperateLogSummaryVO operationSummary() {
        OperateLogSummaryVO summary = new OperateLogSummaryVO();
        summary.setTotalCount(entityManager.createQuery(
                "select count(o) from OperateLog o", Long.class).getSingleResult());
        summary.setTodayCount(entityManager.createQuery(
                "select count(o) from OperateLog o where o.operateTime >= :start and o.operateTime < :end",
                Long.class).setParameter("start", LocalDate.now().atStartOfDay())
                .setParameter("end", LocalDate.now().plusDays(1).atStartOfDay()).getSingleResult());

        Map<String, long[]> byType = new HashMap<>();
        for (Object[] row : entityManager.createQuery(
                "select o.methodName, count(o), sum(o.costTime) from OperateLog o group by o.methodName",
                Object[].class).getResultList()) {
            String type = operationType((String) row[0]);
            long[] totals = byType.computeIfAbsent(type, ignored -> new long[2]);
            totals[0] += ((Number) row[1]).longValue();
            totals[1] += row[2] == null ? 0 : ((Number) row[2]).longValue();
        }
        List<OperateLogSummaryVO.TypeCountVO> counts = new ArrayList<>();
        List<OperateLogSummaryVO.AvgCostVO> averages = new ArrayList<>();
        byType.forEach((type, total) -> {
            OperateLogSummaryVO.TypeCountVO count = new OperateLogSummaryVO.TypeCountVO();
            count.setType(type);
            count.setCount((int) total[0]);
            counts.add(count);
            OperateLogSummaryVO.AvgCostVO average = new OperateLogSummaryVO.AvgCostVO();
            average.setType(type);
            average.setAvgTime((double) total[1] / total[0]);
            averages.add(average);
        });
        counts.sort(Comparator.comparing(OperateLogSummaryVO.TypeCountVO::getCount)
                .thenComparing(OperateLogSummaryVO.TypeCountVO::getType));
        averages.sort(Comparator.comparing(OperateLogSummaryVO.AvgCostVO::getAvgTime).reversed()
                .thenComparing(OperateLogSummaryVO.AvgCostVO::getType));
        summary.setTotalTypeCounts(counts);
        summary.setAvgCostTimes(averages);
        return summary;
    }

    private String operationType(String method) {
        if (method == null) return "Unknown";
        if (method.startsWith("save") || method.startsWith("add")) return "Create";
        if (method.startsWith("delete") || method.equals("remove")) return "Delete";
        if (method.startsWith("update")) return "Edit";
        if (method.startsWith("upload")) return "Upload File";
        return method;
    }
}
