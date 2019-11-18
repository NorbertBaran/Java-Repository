package uj.java.pwj2019;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class Gvt {



    private static void executeCommand(String[] args){
        GvtCore.args=args;

        try {
            BufferedReader versionReader =new BufferedReader(new FileReader("./.gvt/version.txt"));
            GvtCore.versionNr=Integer.parseInt(versionReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (GvtCore.args[0]){
            case "init":
                init();
                break;
            case "add":
                add();
                break;
            case "detach":
                detach();
                break;
            case "checkout":
                checkout();
                break;
            case "commit":
                commit();
                break;
            case "history":
                history();
                break;
            case "version":
                version();
                break;
        }
    }

    private static void init() {
        CheckPoints.existingInitErr();

        try {
            Files.createDirectory(Paths.get("./.gvt"));
            Files.createDirectory(Paths.get("./.gvt/0"));

            File version=new File("./.gvt/version.txt");
            File message=new File("./.gvt/0/message.txt");

            BufferedWriter versionWriter = new BufferedWriter(new FileWriter("./.gvt/version.txt"));
            versionWriter.write("0");
            versionWriter.close();

            BufferedWriter messageWriter = new BufferedWriter(new FileWriter("./.gvt/0/message.txt"));
            messageWriter.write("GVT initialized.");
            messageWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CheckPoints.successInit();
    }

    private static void add(){
        CheckPoints.notExistingInitErr();
        String fileName=(GvtCore.args.length>=2 ? GvtCore.args[1] : null);
        CheckPoints.missedAddFileNameArgErr(fileName);
        CheckPoints.notExistingFileToAddErr(fileName);
        CheckPoints.addedFileBefore(fileName);

        try {
            GvtCore.copyOldCommit();
            GvtCore.addChanges(fileName);
            CheckPoints.successAdd(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedAddErr(fileName, e);
        }
    }

    private static void detach(){
        CheckPoints.notExistingInitErr();
        String fileName=(GvtCore.args.length>=2 ? GvtCore.args[1] : null);
        CheckPoints.missedDetachFileNameArgErr(fileName);
        CheckPoints.notAddedFileToDetachErr(fileName);

        try{
            GvtCore.copyOldCommit();
            GvtCore.detachFile(fileName);
            CheckPoints.successDetach(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedDetachErr(fileName, e);
        }
    }



    private static void checkout(){
        CheckPoints.notExistingInitErr();
        //Integer checkoutVersion=(gvtArgs.length>=2 ? Integer.parseInt(gvtArgs[1]) : null);
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

    private static void commit(){
        CheckPoints.notExistingInitErr();
        String fileName=(GvtCore.args.length>=2 ? GvtCore.args[1] : null);
        CheckPoints.missedCommitFileNameArgErr(fileName);
        CheckPoints.notAddedFileToCommitErr(fileName);
        CheckPoints.notExistingFileToCommitErr(fileName);

        try{
            GvtCore.copyOldCommit();
            GvtCore.makeCommitMessageTemplate(fileName, "Added");
            CheckPoints.successCommit(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedCommitErr(fileName, e);
        }
    }

    private static void history(){
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

    private static void version(){
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
        executeCommand(args);
    }
}
