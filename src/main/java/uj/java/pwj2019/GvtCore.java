package uj.java.pwj2019;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class GvtCore {
    static String[] args;
    static Integer versionNr;

    static void copyDirectoryRec(File sourceLocation, File targetLocation) throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectoryRec(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    static void copyOldCommit(){
        File oldFile=new File("./.gvt/"+versionNr.toString());

        versionNr++;
        File newFile=new File("./.gvt/"+versionNr.toString());
        newFile.mkdir();

        try {
            copyDirectoryRec(oldFile, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void makeCommitMessageTemplate(String fileName, String template){
        try{
            BufferedWriter versionWriter = new BufferedWriter(new FileWriter("./.gvt/version.txt"));
            versionWriter.write(versionNr.toString());
            versionWriter.close();

            BufferedWriter messageWriter = new BufferedWriter(new FileWriter("./.gvt/"+versionNr.toString()+"/message.txt"));
            messageWriter.write(template+" file: "+fileName+".\n");

            String addedCommand=(args.length>=3 ? args[2] : "");
            String addedMessage=(args.length>=4 ? args[3] : null);

            if(addedCommand.equals("-m") && addedMessage!=null)
                messageWriter.write(addedMessage);
            else
                messageWriter.write(template+" file: "+fileName);

            versionWriter.close();
            messageWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static void addChanges(String fileName){
        try {
            Path notControlledFile= Paths.get("./"+fileName);
            Path controlledFile=Paths.get("./.gvt/"+versionNr+"/"+fileName);
            Files.copy(notControlledFile, controlledFile);

            makeCommitMessageTemplate(fileName, "Added");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void detachFile(String fileName){
        try {
            Path controlledFile=Paths.get("./.gvt/"+versionNr+1+"/"+fileName);
            Files.delete(controlledFile);


            makeCommitMessageTemplate(fileName, "Detached");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void checkAndRemovePath(String latest, Path fromOld, Integer version){
        if(!Files.isDirectory(fromOld)){
            if(!Files.exists(Paths.get(latest+fromOld.toString().substring(version.toString().length()+8)))){
                try {
                    Files.delete(fromOld);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void removeDetachedLater(String latest, String older, Integer version){
        try (Stream<Path> paths = Files.walk(Paths.get(older))) {
            paths.forEach( i->{ checkAndRemovePath(latest, i, version); });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void checkAndAddPath(Path fromLatest, String old){
        if(!Files.isDirectory(fromLatest)){
            if(!Files.exists(Paths.get(old+fromLatest.toString().substring(versionNr.toString().length()+8)))){
                try {
                    Files.copy(fromLatest, Paths.get(old+fromLatest.toString().substring(versionNr.toString().length()+8)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void createAddedLater(String latest, String older){
        try (Stream<Path> paths = Files.walk(Paths.get(latest))) {
            paths.forEach(
                    i->{ checkAndAddPath(i, older); });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
