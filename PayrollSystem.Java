import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// Employee class definition
class Employee {
    private String name;
    private double hoursWorked;
    private double hourlyRate;

    public Employee(String name, double hoursWorked, double hourlyRate) {
        this.name = name;
        this.hoursWorked = hoursWorked; 
        this.hourlyRate = hourlyRate;
    }

    public String getName() { return name; }
    public double getHoursWorked() { return hoursWorked; }
    public double getHourlyRate() { return hourlyRate; }

    // Method to calculate gross pay
    public double calculateGrossPay() {
        return hoursWorked * hourlyRate;
    }
}

// Main application class
public class PayrollSystem {
    private static ArrayList<Employee> employees = new ArrayList<>(); // List to hold employees

    public static void main(String[] args) {
        JFrame frame = new JFrame("Payroll System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Employee", new AddEmployeePanel());
        tabbedPane.addTab("Current Employees", new CurrentEmployeePanel(employees));
        
        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // Panel for adding new employees
    static class AddEmployeePanel extends JPanel {
        private JTextField nameField;
        private JTextField hoursField;
        private JTextField rateField;

        public AddEmployeePanel() {
            setLayout(new GridLayout(4, 2));

            add(new JLabel("Employee Name:"));
            nameField = new JTextField();
            add(nameField);

            add(new JLabel("Hours Worked:"));
            hoursField = new JTextField();
            add(hoursField);

            add(new JLabel("Hourly Rate:"));
            rateField = new JTextField();
            add(rateField);

            JButton addButton = new JButton("Add Employee");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addNewEmployee();
                }
            });
            add(addButton);
        }

        private void addNewEmployee() {
            String name = nameField.getText();
            try {
                double hours = Double.parseDouble(hoursField.getText());
                double rate = Double.parseDouble(rateField.getText());

                Employee newEmployee = new Employee(name, hours, rate);
                employees.add(newEmployee); // Add employee to the list

                writeToFile(); // Write all employee data to file

                // Clear fields after adding
                nameField.setText("");
                hoursField.setText("");
                rateField.setText("");

                JOptionPane.showMessageDialog(this, "Employee added successfully!");
                
                // Refresh Current Employees panel (if needed)
                ((CurrentEmployeePanel) ((JTabbedPane) getParent()).getComponentAt(1)).loadEmployeeData(employees);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for hours and rate.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void writeToFile() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("payroll.txt"))) { // Overwrite file
                for (Employee employee : employees) {
                    String name = String.format("%-20s", employee.getName()); // Pad/truncate name to 20 characters
                    String hours = String.format("%-5d", (int) employee.getHoursWorked()); // Convert to int and pad
                    String rate = String.format("%-5.2f", employee.getHourlyRate()); // Format rate as float with two decimal places
                    
                    writer.write(String.format("%s%s%s%n", name, hours, rate));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void populateFields(Employee emp) {
            nameField.setText(emp.getName());
            hoursField.setText(String.valueOf(emp.getHoursWorked()));
            rateField.setText(String.valueOf(emp.getHourlyRate()));
        }
    }

    // Panel for displaying current employees
    static class CurrentEmployeePanel extends JPanel {
        private JTable employeeTable;
        private DefaultTableModel tableModel;

        public CurrentEmployeePanel(ArrayList<Employee> employees) {
            setLayout(new BorderLayout());
            String[] columnNames = {"Name", "Hours Worked", "Hourly Rate", "Gross Pay"};

            tableModel = new DefaultTableModel(columnNames, 0);
            employeeTable = new JTable(tableModel);
            
            loadEmployeeData(employees); // Load employee data into the table
            
            JScrollPane scrollPane = new JScrollPane(employeeTable);
            add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton editButton = new JButton("Edit Employee");
            JButton removeButton = new JButton("Remove Employee");

            editButton.addActionListener(e -> editSelectedEmployee());
            removeButton.addActionListener(e -> removeSelectedEmployee());

            buttonPanel.add(editButton);
            buttonPanel.add(removeButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }

        // Method to load employee data into the table
        public void loadEmployeeData(ArrayList<Employee> employees) {
            tableModel.setRowCount(0); // Clear existing rows

            for (Employee emp : employees) { // Iterate over Employee objects
                Object[] rowData = {emp.getName(), emp.getHoursWorked(), emp.getHourlyRate(), emp.calculateGrossPay()};
                tableModel.addRow(rowData); // Add each employee's data to the table model
            }
        }

        private void editSelectedEmployee() {
            int selectedRow = employeeTable.getSelectedRow();
            
            if (selectedRow >= 0) { // Check if an employee is selected
                String name = (String) tableModel.getValueAt(selectedRow, 0);
                double hoursWorked = (Double) tableModel.getValueAt(selectedRow, 1);
                double hourlyRate = (Double) tableModel.getValueAt(selectedRow, 2);

                Employee selectedEmp = new Employee(name, hoursWorked, hourlyRate);
                
                // Populate fields in AddEmployeePanel for editing
                AddEmployeePanel panel = (AddEmployeePanel) ((JTabbedPane) getParent()).getComponentAt(0);
                panel.populateFields(selectedEmp);

                // Update the existing employee instead of removing it from the list
                employees.remove(selectedRow); 
                
                // Refresh table data after removal
                loadEmployeeData(employees); 
                
                JOptionPane.showMessageDialog(this, "Edit details and click 'Add Employee' to save changes.");
                
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
            }
        }

        private void removeSelectedEmployee() {
            int selectedRow = employeeTable.getSelectedRow();
            
            if (selectedRow >= 0) { // Check if an employee is selected
                employees.remove(selectedRow); // Remove from list
                writeToFile(); // Rewrite file after removal
                loadEmployeeData(employees); // Refresh table data after removal
                JOptionPane.showMessageDialog(this, "Employee removed successfully!");
                
                // Optionally also remove from file or handle file updates here.
                
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to remove.");
            }
        }

        private void writeToFile() {
             try (BufferedWriter writer = new BufferedWriter(new FileWriter("payroll.txt"))) { // Overwrite file
                 for (Employee employee : employees) {
                     String name = String.format("%-20s", employee.getName()); // Pad/truncate name to 20 characters
                     String hours = String.format("%-5d", (int) employee.getHoursWorked()); // Convert to int and pad
                     String rate = String.format("%-5.2f", employee.getHourlyRate()); // Format rate as float with two decimal places

                     writer.write(String.format("%s%s%s%n", name, hours, rate));
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
    }
}
