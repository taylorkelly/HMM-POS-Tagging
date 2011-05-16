/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmmposttagging.output;

import hmmpostagging.Main;
import hmmpostagging.hmm.HMM;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author taylor
 */
public class Saver {
    public static void store(String fileName, Map<? extends Object, ? extends Object> map) {
        File file = new File(Main.FOLDER, fileName);
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            for (Entry entry : map.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                bwriter.write(value.toString());
                bwriter.write(" - ");
                if (key instanceof Entry) {
                    bwriter.write(((Entry) key).getKey().toString());
                    bwriter.write(": ");
                    bwriter.write(((Entry) key).getValue().toString());
                } else {
                    bwriter.write(key.toString());

                }
                bwriter.newLine();
            }
            bwriter.flush();
        } catch (IOException e) {
            System.out.println("IO Exception (" + fileName + ")");
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                System.out.println("IO Exception (on close) (" + fileName + ")");
            }
        }
    }

    public static void save(HMM hmm) {
        File file = new File(Main.FOLDER, "hmm.save");
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(file);
            bwriter = new BufferedWriter(fwriter);
            hmm.save(bwriter);
            bwriter.flush();
        } catch (IOException e) {
            System.out.println("IO Exception (hmm.save)");
        } finally {
            try {
                if (bwriter != null) {
                    bwriter.flush();
                    bwriter.close();
                }
                if (fwriter != null)
                    fwriter.close();
            } catch (IOException e) {
                System.out.println("IO Exception (on close) (hmm.save)");
            }
        }
    }
}
