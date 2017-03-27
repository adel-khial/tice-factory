package com.team33.model.csv.Students;

import com.team33.model.Util;
import com.team33.model.csv.UserFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.lang.Math;
/**
 * Created by Amine on 13/02/2017.
 */
public class AffectingStudentToCourseFormat extends UserFormat {

    private CourseFormat courseFormat;
    private ArrayList<Student> listOfStudentsWithoutEmail;
    private String level;
    private String optin;
    private String filePathOut;


    public AffectingStudentToCourseFormat(String level, String optin,String filePathOut)  {
        this.courseFormat = new CourseFormat();
        this.courseFormat.openWrkbook();
        this.listOfStudentsWithoutEmail = new ArrayList<>();
        this.level = level;
        this.optin = optin;
        if(filePathOut == null || filePathOut.equals(""))
        {
            if(optin.equals("CPI") || level.equals("1CS"))
                filePathOut = level + "courses";
            else filePathOut = level + optin +"courses";
        }
        this.filePathOut = filePathOut+".xlsx";
    }

    public ArrayList<Student> getListOfStudentsWithoutEmail() {
        return listOfStudentsWithoutEmail;
    }

    private void generateHeader(int numberOfOptionalModules)// gener le header ie ecrire dans la première ligne (username,fistname,lastname,email) -> le format accepté par moodle
    {

        for (int i = 0; i < (5 + courseFormat.getNumberOfCourses(this.level,this.optin) + numberOfOptionalModules);i++)
        {
            this.getHeader().createCell(i);
        }

        this.getHeader().getCell(0).setCellValue("username");
        this.getHeader().getCell(1).setCellValue("password");
        this.getHeader().getCell(2).setCellValue("firstname");
        this.getHeader().getCell(3).setCellValue("lastname");
        this.getHeader().getCell(4).setCellValue("email");
        for(int i = 0; i < courseFormat.getNumberOfCourses(this.level,this.optin)+numberOfOptionalModules; i++)
        {
            this.getHeader().getCell(i+5).setCellValue("course"+(i+1));
        }
    }

    public void generateRow(int numRow, Student student)// générer une ligne cde fichier résultat contenant les coordonné d'un étudiant
    {
        Row rw = this.getWorkbookOut().getSheetAt(0).createRow(numRow);
        for (int i = 0; i < 5;i++) {
            rw.createCell(i);
        }
        rw.getCell(0).setCellValue(student.getUsername());
        rw.getCell(1).setCellValue(student.getPassword());
        rw.getCell(2).setCellValue(student.getFirstName());
        rw.getCell(3).setCellValue(student.getLastNameInMoodle());
        rw.getCell(4).setCellValue(student.getEmail());
        for(int i = 0; i < student.numberOfCourses(); i++)
        {

            rw.createCell(i+5).setCellValue(student.getCourses().get(i));
        }
    }

    private int maxNumberOfOptionalModules(HashMap<String,ArrayList<String>> optionalModules)
    {
        int max = 0;
        if(optionalModules!=null)
        {
            Set<String> keySet = optionalModules.keySet();
            for(String key : keySet)
            {
               max = java.lang.Math.max(max,optionalModules.get(key).size());
            }
        }
        return max;
    }

    private String nameOfEmailSheet()
    {
        String sheetName = "";
        if(this.optin.equals("CPI"))
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

    private void createStudentList()  {
        int numRow = 1;



        FileInformationExtractor extractor = new FileInformationExtractor(getWorkbookIn(),optin);
        ArrayList<Student> students = extractor.findStudents();
        HashMap<Student,Integer> studentHashMap  =  extractor.createStudentsHashMap();
        EmailFinder emailFinder = new EmailFinder(nameOfEmailSheet(),getEmailsWorkbook(),studentHashMap);

        HashMap<String,ArrayList<String>> optionalModules = null;
        if(level.equals("2CS")) optionalModules = extractor.extractOptionalModules();
        generateHeader(maxNumberOfOptionalModules(optionalModules));
        for(Student student : students)
        {
            student.setLevel(level);
            emailFinder.setStudent(student);
            emailFinder.getEmails();
            student.setStudentInformations();
            student.allocateCourses(this.courseFormat,optionalModules);
            if(!student.hasEmail())
            {
                student.setPositionInWorkbookOut(numRow);
                this.listOfStudentsWithoutEmail.add(student);
            }
            generateRow(numRow,student);
            numRow++;
        }
        File file = new File(filePathOut);
        saveUsersList(file);
    }



    @Override
    public String buildCSV(ArrayList<String> workbooksPaths)  {
        // WorkbooksPaths should contain only list of first semester and list of e-mails

        String type;
        FileInformationExtractor extractor = new FileInformationExtractor();
        for (String workbooksPath : workbooksPaths) {
            if(workbooksPath.contains(".docx"))
            {

                workbooksPath = extractor.ConvertWordTableToExcel(workbooksPath);
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
