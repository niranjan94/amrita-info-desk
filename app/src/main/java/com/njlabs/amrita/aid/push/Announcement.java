package com.njlabs.amrita.aid.push;

public class Announcement {
    
    //private variables
    int _id;
    String _title;
    String _alert;
    String _datetime;
    String _status;
     
    // Empty constructor
    public Announcement(){
         
    }
    // constructor
    public Announcement(int id, String title, String alert, String datetime, String status){
        this._id = id;
        this._title = title;
        this._alert = alert;
        this._datetime = datetime;
        this._status=status;
    }
     
    // constructor
    public Announcement(String title, String alert, String datetime, String status){
        this._title = title;
        this._alert = alert;
        this._datetime = datetime;
        this._status=status;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
     
    // getting title
    public String getTitle(){
        return this._title;
    }
     
    // setting name
    public void setTitle(String title){
        this._title = title;
    }
     
    // getting alert
    public String getAlert(){
        return this._alert;
    }
     
    // setting alert
    public void setAlert(String alert){
        this._alert = alert;
    }
    
 // getting datetime
    public String getDatetime(){
        return this._datetime;
    }
     
    // setting datetime
    public void setDatetime(String datetime){
        this._datetime = datetime;
    }
    
    // get status
    public String getStatus(){
        return this._status;
    }
     
    // setting status
    public void setStatus(String status){
        this._status = status;
    }
    
}