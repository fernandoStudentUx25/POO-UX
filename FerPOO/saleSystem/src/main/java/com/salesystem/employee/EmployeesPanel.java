package com.salesystem.employee;

import com.salesystem.db.Database;
import com.salesystem.ui.DatabaseTask;
import com.salesystem.ui.RecordsPanel;
import javax.swing.*;

public final class EmployeesPanel extends RecordsPanel<EmployeeRepository.Employee> {
    private final EmployeeRepository repository;

    public EmployeesPanel(Database database) {
        this(database, new EmployeeRepository());
    }

    private EmployeesPanel(Database database, EmployeeRepository repository) {
        super(database, new String[]{"Employee ID", "First name", "Last name", "Phone"},
                repository::findAll, e -> new Object[]{e.id(), e.firstName(), e.lastName(), e.phone()});
        this.repository = repository;
        addAction("Create employee", this::createEmployee);
    }

    private void createEmployee() {
        var firstName = new JTextField(20);
        var lastName = new JTextField(20);
        var phoneField = new JTextField(20);
        Object[] fields = {"First name", firstName, "Last name", lastName, "Phone", phoneField};
        while (JOptionPane.showConfirmDialog(this, fields, "Create employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String first = firstName.getText().strip();
            String last = lastName.getText().strip();
            String phone = phoneField.getText().strip();
            if (first.isEmpty() || last.isEmpty() || first.length() > 60 || last.length() > 60 || phone.length() > 20) {
                JOptionPane.showMessageDialog(this, "Enter both names (up to 60 characters each) and a phone of up to 20 characters.");
                continue;
            }
            busy(true);
            DatabaseTask.run(database, c -> repository.create(c, first, last, phone), id -> {
                JOptionPane.showMessageDialog(this, "Employee created. Employee ID: " + id);
                saved();
            }, this::failed);
            return;
        }
    }
}
