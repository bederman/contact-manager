/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.contactmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;

public final class ContactManager extends Activity
{

    public static final String TAG = "ContactManager";
    
//    private String appKey = "kid_Teem0SkMvq";
//    private String appSecret = "86e8fdc2d72547fb979736a0d25daa89";

    private Button mAddAccountButton;
    private ListView mContactList;
    private ArrayList<String> contactnames;

    /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	
    	Log.v(TAG, "Activity State: onCreate()");
    	super.onCreate(savedInstanceState);
    	
//    	final Client mKinveyClient = new Client.Builder(appKey, appSecret, this.getApplicationContext()).build();
    	final Client mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
    	
    	//ping
    	mKinveyClient.ping(new KinveyPingCallback() {
    	    public void onFailure(Throwable t) {
    	        Log.e(TAG, "Kinvey Ping Failed", t);
    	        CharSequence text = "Ping failed";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    	    }
    	    public void onSuccess(Boolean b) {
    	        Log.d(TAG, "Kinvey Ping Success");
    	        CharSequence text = "Ping succeeded";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    	    }
    	});
    	
        setContentView(R.layout.contact_manager);

        // Obtain handles to UI objects
        mAddAccountButton = (Button) findViewById(R.id.addContactButton);
        mContactList = (ListView) findViewById(R.id.contactList);


        // Register handler for UI elements
        mAddAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "mAddAccountButton clicked");
                launchContactAdder();
            }
        });
        
        
        
        //logout any existing/leftover user
        mKinveyClient.user().logout().execute();
        
        //dummy user to test
        mKinveyClient.user().create("duchess", "guest", new KinveyUserCallback() {
            public void onFailure(Throwable t) {
//                CharSequence text = "Could not sign up";
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
            public void onSuccess(User u) {
//                CharSequence text = u.getUsername() + ", your account has been created";
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
        
        
        //logging in user
        if (!mKinveyClient.user().isUserLoggedIn()){
        	mKinveyClient.user().login("duchess", "guest", new KinveyUserCallback() {
	            @Override
	            public void onFailure(Throwable error) {
	                Log.e(TAG, "Login Failure", error);
	                CharSequence text = "login failed";
	                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	            }
	            @Override
	            public void onSuccess(User result) {
	                Log.i(TAG,"Logged in a new implicit user with id: " + result.getId());
	                CharSequence text = "Welcome back, " + result.getUsername() + ".";
	                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	            }
	        });
        }        

        // Populate the contact list
        populateContactList(mKinveyClient);
    }

    /**
     * Populate the contact list based on account currently selected in the account spinner.
     */
    private void populateContactList(Client mKinveyClient) {
    	AsyncAppData<ContactEntity> myContacts = mKinveyClient.appData("contact", ContactEntity.class);
    	myContacts.get(new KinveyListCallback<ContactEntity>()     {
    		  @Override
    		  public void onSuccess(ContactEntity[] result) { 
    		    Log.v(TAG, "received "+ result.length + " contacts");
    		    //add names to the list
    		    contactnames = new ArrayList<String>();
    		    for(ContactEntity entity : result){
    		    	contactnames.add((String) entity.get("name"));
    		    }
    		    //display list
    		    ArrayAdapter adapter = new ArrayAdapter(ContactManager.this, android.R.layout.simple_list_item_1,
    	                contactnames.toArray());
    		    mContactList.setAdapter(adapter);
    		    
    		  }
    		  @Override
    		  public void onFailure(Throwable error)  { 
    		    Log.e(TAG, "failed to fetch all", error);
    		  }
    		});
    }

    public void onResume() {
    	super.onResume();
    	final Client mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
    	populateContactList(mKinveyClient);
    }
   
    /**
     * Launches the ContactAdder activity to add a new contact to the selected accont.
     */
    protected void launchContactAdder() {
        Intent i = new Intent(this, ContactAdder.class);
        startActivity(i);
    }
}
