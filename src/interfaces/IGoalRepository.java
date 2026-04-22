package interfaces;

import models.Goal;
import java.util.List;

public interface IGoalRepository {
    List<Goal> getAllGoals();
    List<Goal> getGoalsByEmployee(String employeeId);
    void addGoal(Goal goal);
    void updateGoal(Goal goal);
    void deleteGoal(String goalId);
}
