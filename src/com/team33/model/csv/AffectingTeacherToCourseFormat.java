package com.team33.model.csv;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;

/**
 * Created by Amine on 13/02/2017.
 */
public class AffectingTeacherToCourseFormat implements CSVFormat {

    private XSSFWorkbook workbookIn;
    private XSSFWorkbook workbookOut;
    private XSSFWorkbook EmailsWorkbook;
    private Row header = null;



    @Override
    public String buildCSV(ArrayList<String> workbooksPaths) {
        return null;
    }
}
