package uj.java.pwj2019;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class Gvt {
    static void init() {
        CheckPoints.existingInitErr();
        try {
            Files.createDirectory(Paths.get("./.gvt"));
            GvtCore.createNewEmptyVersion("GVT initialized.");
            CheckPoints.successInit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void add(){
        CheckPoints.notExistingInitErr();
        String fileName=(GvtCore.args.length>=2 ? GvtCore.args[1] : null);
        CheckPoints.missedAddFileNameArgErr(fileName);
        CheckPoints.notExistingFileToAddErr(fileName);
        CheckPoints.addedFileBefore(fileName);
        try {
            GvtCore.createNewEmptyVersion("Added file: "+fileName+".\n");
            GvtCore.copyDirectoryRec(new File("./.gvt/"+(GvtCore.versionNr-1)+"/"), new File("./.gvt/"+GvtCore.versionNr+"/"));
            //Files.copy(Paths.get("./.gvt/"+(GvtCore.versionNr-1)+"/"), Paths.get("./.gvt/"+GvtCore.versionNr+"/"));
            Files.copy(Paths.get("./"+fileName), Paths.get("./.gvt/"+GvtCore.versionNr+"/"+fileName));
            CheckPoints.successAdd(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedAddErr(fileName, e);
        }
    }

    static void detach(){
        CheckPoints.notExistingInitErr();
        String fileName=(GvtCore.args.length>=2 ? GvtCore.args[1] : null);
        CheckPoints.missedDetachFileNameArgErr(fileName);
        CheckPoints.notAddedFileToDetachErr(fileName);
        try{
            GvtCore.createNewEmptyVersion("Detached file: "+fileName+".\n");
            GvtCore.copyDirectoryRec(new File("./.gvt/"+(GvtCore.versionNr-1)+"/"), new File("./.gvt/"+GvtCore.versionNr+"/"));
            //Files.copy(Paths.get("./.gvt/"+(GvtCore.versionNr-1)+"/"), Paths.get("./.gvt/"+GvtCore.versionNr+"/"));
            Files.delete(Paths.get("./.gvt/"+GvtCore.versionNr+"/"+fileName));
            CheckPoints.successDetach(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedDetachErr(fileName, e);
        }
    }

    static void checkout(){
        CheckPoints.notExistingInitErr();
        String version=GvtCore.args[1];
        CheckPoints.incorrectVersionNrToCheckout(version);
        Integer checkoutVersion=Integer.parseInt(version);
        try{
            File checkoutFile=new File("./.gvt/"+checkoutVersion);
            File nativeFile=new File("./");
            GvtCore.copyDirectoryRec(checkoutFile, nativeFile);

            GvtCore.removeDetachedLater("./.gvt/"+GvtCore.versionNr.toString()+"/", "./.gvt/"+checkoutVersion.toString()+"/", checkoutVersion);
            GvtCore.createAddedLater("./.gvt/"+GvtCore.versionNr.toString()+"/", "./.gvt/"+checkoutVersion.toString()+"/");

            for(int i=checkoutVersion+1; i<=GvtCore.versionNr; i++){
                Path directory = Paths.get("./.gvt/"+i);
                Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            CheckPoints.successCheckout(checkoutVersion);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static void commit(){
        CheckPoints.notExistingInitErr();
        String fileName=(GvtCore.args.length>=2 ? GvtCore.args[1] : null);
        CheckPoints.missedCommitFileNameArgErr(fileName);
        CheckPoints.notAddedFileToCommitErr(fileName);
        CheckPoints.notExistingFileToCommitErr(fileName);
        try{
            GvtCore.createNewEmptyVersion("Committed file: "+fileName+".\n");
            GvtCore.copyDirectoryRec(new File("./.gvt/"+(GvtCore.versionNr-1)+"/"), new File("./.gvt/"+GvtCore.versionNr+"/"));
            //Files.copy(Paths.get("./.gvt/"+(GvtCore.versionNr-1)+"/"), Paths.get("./.gvt/"+GvtCore.versionNr+1+"/"));
            Files.delete(Paths.get("./.gvt/"+GvtCore.versionNr+"/"+fileName));
            Files.copy(Paths.get("./"+fileName), Paths.get("./.gvt/"+GvtCore.versionNr+"/"+fileName));
            CheckPoints.successCommit(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedCommitErr(fileName, e);
        }
    }

    static void history(){
        Integer versionNr=GvtCore.versionNr;
        int lastN=(GvtCore.args.length>=2 ? Integer.parseInt(GvtCore.args[1]) : versionNr+1);
        try {
            for(int i=versionNr-lastN+1; i<=versionNr; i++){
                BufferedReader messageReader = new BufferedReader(new FileReader("./.gvt/"+i+"/message.txt"));
                System.out.println(i+": "+messageReader.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void version(){
        int version=(GvtCore.args.length>=2 ? Integer.parseInt(GvtCore.args[1]) : GvtCore.versionNr);
        try{
            System.out.println("Version: "+version);
            Stream<String> stream = Files.lines(Paths.get("./.gvt/"+version+"/message.txt"));
            stream.forEach(System.out::println);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        GvtCore.setInitialVariableValue(args);
        GvtCore.execute();
    }
}
