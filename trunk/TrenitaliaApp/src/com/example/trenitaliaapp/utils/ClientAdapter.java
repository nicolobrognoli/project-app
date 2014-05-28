package com.example.trenitaliaapp.utils;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.trenitaliaapp.ClienteDetailFragment.DettaglioCallbacks;
import com.example.trenitaliaapp.R;

public class ClientAdapter extends ArrayAdapter<User>
{
    public interface AdapterCallback
    {
        
        public void onDelete(int position);
    }
    
    private AdapterCallback callback_;
    
    Vector<User> userList_;
    
    public ClientAdapter(Context context, int resource, Vector<User> objects)
    {
        super(context, resource, objects);
        userList_ = objects;
        if (!(context instanceof AdapterCallback))
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        
        callback_ = (AdapterCallback) context;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_row, null);
        
        TextView nomeTextView = (TextView) convertView.findViewById(R.id.textview_nome);
        TextView numeroTextView = (TextView) convertView.findViewById(R.id.textview_numero);
        
        User user = userList_.get(position);
        
        if (user != null)
        {
            nomeTextView.setText(user.getNome() + " " + user.getCognome());
            numeroTextView.setText(user.getNumero());
        }
        
        ImageButton eliminaButton = (ImageButton) convertView.findViewById(R.id.button_elimina);
        eliminaButton.setTag(position);
        eliminaButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                int position = (Integer) v.getTag();
                User user = userList_.get(position);
                SDCard.deleteUser(user);
                if (callback_ != null)
                    callback_.onDelete(position);
            }
        });
        
        return convertView;
    }
    
}
