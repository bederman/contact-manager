
package com.example.android.contactmanager;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;


public class ContactEntity extends GenericJson {
    @Key("_id")
    private String id; 
	@Key("name")
	private String name;
    private static class Email extends GenericJson{
    	@Key("email")
    	private String email;
    	@Key("email_type")
    	private String email_type;
    	
    	public Email(){}
    }
    private static class Phone extends GenericJson{
    	@Key("phone")
    	private String phone;
    	@Key("phone_type")
    	private String phone_type;
    	
    	public Phone(){}
    }
    @Key("_kmd")
    private KinveyMetaData meta; // Kinvey metadata, OPTIONAL
    @Key("_acl")
    private KinveyMetaData.AccessControlList acl; //Kinvey access control, OPTIONAL
    public ContactEntity(){}  //GenericJson classes must have a public empty constructor
    
    public void setName(String newName){
    	name = newName;
    }
    public String getName(){
    	return name;
    }
}