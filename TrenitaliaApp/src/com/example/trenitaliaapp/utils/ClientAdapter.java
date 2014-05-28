package com.example.trenitaliaapp.utils;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.trenitaliaapp.R;

public class ClientAdapter extends ArrayAdapter<User>
{
    public interface AdapterCallback
    {        
        public void onDelete(int position);
    }
    
    private AdapterCallback callback_;
    
    private Context context_;
    
    private Vector<User> userList_;
    
    private int positionDelete_;
    
    public ClientAdapter(Context context, int resource, Vector<User> objects)
    {
        super(context, resource, objects);
        context_ = context;
        if (objects != null)
            userList_ = objects;
        else
            userList_ = new Vector<User>();
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
                AlertDialog.Builder confermaEliminazione = new AlertDialog.Builder(context_);
                confermaEliminazione.setTitle(context_.getResources().getString(R.string.dialog_elimina_title));
                confermaEliminazione.setMessage(context_.getResources().getString(R.string.dialog_elimina_text));
                
                positionDelete_ = (Integer) v.getTag();                
                
                String positiveButtonTitle = context_.getResources().getString(R.string.button_ok);
                confermaEliminazione.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        User user = userList_.get(positionDelete_);
                        SDCard.deleteUser(user);
                        if (callback_ != null)
                            callback_.onDelete(positionDelete_);
                    }
                });
                
                String negativeButtonTitle = context_.getResources().getString(R.string.button_annulla);
                confermaEliminazione.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        // do nothing                        
                    }
                });
                confermaEliminazione.show();              
            }
        });
        
        return convertView;
    }
    
}
