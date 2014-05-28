package com.example.trenitaliaapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * An activity representing a list of Clienti. This activity has different presentations for handset and tablet-size devices. On handsets, the activity presents a list of items, which when touched, lead to a {@link ClienteDetailActivity} representing item details. On tablets, the activity presents the list of items and item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a {@link ClienteListFragment} and the item details (if present) is a {@link ClienteDetailFragment}.
 * <p>
 * This activity also implements the required {@link ClienteListFragment.Callbacks} interface to listen for item selections.
 */
public class ClienteListActivity extends FragmentActivity implements ClienteListFragment.Callbacks, ClienteDetailFragment.DettaglioCallbacks
{
    
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_twopane);      
        
        ClienteListFragment fragmentSx = new ClienteListFragment();
        getFragmentManager().beginTransaction().replace(R.id.cliente_list, fragmentSx).commit();
        
        ClienteDetailFragment fragmentDx = new ClienteDetailFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.cliente_detail_container, fragmentDx).commit();
        
        mTwoPane = true;
    }
    
    /**
     * Callback method from {@link ClienteListFragment.Callbacks} indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int position)
    {
        if (mTwoPane)
        {
            Bundle arguments = new Bundle();
            arguments.putInt(ClienteDetailFragment.ARG_ITEM_ID, position);
            ClienteDetailFragment fragment = new ClienteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.cliente_detail_container, fragment).commit();
        }
    }
    
    @Override
    public void newItemInsert()
    {
        ClienteDetailFragment fragment = new ClienteDetailFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.cliente_detail_container, fragment).commit();
    }
    
    @Override
    public void onStateChanged()
    {
        ClienteListFragment fragmentSx = new ClienteListFragment();
        getFragmentManager().beginTransaction().replace(R.id.cliente_list, fragmentSx).commit();
    }

}
