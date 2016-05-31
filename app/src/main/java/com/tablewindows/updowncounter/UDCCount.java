package com.tablewindows.updowncounter;
import java.sql.Date;

public class UDCCount {


	   
	    int _id = 0;
	    String _name;
	    Long _ctrDate;
	    int _ctrCount;
	   
	    public UDCCount(){
	        this._id =0;
	        this._name = "";
	        this._ctrDate = (long) 0;
			this._ctrCount  = 0;  
	    }
	   
	    public UDCCount(int id, String name, int ctrDate, int ctrCount){
	        this._id = id;
	        this._name = name;
	        this._ctrDate = (long) ctrDate;
			this._ctrCount  = ctrCount;
	    }

	    public UDCCount(int id, String name){
	        this._id = id;
	        this._name = name;
			this._ctrCount  = 0;
	    }
	   
	    public UDCCount(String name){
	        this._id = 0;
	        this._name = name;
			this._ctrCount  = 0;
	    }




		
	    public int getID(){
	        return this._id;
	    }
	     
	   
	    public void setID(int id){
	        this._id = id;
	    }
	     
	   
	    public String getName(){
	        return this._name;
	    }
	     
	   
	    public void setName(String name){
	        this._name = name;
	    }
	     
	   
	    public Date getDate() {
	        return new Date(this._ctrDate);
	    }
	     
	   
	    public void setDate(Long date){
	        this._ctrDate = date;
	    }

		
	    public Integer getctrCount(){
	        return this._ctrCount;
	    }
	     
	   
	    public void setCount(int ctrCount){
	        this._ctrCount = ctrCount;
	    }
	    
	
	
	
}
