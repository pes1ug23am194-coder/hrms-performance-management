package service;

import interfaces.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ICustomizationForPM that communicates with the External 
 * Performance Customization subsystem via the Integration Bus.
 */
public class CustomizationClient implements ICustomizationForPM {
    
    public CustomizationClient() {
        // Ensure the external subsystem is initialized
        PerformanceCustomizationSubsystem.getInstance();
    }

    public String getSourceName() {
        return "Performance Customization Subsystem (via Integration Bus)";
    }

    @Override
    @SuppressWarnings("unchecked")
    public FormSummary getFormById(int formId) {
        return (FormSummary) IntegrationBus.getInstance().call("PerformanceCustomization", "getFormById", formId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FormSummary> getAllForms() {
        return (List<FormSummary>) IntegrationBus.getInstance().call("PerformanceCustomization", "getAllForms");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FieldSummary> getFieldsForForm(int formId) {
        return (List<FieldSummary>) IntegrationBus.getInstance().call("PerformanceCustomization", "getFieldsForForm", formId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<WorkflowSummary> getAllWorkflows() {
        return (List<WorkflowSummary>) IntegrationBus.getInstance().call("PerformanceCustomization", "getAllWorkflows");
    }

    @Override
    @SuppressWarnings("unchecked")
    public WorkflowSummary getWorkflowById(int workflowId) {
        return (WorkflowSummary) IntegrationBus.getInstance().call("PerformanceCustomization", "getWorkflowById", workflowId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<WorkflowStepSummary> getWorkflowSteps(int workflowId) {
        return (List<WorkflowStepSummary>) IntegrationBus.getInstance().call("PerformanceCustomization", "getWorkflowSteps", workflowId);
    }

    @Override
    public boolean triggerWorkflow(int workflowId, String newStatus) {
        return (Boolean) IntegrationBus.getInstance().call("PerformanceCustomization", "triggerWorkflow", workflowId, newStatus);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TaskFlowSummary> getAllTaskFlows() {
        return (List<TaskFlowSummary>) IntegrationBus.getInstance().call("PerformanceCustomization", "getAllTaskFlows");
    }

    @Override
    @SuppressWarnings("unchecked")
    public TaskFlowSummary getTaskFlowById(int taskFlowId) {
        return (TaskFlowSummary) IntegrationBus.getInstance().call("PerformanceCustomization", "getTaskFlowById", taskFlowId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getTaskFlowWindows(int taskFlowId) {
        return (List<String>) IntegrationBus.getInstance().call("PerformanceCustomization", "getTaskFlowWindows", taskFlowId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getLookupValues(String lookupType) {
        return (List<String>) IntegrationBus.getInstance().call("PerformanceCustomization", "getLookupValues", lookupType);
    }
    
    public String generateExternalReport(String cycleName) {
        return (String) IntegrationBus.getInstance().call("PerformanceCustomization", "generateExternalReport", cycleName);
    }
}

