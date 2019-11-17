package uj.java.pwj2019;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckPoints {
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
        Path filePath= Paths.get(fileName);
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
