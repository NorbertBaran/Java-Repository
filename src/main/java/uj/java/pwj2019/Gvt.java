package uj.java.pwj2019;

import javafx.css.converter.LadderConverter;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class Gvt {

    private static String[] gvtArgs;
    private static File gvtFile;

    private static void executeCommand(String[] args){
        gvtArgs=args;
        gvtFile=(Files.exists(Paths.get("./.gvt")) ? new File("./.gvt") : null);

        switch (gvtArgs[0]){
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

    static class CheckPoints {
        private static void existingInitErr(){
            if(gvtFile!=null){
                System.out.println("Current directory is already initialized.");
                System.exit(10);
            }
        }
        private static void successInit(){
            System.out.println("Current directory initialized successfully");
            //System.exit(0);
        }
        private static void notExistingInitErr(){
            if(gvtFile==null) {
                System.out.println("Current directory is not initialized. Please use \"init\" command to initialize.");
                System.exit(-2);
            }
        }
        private static void missedAddFileNameArgErr(String fileName){
            if(fileName==null){
                System.out.println("Please specify file to add.");
                System.exit(20);
            }
        }
        private static void notExistingFileToAddErr(String fileName){
            Path filePath=Paths.get(fileName);
            if(!Files.exists(filePath)){
                System.out.println("File "+fileName+" not found.");
                System.exit(21);
            }
        }
        private static void addedFileBefore(String fileName){
            Path filePath=Paths.get(fileName);
            Integer versionNr=getGvtVersionNr();
            System.out.println("./.gvt/" + versionNr + "/"+ fileName);
            if(Files.exists(Paths.get("./.gvt/" + versionNr + "/"+ fileName))){
                System.out.println("File "+fileName+" already added.");
                System.exit(0);
            }
        }
        private static void notDefinedAddErr(String fileName, Exception e){
            System.out.println("File"+fileName+"cannot be added, see ERR for details.");
            e.printStackTrace();
            System.exit(22);
        }
        private static void successAdd(String fileName){
            System.out.println("File"+fileName+"added successfully.");
            //System.exit(0);
        }
        private static void missedDetachFileNameArgErr(String fileName){
            if(fileName==null){
                System.out.println("Please specify file to detach.");
                System.exit(30);
            }
        }
        private static void notAddedFileToDetachErr(String fileName){
            Integer versionNr=getGvtVersionNr();
            Path filePath=Paths.get("./.gvt/"+versionNr+"/"+fileName);
            if(!Files.exists(filePath)){
                System.out.println("File "+fileName+" is not added to gvt.");
                //System.exit();
            }
        }
        private static void notDefinedDetachErr(String fileName, Exception e){
            System.out.println("File"+fileName+"cannot be detached, see ERR for details.");
            e.printStackTrace();
            System.exit(31);
        }
        private static void successDetach(String fileName){
            System.out.println("File"+fileName+"added successfully.");
            //System.exit(0);
        }

        private static void incorrectVersionNrToCheckout(String version){
            try{
                Integer checkoutVersion=Integer.parseInt(version);
                if(checkoutVersion<0 || checkoutVersion>getGvtVersionNr()){
                    System.out.println("Invalid version number: "+version);
                    System.exit(40);
                }
            }catch(Exception e){
                System.exit(40);
            }
        }
        private static void notDefinedCheckoutErr(String fileName, Exception e){
            e.printStackTrace();
        }
        private static void successCheckout(Integer version){
            System.out.println("Version"+version+"checked out successfully.");
            //System.exit(0);
        }

        private static void missedCommitFileNameArgErr(String fileName){
            if(fileName==null){
                System.out.println("Please specify file to commit.");
                System.exit(50);
            }
        }
        private static void notAddedFileToCommitErr(String fileName){
            Integer versionNr=getGvtVersionNr();
            Path filePath=Paths.get("./.gvt/"+versionNr+"/"+fileName);
            if(!Files.exists(filePath)){
                System.out.println("File "+fileName+" is not added to gvt.");
                //System.exit();
            }
        }
        private static void notExistingFileToCommitErr(String fileName){
            Path filePath=Paths.get(fileName);
            if(!Files.exists(filePath)){
                System.out.println("File"+fileName+"does not exist.");
                System.exit(51);
            }
        }
        private static void notDefinedCommitErr(String fileName, Exception e){
            System.out.println("File"+fileName+"cannot be detached, see ERR for details.");
            e.printStackTrace();
            System.exit(-52);
        }
        private static void successCommit(String fileName){
            System.out.println("File "+fileName+" committed successfully.");
            //System.exit(0);
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

    private static Integer getGvtVersionNr(){
        try {
            BufferedReader versionReader =new BufferedReader(new FileReader("./.gvt/version.txt"));
            String versionNr=versionReader.readLine();
            return Integer.parseInt(versionNr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyDirectoryRec(File sourceLocation, File targetLocation) throws IOException {

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

    private static void copyOldCommit(){
        Integer versionNr=getGvtVersionNr();
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

    private static  void makeCommitMessageTemplate(String fileName, String template){
        Integer versionNr=getGvtVersionNr()+1;
        try{
            BufferedWriter versionWriter = new BufferedWriter(new FileWriter("./.gvt/version.txt"));
            versionWriter.write(versionNr.toString());
            versionWriter.close();

            BufferedWriter messageWriter = new BufferedWriter(new FileWriter("./.gvt/"+versionNr.toString()+"/message.txt"));
            messageWriter.write(template+" file: "+fileName+".\n");

            String addedCommand=(gvtArgs.length>=3 ? gvtArgs[2] : "");
            String addedMessage=(gvtArgs.length>=4 ? gvtArgs[3] : null);

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

    private static void addChanges(String fileName){
        Integer versionNr=getGvtVersionNr()+1;
        try {
            Path notControlledFile=Paths.get("./"+fileName);
            Path controlledFile=Paths.get("./.gvt/"+versionNr+"/"+fileName);
            Files.copy(notControlledFile, controlledFile);

            makeCommitMessageTemplate(fileName, "Added");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void add(){
        CheckPoints.notExistingInitErr();
        String fileName=(gvtArgs.length>=2 ? gvtArgs[1] : null);
        CheckPoints.missedAddFileNameArgErr(fileName);
        CheckPoints.notExistingFileToAddErr(fileName);
        CheckPoints.addedFileBefore(fileName);

        try {
            copyOldCommit();
            addChanges(fileName);
            CheckPoints.successAdd(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedAddErr(fileName, e);
        }
    }



    private static void detachFile(String fileName){
        Integer versionNr=getGvtVersionNr()+1;
        try {
            Path controlledFile=Paths.get("./.gvt/"+versionNr+"/"+fileName);
            Files.delete(controlledFile);


            makeCommitMessageTemplate(fileName, "Detached");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void detach(){
        CheckPoints.notExistingInitErr();
        String fileName=(gvtArgs.length>=2 ? gvtArgs[1] : null);
        CheckPoints.missedDetachFileNameArgErr(fileName);
        CheckPoints.notAddedFileToDetachErr(fileName);

        try{
            copyOldCommit();
            detachFile(fileName);
            CheckPoints.successDetach(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedDetachErr(fileName, e);
        }
    }

    private static void checkAndRemovePath(String latest, Path fromOld, Integer version){
        if(!Files.isDirectory(fromOld)){
            if(!Files.exists(Paths.get(latest+fromOld.toString().substring(version.toString().length()+8)))){
                //System.out.println(fromOld.toString().substring(getGvtVersionNr().toString().length()+7));
                try {
                    Files.delete(fromOld);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                //System.out.println(version.toString());
                //System.out.println(latest+fromOld.toString().substring(version.toString().length()+8));
                //System.out.println(fromOld.toString());
                //System.out.println(fromOld.toString().substring(version.toString().length()+8));
            }
        }
    }

    private static void removeDetachedLater(String latest, String older, Integer version){
        try (Stream<Path> paths = Files.walk(Paths.get(older))) {
            //paths.forEach(System.out::println);
            //paths.forEach(Gvt::checkAndRemovePath);
            paths.forEach(
                    i->{
                        //if(!Files.isDirectory(i)){
                        //    System.out.println(i);
                        //    System.out.println(i.toString().substring(getGvtVersionNr().toString().length()+7));
                        //}
                        checkAndRemovePath(latest, i, version);
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkAndAddPath(Path fromLatest, String old){
        if(!Files.isDirectory(fromLatest)){
            if(!Files.exists(Paths.get(old+fromLatest.toString().substring(getGvtVersionNr().toString().length()+8)))){
                //System.out.println(fromOld.toString().substring(getGvtVersionNr().toString().length()+7));
                try {
                    //System.out.println(getGvtVersionNr().toString());
                    //System.out.println(getGvtVersionNr().toString().length());
                    //System.out.println(fromLatest.toString());
                    //System.out.println(fromLatest.toString().substring(getGvtVersionNr().toString().length()+7));
                    Files.copy(fromLatest, Paths.get(old+fromLatest.toString().substring(getGvtVersionNr().toString().length()+8)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createAddedLater(String latest, String older){
        try (Stream<Path> paths = Files.walk(Paths.get(latest))) {
            //paths.forEach(System.out::println);
            //paths.forEach(Gvt::checkAndRemovePath);
            paths.forEach(
                    i->{
                        //if(!Files.isDirectory(i)){
                        //    System.out.println(i);
                        //    System.out.println(i.toString().substring(getGvtVersionNr().toString().length()+7));
                        //}
                        checkAndAddPath(i, older);
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkout(){
        CheckPoints.notExistingInitErr();
        //Integer checkoutVersion=(gvtArgs.length>=2 ? Integer.parseInt(gvtArgs[1]) : null);
        String version=gvtArgs[1];
        CheckPoints.incorrectVersionNrToCheckout(version);
        Integer checkoutVersion=Integer.parseInt(version);
        try{
            File checkoutFile=new File("./.gvt/"+checkoutVersion);
            File nativeFile=new File("./");
            copyDirectoryRec(checkoutFile, nativeFile);

            removeDetachedLater("./.gvt/"+getGvtVersionNr().toString()+"/", "./.gvt/"+checkoutVersion.toString()+"/", checkoutVersion);
            createAddedLater("./.gvt/"+getGvtVersionNr().toString()+"/", "./.gvt/"+checkoutVersion.toString()+"/");

            for(int i=checkoutVersion+1; i<=getGvtVersionNr(); i++){
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
        String fileName=(gvtArgs.length>=2 ? gvtArgs[1] : null);
        CheckPoints.missedCommitFileNameArgErr(fileName);
        CheckPoints.notAddedFileToCommitErr(fileName);
        CheckPoints.notExistingFileToCommitErr(fileName);

        try{
            copyOldCommit();
            makeCommitMessageTemplate(fileName, "Added");
            CheckPoints.successCommit(fileName);
        }catch (Exception e){
            CheckPoints.notDefinedCommitErr(fileName, e);
        }
    }

    private static void history(){
        Integer versionNr=getGvtVersionNr();
        int lastN=(gvtArgs.length>=2 ? Integer.parseInt(gvtArgs[1]) : versionNr+1);
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
        int version=(gvtArgs.length>=2 ? Integer.parseInt(gvtArgs[1]) : getGvtVersionNr());
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
