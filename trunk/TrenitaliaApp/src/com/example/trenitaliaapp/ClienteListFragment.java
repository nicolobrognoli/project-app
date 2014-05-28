package com.example.trenitaliaapp;

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

import com.example.trenitaliaapp.utils.ClientAdapter;
import com.example.trenitaliaapp.utils.SDCard;
import com.example.trenitaliaapp.utils.User;

/**
 * A list fragment representing a list of Clienti. This fragment also supports tablet devices by allowing list items to be given an 'activated' state upon selection. This helps indicate which item is currently being viewed in a {@link ClienteDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks} interface.
 */
public class ClienteListFragment extends Fragment
{
    
    /**
     * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    
    /**
     * The fragment's current callback object, which is notified of list item clicks.
     */
    private Callbacks mCallbacks = callbacks;
    
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    
    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
     */
    public interface Callbacks
    {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
        
        /**
         * Callback for new input
         */
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
        
        ListView list = (ListView) rootView.findViewById(R.id.clienti_list);
        SDCard sdCardUtils = new SDCard();
        Vector<User> userList = sdCardUtils.readAllUsers();
        
        if (userList != null && userList.size() > 0)
        {
            ClientAdapter clientAdapter = new ClientAdapter(getActivity(), R.layout.list_row, userList);
            list.setAdapter(clientAdapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    mCallbacks.onItemSelected(arg2);
                }
            });  
        }     
        
        ImageButton nuovoButton = ((ImageButton) rootView.findViewById(R.id.button_nuovo));
        nuovoButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                mCallbacks.newItemInsert();
            }
        });
        
        return rootView;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
        {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
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
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION)
        {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }
    
    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick)
    {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        // getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }
    
    private void setActivatedPosition(int position)
    {
        // if (position == ListView.INVALID_POSITION)
        // {
        // getListView().setItemChecked(mActivatedPosition, false);
        // }
        // else
        // {
        // getListView().setItemChecked(position, true);
        // }
        //
        // mActivatedPosition = position;
    }
}
