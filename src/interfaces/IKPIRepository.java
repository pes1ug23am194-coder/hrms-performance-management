package interfaces;

import models.KPI;
import java.util.List;

public interface IKPIRepository {
    List<KPI> getAllKPIs();
    void updateKPI(KPI kpi);
}
