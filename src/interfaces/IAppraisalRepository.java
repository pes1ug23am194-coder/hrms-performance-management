package interfaces;

import models.Appraisal;
import java.util.List;

public interface IAppraisalRepository {
    List<Appraisal> getAllAppraisals();
    List<Appraisal> getAppraisalsByEmployee(String employeeId);
    void addAppraisal(Appraisal appraisal);
}
