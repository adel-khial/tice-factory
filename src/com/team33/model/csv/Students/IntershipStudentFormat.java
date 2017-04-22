package com.team33.model.csv.Students;

import org.apache.poi.ss.usermodel.Row;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hamza on 18/04/2017.
 */
public class IntershipStudentFormat  extends StudentFormat{
    public IntershipStudentFormat(String level, String optin, String filePathOut) {
        super(level, optin, filePathOut);
    }



    protected void generateRow(int numRow, Student student)// générer une ligne cde fichier résultat contenant les coordonné d'un étudiant
    {
        Row rw = getWorkbookOut().getSheetAt(0).createRow(numRow);

        rw.createCell(0).setCellValue(student.getUsername());
        rw.createCell(1).setCellValue(student.getPassword());
        rw.createCell(2).setCellValue(student.getFirstName());
        rw.createCell(3).setCellValue(student.getLastNameInMoodle());
        rw.createCell(4).setCellValue(student.getEmail());
    }

    @Override
    public void generateHeader() {
        Row rw = getWorkbookOut().getSheetAt(0).createRow(0);

        rw.createCell(0).setCellValue("username");
        rw.createCell(1).setCellValue("password");
        rw.createCell(2).setCellValue("firstname");
        rw.createCell(3).setCellValue("lastname");
        rw.createCell(4).setCellValue("email");
    }

    protected void createStudentList()  {

        Long startTime = System.currentTimeMillis();
        System.out.println("Création de la liste ***");
        int numRow = 1;

        generateHeader();

        ColumnsInformationBox box = new ColumnsInformationBox(getWorkbookIn().getSheetAt(0));
        box.extractInformationsFromFile();
        FileInformationExtractor extractor = new FileInformationExtractor(getWorkbookIn(),getOptin());
        ArrayList<Student> students = extractor.findIntershipStudents();
        HashMap<Student, Integer> studentHashMap = extractor.createStudentsHashMap();
        EmailFinder emailFinder = new EmailFinder(nameOfEmailSheet(),getEmailsWorkbook(),studentHashMap);

        for(Student student : students)
        {
            student.setLevel(getLevel());
            emailFinder.setStudent(student);
            emailFinder.getEmails();
            student.setStudentInformations();
            if(!student.hasEmail())
            {
                student.setPositionInWorkbookOut(numRow);
                this.getListOfStudentsWithoutEmail().add(student);
            }
            generateRow(numRow,student);
            numRow++;
        }
        File file = new File(getFilePathOut());
        saveUsersList(file);

        System.out.println("Temps d'execution : " + (System.currentTimeMillis()-startTime));


    }


}
