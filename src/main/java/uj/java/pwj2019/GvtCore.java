package uj.java.pwj2019;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class GvtCore {
    static String[] args;
    static Integer versionNr;
    static Integer setVersionNr;

    static void setInitialVariableValue(String[] args){
        GvtCore.args=args;
        try {
            if(Files.exists(Paths.get("./.gvt/version.txt"))){
                BufferedReader versionReader =new BufferedReader(new FileReader("./.gvt/version.txt"));
                GvtCore.versionNr=Integer.parseInt(versionReader.readLine());
                BufferedReader checkVersionReader =new BufferedReader(new FileReader("./.gvt/setVersion.txt"));
                GvtCore.setVersionNr =Integer.parseInt(checkVersionReader.readLine());
            }else{
                GvtCore.versionNr=-1;
                GvtCore.versionNr=-1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void execute(){
        if(args.length==0){
            System.out.println("Please specify command.");
            System.exit(1);
        }
        switch (GvtCore.args[0]){
            case "init":
                Gvt.init();
                break;
            case "add":
                Gvt.add();
                break;
            case "detach":
                Gvt.detach();
                break;
            case "checkout":
                Gvt.checkout();
                break;
            case "commit":
                Gvt.commit();
                break;
            case "history":
                Gvt.history();
                break;
            case "version":
                Gvt.version();
                break;
            default:
                System.out.println("Unknown command "+args[0]+".");
                System.exit(1);
        }
    }

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

    static void updateVersion(String message){
        try{
            versionNr++;
            setVersionNr=versionNr;
            BufferedWriter versionWriter = new BufferedWriter(new FileWriter("./.gvt/version.txt"));
            versionWriter.write(versionNr.toString());
            versionWriter.close();
            BufferedWriter setVersionWriter = new BufferedWriter(new FileWriter("./.gvt/setVersion.txt"));
            setVersionWriter.write(setVersionNr.toString());
            setVersionWriter.close();

            //Files.createDirectory(Paths.get("./.gvt/"+versionNr));
            BufferedWriter messageWriter = new BufferedWriter(new FileWriter("./.gvt/"+versionNr.toString()+"/message.txt"));
            messageWriter.write(message);
            versionWriter.close();

            String addedCommand=(args.length>=3 ? args[2] : "");
            String addedMessage=(args.length>=4 ? args[3] : null);

            if(addedCommand.equals("-m") && addedMessage!=null)
                messageWriter.write(addedMessage);
            //else
            //    messageWriter.write(message);

            messageWriter.close();
        }catch (Exception e){
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
