package impl;

import interfaces.ISkillGapRepository;
import models.SkillGap;
import java.util.*;

public class SkillGapRepository implements ISkillGapRepository {
    @Override
    public List<SkillGap> getSkillGapsForEmployee(String employeeId) {
        return new ArrayList<>();
    }
}
