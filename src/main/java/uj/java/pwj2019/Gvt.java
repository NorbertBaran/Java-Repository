package uj.java.pwj2019;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Gvt {
    static void init() {
        CheckPoints.existingInitErr();
        try {
            Files.createDirectory(Paths.get("./.gvt"));
            Files.createDirectory(Paths.get("./.gvt/"+(GvtCore.versionNr+1)));
            GvtCore.updateVersion("GVT initialized.");
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
            Files.createDirectory(Paths.get("./.gvt/"+(GvtCore.versionNr+1)));
            GvtCore.copyDirectoryRec(new File("./.gvt/"+GvtCore.setVersionNr+"/"), new File("./.gvt/"+(GvtCore.versionNr+1)+"/"));
            //GvtCore.copyDirectoryRec(new File("./.gvt/"+GvtCore.versionNr+"/"), new File("./.gvt/"+(GvtCore.versionNr+1)+"/"));
            Files.copy(Paths.get("./"+fileName), Paths.get("./.gvt/"+(GvtCore.versionNr+1)+"/"+fileName));
            GvtCore.updateVersion("Added file: "+fileName+"\n");
            CheckPoints.successAdd(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedAddErr(fileName, e);
        }
    }

    static void detach(){
        CheckPoints.notExistingInitErr();
        final String fileName=(GvtCore.args.length>=2 ? new String(GvtCore.args[1].toString()) : null);
        CheckPoints.missedDetachFileNameArgErr(fileName);
        CheckPoints.notAddedFileToDetachErr(fileName);
        try{
            Files.createDirectory(Paths.get("./.gvt/"+(GvtCore.versionNr+1)));
            GvtCore.copyDirectoryRec(new File("./.gvt/"+GvtCore.setVersionNr+"/"), new File("./.gvt/"+(GvtCore.versionNr+1)+"/"));
            //GvtCore.copyDirectoryRec(new File("./.gvt/"+GvtCore.versionNr+"/"), new File("./.gvt/"+(GvtCore.versionNr+1)+"/"));
            Files.delete(Paths.get("./.gvt/"+(GvtCore.versionNr+1)+"/"+fileName));
            GvtCore.updateVersion("Detached file: "+fileName+"\n");
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
                        //Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        //Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            BufferedWriter versionWriter = new BufferedWriter(new FileWriter("./.gvt/message.txt"));
            versionWriter.write(checkoutVersion.toString());
            versionWriter.close();

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
            Files.createDirectory(Paths.get("./.gvt/"+(GvtCore.versionNr+1)));
            GvtCore.copyDirectoryRec(new File("./.gvt/"+GvtCore.setVersionNr+"/"), new File("./.gvt/"+(GvtCore.versionNr+1)+"/"));
            //GvtCore.copyDirectoryRec(new File("./.gvt/"+GvtCore.versionNr+"/"), new File("./.gvt/"+(GvtCore.versionNr+1)+"/"));
            Files.delete(Paths.get("./.gvt/"+(GvtCore.versionNr+1)+"/"+fileName));
            Files.copy(Paths.get("./"+fileName), Paths.get("./.gvt/"+(GvtCore.versionNr+1)+"/"+fileName));
            GvtCore.updateVersion("Committed file: "+fileName+"\n");
            CheckPoints.successCommit(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedCommitErr(fileName, e);
        }
    }

    static void history(){
        Integer versionNr=GvtCore.versionNr;
        int lastN=(GvtCore.args.length>=3 ? Integer.parseInt(GvtCore.args[2]) : versionNr+1);
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
            //Stream<String> stream = Files.lines(Paths.get("./.gvt/"+version+"/message.txt"));
            //stream.forEach(System.out::println);
            var array = Files.lines(Paths.get("./.gvt/"+version+"/message.txt")).toArray();
            for(int i=0; i<array.length-1; i++)
                System.out.println(array[i]);
            System.out.print(array[array.length-1]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        GvtCore.setInitialVariableValue(args);
        GvtCore.execute();
    }
}
