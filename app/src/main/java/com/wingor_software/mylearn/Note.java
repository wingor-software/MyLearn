package com.wingor_software.mylearn;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Klasa odpowiadająca za informacje o notatce
 */
public class Note implements Serializable
{
    private int ID;
    private String title;
    private int subjectID;
    private int color;

    private String content;
    private String photoPath;
    private String filePath;

    public Note(int ID, String title, String content, int subjectID, int color, String photoPath, String filePath) {
        this.ID = ID;
        this.title = title;
        this.subjectID = subjectID;
        this.color = color;

        this.content = content;
        this.photoPath = photoPath;
        this.filePath = filePath;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoPath(){
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getFilePath()
    {
        return filePath;
    }
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }



    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String[] photosPathsToStringArray()
    {
        return photoPath.split("\n");
    }

    public String[] filesPathsToStringArray()
    {
        return filePath.split("\n");
    }

    public ArrayList<Uri> getPhotoUris()
    {
        String[] str = photosPathsToStringArray();
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            uriArrayList.add(Uri.parse(str[i]));
        }
        return uriArrayList;
    }

    public void addPhoto(String newPhotoPath)
    {
        photoPath += ("\n" + newPhotoPath);
    }

    public void deletePhoto(String photoPath)
    {
        if(photoPath.contains(photoPath))
        {
            photoPath = photoPath.replace(photoPath, "");
        }
    }

    public ArrayList<Uri> getFilesUris()
    {
        String[] str = filesPathsToStringArray();
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            uriArrayList.add(Uri.parse(str[i]));
        }
        return uriArrayList;
    }

    public void addFile(String newFilePath)
    {
        filePath += ("\n" + newFilePath);
    }

    public void deleteFile(String filePath)
    {
        if(filePath.contains(filePath))
        {
            filePath = filePath.replace(filePath, "");
        }
    }
}
