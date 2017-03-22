package com.team33.model;

import com.team33.model.csv.Students.AffectingStudentToCourseFormat;
import com.team33.model.csv.CSVBuilder;
import com.team33.model.csv.Students.CourseFormat;
import com.team33.model.csv.Students.Student;
import com.team33.model.csv.Students.StudentFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Test {

    static ArrayList<String> listOfUsedEmails = new ArrayList<String>();
    public static void main(String[] args) throws IOException {

        ArrayList<String> workbooksPaths = new ArrayList<String>();

        workbooksPaths.add("liste email tous les etudiants.xlsx");
        workbooksPaths.add("NOTES ORGA 1CS 2017 BENYAHIA.xlsx");
        AffectingStudentToCourseFormat studentFormat = new AffectingStudentToCourseFormat("1CS","SC","");
        CSVBuilder csvBuilder = new CSVBuilder(workbooksPaths,studentFormat,"C:/Users/hamza/IdeaProjects/team-33");
        csvBuilder.buildCSV();
        String email = null;
        for(Student student : studentFormat.getListOfStudentsWithoutEmail())
        {
            System.out.print(student);

            if(student.getListOfEmails().isEmpty())
            { //poopup 1
                System.out.println(" : ");
                Scanner scanner = new Scanner(System.in);
                email = scanner.nextLine();
                student.setEmail(email);

            }else
            {
                deleteUsedEmails(student);
                System.out.println(student.getListOfEmails().size());
                student.tryToSetEmail();
                if(!student.hasEmail())
                {
                    System.out.println(student.getListOfEmails());
                    Scanner scanner = new Scanner(System.in);
                    int i = scanner.nextInt();
                    email =(String) student.getListOfEmails().get(i);
                    student.setEmail(email);
                }
            }
            student.generateUsename();
            studentFormat.generateRow(student.getPositionInWorkbookOut(),student);
            listOfUsedEmails.add(email);
        }
        studentFormat.saveUsersList(new File(csvBuilder.getTempPath()));
        csvBuilder.convertToCSV();

        /*HashMap<String,String> optionalModules = studentFormat.extractOptionalModules(new XSSFWorkbook(new FileInputStream("Liste_Groupe_2CS_SIQ_S2_2014_2015.xlsx"),));
        for(String key: optionalModules.keySet())
        {
            System.out.println(optionalModules.get(key));
        }*/

    }
    public static void deleteUsedEmails(Student student)
    {
        for(int i = 0; i < student.getListOfEmails().size();i++)
            if (listOfUsedEmails.contains((String) student.getListOfEmails().get(i)))
            {
                student.getListOfEmails().remove(student.getListOfEmails().get(i));
            }
    }
}
