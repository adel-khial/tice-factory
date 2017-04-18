package com.team33.model.csv.Students;

import com.team33.model.Utilities.Util;
import com.team33.model.csv.CSVFormat;
import com.team33.model.csv.UserFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.*;
import java.net.URI;
import java.security.Key;
import java.util.*;

/**
 * Created by Amine on 13/02/2017.
 */

public class StudentFormat extends UserFormat implements CSVFormat {

    private String level;
    private String optin;
    private String filePathOut;
    private ArrayList<Student> listOfStudentsWithoutEmail;
    private HashMap<String,Student> students;
    public ArrayList<Student> getListOfStudentsWithoutEmail() {
        return listOfStudentsWithoutEmail;
    }

    public StudentFormat(String level, String optin, String filePathOut) {
        this.listOfStudentsWithoutEmail = new ArrayList<Student>();
        this.level = level;
        this.optin = optin;
        if(filePathOut == null || filePathOut.equals(""))
        {
            if(optin.equals("CPI") || level.equals("1CS"))
                filePathOut = level;
            else filePathOut = level + optin ;
        }
        this.filePathOut = filePathOut+".xlsx";
    }

    @Override
    public void generateHeader() {
        super.generateHeader();
        getHeader().createCell(5).setCellValue("idnumber");
    }

    /*
     *Méthodes utilitaires
     */


    private void generateRow(int numRow, Student student)// générer une ligne cde fichier résultat contenant les coordonné d'un étudiant
    {
        Row rw = getWorkbookOut().getSheetAt(0).createRow(numRow);

        rw.createCell(0).setCellValue(student.getUsername());
        rw.createCell(1).setCellValue(student.getPassword());
        rw.createCell(2).setCellValue(student.getFirstName());
        rw.createCell(3).setCellValue(student.getLastNameInMoodle());
        rw.createCell(4).setCellValue(student.getEmail());
        rw.createCell(5).setCellValue(student.getIdnumber());
    }

    public void updateRow(int numRow,Student student)
    {
        getWorkbookOut().getSheetAt(0).getRow(numRow).getCell(0).setCellValue(student.getUsername());
        getWorkbookOut().getSheetAt(0).getRow(numRow).getCell(4).setCellValue(student.getEmail());
    }


    private String nameOfEmailSheet()
    {
        String sheetName = "";
        if(this.optin.equals("CPI") || this.optin.equals("SC"))
        {
            sheetName = this.level;
        }
        else sheetName = this.level + this.optin;

        for(Sheet sheet : getEmailsWorkbook())
        {
            if(sheetName.equalsIgnoreCase(sheet.getSheetName())) return sheet.getSheetName();
        }
        return null;
    }

    private void createStudentList()
    {
        Long startTime = System.currentTimeMillis();
        System.out.println("Création de la liste ***");
        int numRow = 1;

        generateHeader();

        FileInformationExtractor extractor = new FileInformationExtractor(getWorkbookIn(), optin);
        this.students = extractor.findStudents();

        HashMap<Student, Integer> studentHashMap = extractor.createStudentsHashMap();
        EmailFinder emailFinder = new EmailFinder(nameOfEmailSheet(), getEmailsWorkbook(), studentHashMap);

        for (Map.Entry<String,Student> entry : students.entrySet())
        {
            Student student = entry.getValue();
            System.out.println(entry.getKey());
            student.setLevel(level);
            emailFinder.setStudent(student);
            emailFinder.getEmails();
            student.setStudentInformations();
            if (!student.hasEmail()) {
                student.setPositionInWorkbookOut(numRow);
                this.listOfStudentsWithoutEmail.add(student);
            }
            generateRow(numRow, student);
            numRow++;
        }
        File file = new File(filePathOut);
        saveUsersList(file);
        System.out.println("Temps d'execution : " + (System.currentTimeMillis()-startTime));
    }

    public void saveCreatedUsers() throws IOException
    {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(this.level+this.optin+".data")));

        oos.writeObject(students);
        oos.close();
    }


    @Override
    public String buildCSV(ArrayList<String> workbooksPaths)  {
        // WorkbooksPaths should contain only list of first semester and list of e-mails

        String type;
        FileInformationExtractor extractor = new FileInformationExtractor();
        for (String workbooksPath : workbooksPaths) {
            if(workbooksPath.contains(".docx"))
            {

                workbooksPath = extractor.ConvertWordTableToExcel(workbooksPath,this.optin);
            }
            File file = new File(workbooksPath);
            type = extractor.getFileType(file);
            if (type.equals("Solarite")) openWorkbookIn(workbooksPath);
            else openEmailWorkbook(workbooksPath);
        }
        createStudentList();

        return filePathOut;
    }
}