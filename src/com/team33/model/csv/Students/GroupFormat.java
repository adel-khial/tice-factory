package com.team33.model.csv.Students;

import com.team33.model.Util;
import com.team33.model.csv.CSVFormat;
import com.team33.model.csv.UserFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Amine on 13/02/2017.
 */
public abstract class GroupFormat extends UserFormat implements CSVFormat {

    private CourseFormat courseFormat;
    private ArrayList<Student> listOfStudentsWithoutEmail;
    private String level;
    private String optin;
    private String filePathOut;
    private int maxNbOptionalsModules;


    public GroupFormat(String level, String optin, String filePathOut) {
        this.courseFormat = new CourseFormat();
        this.courseFormat.openWrkbook();
        this.listOfStudentsWithoutEmail = new ArrayList<>();
        this.level = level;
        this.optin = optin;
        if (filePathOut == null || filePathOut.equals("")) {
            if (optin.equals("CPI") || level.equals("1CS"))
                filePathOut = level + "groupes";
            else filePathOut = level + optin + "groupes";
        }
        this.filePathOut = filePathOut + ".xlsx";
    }

    public CourseFormat getCourseFormat() {
        return courseFormat;
    }


    public String getLevel() {
        return level;
    }

    public String getOptin() {
        return optin;
    }

    public String getFilePathOut() {
        return filePathOut;
    }

    public int getMaxNbOptionalsModules() {
        return maxNbOptionalsModules;
    }

    public void setMaxNbOptionalsModules(int maxNbOptionalsModules) {
        this.maxNbOptionalsModules = maxNbOptionalsModules;
    }

    public ArrayList<Student> getListOfStudentsWithoutEmail() {
        return listOfStudentsWithoutEmail;
    }

    protected void generateHeader(int numberOfOptionalModules)// gener le header ie ecrire dans la première ligne (username,fistname,lastname,email) -> le format accepté par moodle
    {

        for (int i = 0; i < 5 ; i++) {
            this.getHeader().createCell(i);
        }

        this.getHeader().getCell(0).setCellValue("username");
        this.getHeader().getCell(1).setCellValue("password");
        this.getHeader().getCell(2).setCellValue("firstname");
        this.getHeader().getCell(3).setCellValue("lastname");
        this.getHeader().getCell(4).setCellValue("email");
        int j = 5;
        for (int i = 0; i < courseFormat.getNumberOfCourses(this.level, this.optin) + numberOfOptionalModules; i++) {
            this.getHeader().createCell(i + j).setCellValue("course" + (i + 1));
            this.getHeader().createCell(i + j + 1).setCellValue("group" + (i + 1));
            j = j + 1;
        }
    }

    protected void generateRow(int numRow, Student student)// générer une ligne cde fichier résultat contenant les coordonné d'un étudiant
    {
        Row rw = this.getWorkbookOut().getSheetAt(0).createRow(numRow);
        for (int i = 0; i < 5; i++) {
            rw.createCell(i);
        }
        rw.getCell(0).setCellValue(student.getUsername());
        rw.getCell(1).setCellValue(student.getPassword());
        rw.getCell(2).setCellValue(student.getFirstName());
        rw.getCell(3).setCellValue(student.getLastNameInMoodle());
        rw.getCell(4).setCellValue(student.getEmail());
        int j = 5;
        for (int i = 0; i < student.numberOfCourses(); i++) {

            rw.createCell(i + j).setCellValue(student.getCourses().get(i));
            rw.createCell(i + j +1 ).setCellValue("Groupe "+student.getGroupe());
            j = j +1;
        }
    }

    public void upadateRow(int numRow,Student student)
    {
        getWorkbookOut().getSheetAt(0).getRow(numRow).getCell(0).setCellValue(student.getUsername());
        getWorkbookOut().getSheetAt(0).getRow(numRow).getCell(4).setCellValue(student.getEmail());
    }

    protected int maxNumberOfOptionalModules(HashMap<String, ArrayList<String>> optionalModules) {
        int max = 0;
        if (optionalModules != null) {
            Set<String> keySet = optionalModules.keySet();
            for (String key : keySet) {
                max = java.lang.Math.max(max, optionalModules.get(key).size());
            }
        }
        return max;
    }


    protected String nameOfEmailSheet()
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

    protected abstract void createStudentList() throws IOException;


    @Override
    public String buildCSV(ArrayList<String> workbooksPaths)
    {
        // WorkbooksPaths should contain only list of first semester and list of e-mails

        String type;
        FileInformationExtractor extractor = new FileInformationExtractor();
        for (String workbooksPath : workbooksPaths) {
            if (workbooksPath.contains(".docx")) {

                workbooksPath = extractor.ConvertWordTableToExcel(workbooksPath,this.optin);
            }
            File file = new File(workbooksPath);
            type = extractor.getFileType(file);
            if (type.equals("Solarite")) openWorkbookIn(workbooksPath);
            else openEmailWorkbook(workbooksPath);
        }

        try {
            createStudentList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePathOut;
    }
}