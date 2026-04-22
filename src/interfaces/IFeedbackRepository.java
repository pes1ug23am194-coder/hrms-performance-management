package interfaces;

import models.Feedback;
import java.util.List;

public interface IFeedbackRepository {
    List<Feedback> getAllFeedback();
    List<Feedback> getFeedbackForEmployee(String employeeId);
    void addFeedback(Feedback feedback);
    void deleteFeedback(String id);
}
