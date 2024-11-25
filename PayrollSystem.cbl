       IDENTIFICATION DIVISION.
       PROGRAM-ID. Payroll.
       
       ENVIRONMENT DIVISION.
       INPUT-OUTPUT SECTION.
       FILE-CONTROL.
           SELECT PayrollFile ASSIGN TO "payroll.txt"
               ORGANIZATION IS LINE SEQUENTIAL.

       DATA DIVISION.
       FILE SECTION.
       
       FD  PayrollFile.
       01  PayrollRecord.

           05 EmployeeName     PIC A (20).
           05 HoursWorked      PIC 99V9.
           05 HourlyRate       PIC 99V99.
           05 GrossPay         PIC 9999V99.
           05 Taxes            PIC 999V99.
           05 NetPay           PIC 9999V99.
           05 TaxRate          PIC 999V999.

           COMPUTE GrossPay = HoursWorked * HourlyRate 
           COMPUTE Taxes = GrossPay * TaxRate
           COMPUTE NetPay = GrossPay - Taxes


           READ PayrollFile INTO PayrollRecord
           AT END
               DISPLAY "End of file reached."
           NOT AT END
               COMPUTE GrossPay = HoursWorked * HourlyRate 
               COMPUTE Taxes = GrossPay * TaxRate
               COMPUTE NetPay = GrossPay - Taxes
               DISPLAY "Employee Name: " EmployeeName
               DISPLAY "Gross Pay: " GrossPay
               DISPLAY "Net Pay: " NetPay
       END-READ.
   