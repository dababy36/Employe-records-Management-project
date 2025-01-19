package com.employe_management.erms.configs;

import com.employe_management.erms.entity.employe;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




import com.employe_management.erms.entity.employe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Component
public class EmployeeAuditListener {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeAuditListener.class);

    // Define methods to handle PrePersist, PreUpdate, and PreRemove events

    @PrePersist
    @PreUpdate
    @PreRemove
    public void logChanges(Object entity) {
        if (entity instanceof employe) {
            employe employee = (employe) entity;
            String action = "UNKNOWN";

            if (employee.getId() == null) {
                action = "CREATE";
            } else if (employee.getId() != null) {
                action = "UPDATE";
            }

            // Prepare the log message in YAML format
            Map<String, Object> change = new HashMap<>();
            change.put("action", action);
            change.put("employeeId", employee.getId());
            change.put("employeeName", employee.getFullName());
            change.put("timestamp", new java.util.Date().toString());

            // Log the change to a YAML file
            writeToYAMLFile(change);
        }
    }

    // Method to write the log to a YAML file
    private void writeToYAMLFile(Map<String, Object> change) {
        try (FileWriter writer = new FileWriter("employee_audit_log.yaml", true)) {
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            Yaml yaml = new Yaml(options);
            yaml.dump(change, writer); // Appending the log change to the YAML file
            logger.info("Logged change: {}", change);
        } catch (IOException e) {
            logger.error("Error writing audit log to YAML file", e);
        }
    }
}

