package com.example.trenitaliaapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.trenitaliaapp.dummy.DummyContent;

/**
 * A fragment representing a single Cliente detail screen. This fragment is either contained in a {@link ClienteListActivity} in two-pane mode (on tablets) or a {@link ClienteDetailActivity} on handsets.
 */
public class ClienteDetailFragment extends Fragment
{
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
     */
    public ClienteDetailFragment()
    {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cliente_detail, container, false);
        
        // Show the dummy content as text in a TextView.
        if (mItem != null)
        {
            // ((TextView) rootView.findViewById(R.id.cliente_detail)).setText(mItem.content);
        }
        
        Button salvaButton = ((Button) rootView.findViewById(R.id.button_salva));
        salvaButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                View parent = (View) v.getParent();
                if (parent != null)
                {
                    EditText nomeText = ((EditText) parent.findViewById(R.id.edit_text_nome));
                    String nome = nomeText.getText().toString();
                    Log.v("Input:", "Nome:" + nome);
                    
                    EditText cognomeText = ((EditText) parent.findViewById(R.id.edit_text_cognome));
                    String cognome = cognomeText.getText().toString();
                    Log.v("Input:", "Cognome:" + cognome);
                    
                    EditText numeroText = ((EditText) parent.findViewById(R.id.edit_text_numero));
                    String numero = numeroText.getText().toString();
                    Log.v("Input:", "Numero:" + numero);
                    
                    // TODO: azioni bottone salva
                }
                
            }
        });
        
        return rootView;
    }
}
