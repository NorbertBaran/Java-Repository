package uj.java.pwj2019;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckPoints {
    static void existingInitErr(){
        if(Files.exists(Paths.get("./.gvt"))){
            System.out.println("Current directory is already initialized.");
            System.exit(10);
        }
    }
    static void successInit(){
        System.out.println("Current directory initialized successfully.");
        //System.exit(0);
    }
    static void notExistingInitErr(){
        if(!Files.exists(Paths.get("./.gvt"))) {
            System.out.println("Current directory is not initialized. Please use \"init\" command to initialize.");
            System.exit(-2);
        }
    }
    static void missedAddFileNameArgErr(String fileName){
        if(fileName==null){
            System.out.println("Please specify file to add.");
            System.exit(20);
        }
    }
    static void notExistingFileToAddErr(String fileName){
        Path filePath= Paths.get(fileName);
        if(!Files.exists(filePath)){
            System.out.println("File "+fileName+" not found.");
            System.exit(21);
        }
    }
    static void addedFileBefore(String fileName){
        Path filePath=Paths.get(fileName);
        if(Files.exists(Paths.get("./.gvt/" + GvtCore.versionNr + "/"+ fileName))){
            System.out.println("File "+fileName+" already added.");
            System.exit(0);
        }
    }
    static void notDefinedAddErr(String fileName, Exception e){
        System.out.println("File"+fileName+"cannot be added, see ERR for details.");
        e.printStackTrace();
        System.exit(22);
    }
    static void successAdd(String fileName){
        System.out.println("File "+fileName+" added successfully.");
        //System.exit(0);
    }
    static void missedDetachFileNameArgErr(String fileName){
        if(fileName==null){
            System.out.println("Please specify file to detach.");
            System.exit(30);
        }
    }
    static void notAddedFileToDetachErr(String fileName){
        //Path filePath=Paths.get("./.gvt/"+GvtCore.setVersionNr+"/"+fileName);
        if(!Files.exists(Paths.get("./.gvt/"+GvtCore.setVersionNr+"/"+fileName))){
            System.out.println("File "+fileName+" is not added to gvt.");
            //System.out.println();
            //return;
            System.exit(0);
        }
    }
    /*static boolean notAddedFileToDetachErr(String fileName){
        Path filePath=Paths.get("./.gvt/"+GvtCore.setVersionNr+"/"+fileName);
        if(!Files.exists(filePath)){
            //System.out.println("File "+fileName+" is not added to gvt.");
            return true;
            //System.exit(0);
            //System.out.println("Test1");
        }
        return false;
    }*/
    static void notDefinedDetachErr(String fileName, Exception e){
        System.out.println("File "+fileName+" cannot be detached, see ERR for details.");
        e.printStackTrace();
        System.exit(31);
    }
    static void successDetach(String fileName){
        System.out.println("File "+fileName+" detached successfully.");
        //System.exit(0);
    }

    static void incorrectVersionNrToCheckout(String version){
        try{
            Integer checkoutVersion=Integer.parseInt(version);
            if(checkoutVersion<0 || checkoutVersion>GvtCore.versionNr){
                System.out.println("Invalid version number: "+version);
                System.exit(40);
            }
        }catch(Exception e){
            System.exit(40);
        }
    }
    static void notDefinedCheckoutErr(String fileName, Exception e){
        e.printStackTrace();
    }
    static void successCheckout(Integer version){
        System.out.println("Version"+version+"checked out successfully.");
        //System.exit(0);
    }

    static void missedCommitFileNameArgErr(String fileName){
        if(fileName==null){
            System.out.println("Please specify file to commit.");
            System.exit(50);
        }
    }
    static void notAddedFileToCommitErr(String fileName){
        Path filePath=Paths.get("./.gvt/"+GvtCore.versionNr+"/"+fileName);
        if(!Files.exists(filePath)){
            System.out.println("File "+fileName+" is not added to gvt.");
            //System.exit();
        }
    }
    static void notExistingFileToCommitErr(String fileName){
        Path filePath=Paths.get(fileName);
        if(!Files.exists(filePath)){
            System.out.println("File"+fileName+"does not exist.");
            System.exit(51);
        }
    }
    static void notDefinedCommitErr(String fileName, Exception e){
        System.out.println("File"+fileName+"cannot be detached, see ERR for details.");
        e.printStackTrace();
        System.exit(-52);
    }
    static void successCommit(String fileName){
        System.out.println("File "+fileName+" committed successfully.");
        //System.exit(0);
    }
}
