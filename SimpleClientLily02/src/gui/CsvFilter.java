/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * ファイル選択ウィンドウのcsvフィルタ
 * @author niwatakumi
 */
public class CsvFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        /* ディレクトリなら無条件で表示する */
        if (f.isDirectory()) {
            return true;
        }

        /* 拡張子を取り出し、html又はhtmだったら表示する */
        String ext = getExtension(f);
        if (ext != null) {
            if (ext.equals("csv")) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "CSVファイル";
    }

    private String getExtension(File f) {
        String ext = null;
        String filename = f.getName();
        int dotIndex = filename.lastIndexOf('.');

        if ((dotIndex > 0) && (dotIndex < filename.length() - 1)) {
            ext = filename.substring(dotIndex + 1).toLowerCase();
        }

        return ext;
    }

}
