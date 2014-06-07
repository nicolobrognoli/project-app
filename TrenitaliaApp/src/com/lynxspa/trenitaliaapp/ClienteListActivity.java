package com.lynxspa.trenitaliaapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lynxspa.trenitaliaapp.utils.SDCard;
import com.lynxspa.trenitaliaapp.utils.ClientAdapter.AdapterCallback;

public class ClienteListActivity extends FragmentActivity implements ClienteListFragment.Callbacks, ClienteDetailFragment.DettaglioCallbacks, AdapterCallback
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_twopane);
        
        if (savedInstanceState == null)
        {
            ClienteListFragment fragmentSx = new ClienteListFragment();
            getFragmentManager().beginTransaction().replace(R.id.cliente_list, fragmentSx).commit();
            
            ClienteDetailFragment fragmentDx = new ClienteDetailFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.cliente_detail_container, fragmentDx).commit();
        }
    }
    
    @Override
    public void onItemSelected(int position)
    {
        Bundle arguments = new Bundle();
        arguments.putInt(ClienteDetailFragment.ARG_ITEM_ID, position);
        ClienteDetailFragment fragment = new ClienteDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.cliente_detail_container, fragment).commit();
    }
    
    @Override
    public void newItemInsert()
    {
        SDCard.refreshFileSystem(this);
        ClienteDetailFragment fragment = new ClienteDetailFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.cliente_detail_container, fragment).commit();
    }
    
    @Override
    public void onStateChanged()
    {
        SDCard.refreshFileSystem(this);
        ClienteListFragment fragmentSx = new ClienteListFragment();
        getFragmentManager().beginTransaction().replace(R.id.cliente_list, fragmentSx).commit();
    }
    
    @Override
    public void onDelete(int position)
    {
        SDCard.refreshFileSystem(this);
        ClienteListFragment fragmentSx = new ClienteListFragment();
        getFragmentManager().beginTransaction().replace(R.id.cliente_list, fragmentSx).commit();
    }
}
