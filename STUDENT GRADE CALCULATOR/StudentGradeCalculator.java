import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Student {
    private String name;
    private int numSubjects;
    private int[] marks;
    private int totalMarks;
    private double averagePercentage;
    private char grade;

    public Student(String name, int numSubjects, int[] marks) {
        this.name = name;
        this.numSubjects = numSubjects;
        this.marks = marks;
        calculateTotalAndGrade();
    }

    private void calculateTotalAndGrade() {
        totalMarks = 0;
        for (int mark : marks) {
            totalMarks += mark;
        }
        averagePercentage = (double) totalMarks / numSubjects;

        // Determine grade based on average percentage
        if (averagePercentage >= 90) {
            grade = 'A';
        } else if (averagePercentage >= 80) {
            grade = 'B';
        } else if (averagePercentage >= 70) {
            grade = 'C';
        } else if (averagePercentage >= 60) {
            grade = 'D';
        } else {
            grade = 'F';
        }
    }

    public String getName() {
        return name;
    }

    public void displayInfo() {
        System.out.println("\nStudent Name: " + name);
        System.out.println("Total Marks: " + totalMarks);
        System.out.println("Average Percentage: " + String.format("%.1f", averagePercentage) + "%");
        System.out.println("Grade: " + grade);
    }

    public void editMarks(Scanner scanner) {
        System.out.println("Current Marks: ");
        for (int i = 0; i < numSubjects; i++) {
            System.out.println("Subject " + (i + 1) + ": " + marks[i]);
        }
        System.out.print("Do you want to edit the marks? (yes/no): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            for (int i = 0; i < numSubjects; i++) {
                System.out.print("Enter new marks for subject " + (i + 1) + ": ");
                marks[i] = scanner.nextInt();
            }
            calculateTotalAndGrade();
            System.out.println("Marks updated successfully.");
        }
    }

    @Override
    public String toString() {
        StringBuilder data = new StringBuilder(name + "," + numSubjects + "," + totalMarks + "," + averagePercentage + "," + grade);
        for (int mark : marks) {
            data.append(",").append(mark);
        }
        return data.toString();
    }

    public static Student fromString(String data) {
        String[] tokens = data.split(",");
        String name = tokens[0];
        int numSubjects = Integer.parseInt(tokens[1]);
        int totalMarks = Integer.parseInt(tokens[2]);
        double averagePercentage = Double.parseDouble(tokens[3]);
        char grade = tokens[4].charAt(0);
        
        int[] marks = new int[numSubjects];
        for (int i = 0; i < numSubjects; i++) {
            marks[i] = Integer.parseInt(tokens[5 + i]);
        }
        Student student = new Student(name, numSubjects, marks);
        student.totalMarks = totalMarks; // Restore total and grade from file data
        student.averagePercentage = averagePercentage;
        student.grade = grade;
        return student;
    }
}

class StudentGradeCalculator {
    private static final String FILE_NAME = "students.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        // Check if student already exists
        List<Student> students = readAllStudents();
        Student existingStudent = null;
        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(name)) {
                existingStudent = student;
                break;
            }
        }

        if (existingStudent != null) {
            System.out.println("\nStudent record found:");
            existingStudent.displayInfo();
            // Ask if student wants to edit their marks
            existingStudent.editMarks(scanner);
            // Update the file with the modified student data
            updateStudentInFile(students);
        } else {
            System.out.print("Enter the number of subjects: ");
            int numSubjects = scanner.nextInt();
            int[] marks = new int[numSubjects];

            for (int i = 0; i < numSubjects; i++) {
                System.out.print("Enter marks for subject " + (i + 1) + ": ");
                marks[i] = scanner.nextInt();
            }

            Student newStudent = new Student(name, numSubjects, marks);
            newStudent.displayInfo();
            students.add(newStudent);
            // Save the new student record to the file
            saveAllStudentsToFile(students);
            System.out.println("New student record saved.");
        }

        scanner.close();
    }

    private static List<Student> readAllStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(FILE_NAME);

        // Check if file exists, if not, create it
        if (!file.exists()) {
            try {
                file.createNewFile(); // Create the file if it doesn't exist
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                students.add(Student.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return students;
    }

    private static void saveAllStudentsToFile(List<Student> students) {
        File file = new File(FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void updateStudentInFile(List<Student> students) {
        File file = new File(FILE_NAME);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating file: " + e.getMessage());
        }
    }
}
