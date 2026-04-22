package interfaces;

import models.DeptReport;
import models.Employee;
import models.ProgressReport;
import models.SkillGapSummary;
import java.util.List;

public interface IReportRepository {
    DeptReport getDeptPerformanceSummary(String deptId, String cycleId);
    List<Employee> getTopPerformers(String deptId, String cycleId, int n);
    ProgressReport getEmployeeProgressReport(String employeeId, String cycleId);
    double getAppraisalCompletionRate(String cycleId);
    List<SkillGapSummary> getSkillGapSummary(String deptId);
}
