package interfaces;

import models.SkillGap;
import java.util.List;

public interface ISkillGapRepository {
    List<SkillGap> getSkillGapsForEmployee(String employeeId);
}
