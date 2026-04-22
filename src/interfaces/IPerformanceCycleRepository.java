package interfaces;

import models.PerformanceCycle;
import java.util.List;

public interface IPerformanceCycleRepository {
    List<PerformanceCycle> getAllCycles();
    PerformanceCycle getCycleById(String cycleId);
}
