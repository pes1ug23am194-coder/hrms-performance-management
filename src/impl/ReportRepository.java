package impl;

import interfaces.IReportRepository;
import models.*;
import java.util.*;

public class ReportRepository implements IReportRepository {

    @Override
    public DeptReport getDeptPerformanceSummary(String deptId, String cycleId) {
        DeptReport report = new DeptReport();
        // Placeholder implementation
        return report;
    }

    @Override
    public List<Employee> getTopPerformers(String deptId, String cycleId, int n) {
        return new ArrayList<>();
    }

    @Override
    public ProgressReport getEmployeeProgressReport(String employeeId, String cycleId) {
        ProgressReport rpt = new ProgressReport();
        return rpt;
    }

    @Override
    public double getAppraisalCompletionRate(String cycleId) {
        return 0.85;
    }

    @Override
    public List<SkillGapSummary> getSkillGapSummary(String deptId) {
        return new ArrayList<>();
    }
}
