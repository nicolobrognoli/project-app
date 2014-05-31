package com.lynxspa.trenitaliaapp;

import java.util.Vector;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.lynxspa.trenitaliaapp.utils.ClientAdapter;
import com.lynxspa.trenitaliaapp.utils.SDCard;
import com.lynxspa.trenitaliaapp.utils.User;

public class ClienteListFragment extends Fragment
{
    
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    
    private int positionSelected_;
    
    private Callbacks mCallbacks = callbacks;
    
    private ListView list_;
    
    private Vector<User> userList_;
    
    public interface Callbacks
    {
        public void onItemSelected(int id);
        
        public void newItemInsert();
    }
    
    private static Callbacks callbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id)
        {
        }
        
        @Override
        public void newItemInsert()
        {
        }
    };
    
    public ClienteListFragment()
    {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.item_list_layout, container, false);
        
        list_ = (ListView) rootView.findViewById(R.id.clienti_list);
        SDCard sdCardUtils = new SDCard();
        userList_ = sdCardUtils.readAllUsers();
        
        if (userList_ != null && userList_.size() > 0)
        {
            ClientAdapter clientAdapter = new ClientAdapter(getActivity(), R.layout.list_row, userList_);
            list_.setAdapter(clientAdapter);
            list_.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    setActivatedPosition(arg2);
                }
            });
        }
        else
        {
            // Client list empty, load input form
            mCallbacks.newItemInsert();
        }
        
        ImageButton nuovoButton = ((ImageButton) rootView.findViewById(R.id.button_nuovo));
        nuovoButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                positionSelected_ = -1;
                mCallbacks.newItemInsert();
            }
        });
        
        if (userList_ != null && userList_.size() > 0)
        {
            
            positionSelected_ = savedInstanceState != null ? savedInstanceState.getInt(STATE_ACTIVATED_POSITION) : 0;
            setActivatedPosition(positionSelected_);
            
        }
        
        return rootView;
    }
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks))
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        
        mCallbacks = (Callbacks) activity;
    }
    
    @Override
    public void onDetach()
    {
        super.onDetach();
        
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = callbacks;
    }
    
    public void setActivateOnItemClick(boolean activateOnItemClick)
    {
        list_.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }
    
    private void setActivatedPosition(int position)
    {
        if (position != ListView.INVALID_POSITION)
        {
            positionSelected_ = position;
            mCallbacks.onItemSelected(position);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putInt(STATE_ACTIVATED_POSITION, positionSelected_);
    }
}
