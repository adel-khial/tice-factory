package com.team33.model.csv.Students;

import com.team33.model.Utilities.Util;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

/**
 * Created by hamza on 19/03/2017.
 */
    class ColumnsInformationBox {
    private Sheet sheet;
    private int colNom;
    private int colPrenom;
    private int colGroupe;
    private int  colOptin;
    private int colMatrin;

    public ColumnsInformationBox(Sheet sheet)
    {
        this.sheet = sheet;
    }

    public int getColNom() {
        return colNom;
    }

    public int getColPrenom() {
        return colPrenom;
    }

    public int getColGroupe() {
        return colGroupe;
    }

    public int getColOptin() {
        return colOptin;
    }

    public int getColMatrin() {
        return colMatrin;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public void extractInformationsFromFile()
    {
        boolean found = false;
        Iterator<Row> rowIterator = this.sheet.rowIterator();
        Row rw = rowIterator.next();
        while ((!found) && (rowIterator.hasNext())) {
            if (Util.getInstance().existInRow(rw, "Prenom")) {
                found = true;
                this.colNom = Util.getInstance().column(rw, "Nom");
                this.colPrenom = Util.getInstance().column(rw, "Prenom");
                this.colGroupe = Util.getInstance().column(rw, "NG");
                this.colOptin = Util.getInstance().column(rw, "Optin");
                this.colMatrin = Util.getInstance().column(rw,"Matrin");
            } else if (Util.getInstance().existInRow(rw, "Prénom")) {
                found = true;
                this.colNom = Util.getInstance().column(rw, "Nom");
                this.colPrenom = Util.getInstance().column(rw, "Prénom");
                this.colGroupe = Util.getInstance().column(rw, "NG");
                this.colOptin = Util.getInstance().column(rw, "Optin");
                this.colMatrin = Util.getInstance().column(rw,"Matrin");
            } else {
                rw = rowIterator.next();
            }
        }
    }


}
