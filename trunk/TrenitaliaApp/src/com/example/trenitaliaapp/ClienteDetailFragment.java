package com.example.trenitaliaapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trenitaliaapp.dummy.DummyContent;
import com.example.trenitaliaapp.utils.Utils;

/**
 * A fragment representing a single Cliente detail screen. This fragment is either contained in a {@link ClienteListActivity} in two-pane mode (on tablets) or a {@link ClienteDetailActivity} on handsets.
 */
public class ClienteDetailFragment extends Fragment
{
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    
    private static final int FOTO_VISO_REQUEST = 2;
    
    private static final int FOTO_DOCUMENTO_REQUEST = 3;
    
    private Button salvaButton_;
    
    private Button fotoVisoButton_;
    
    private Button fotoDocumentoButton_;
    
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
        
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID))
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
        
        Utils.resetTempFolder();
        
        salvaButton_ = ((Button) rootView.findViewById(R.id.button_salva));
        salvaButton_.setOnClickListener(new OnClickListener() {
            
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
        
        fotoVisoButton_ = ((Button) rootView.findViewById(R.id.button_foto_viso));
        fotoVisoButton_.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                String root = Environment.getExternalStorageDirectory().toString() + Utils.APP_PATH;
                
                // Creating folders for Image
                String imageFolderPath = root + Utils.TEMP_IMG_PATH;
                File imagesFolder = new File(imageFolderPath);
                imagesFolder.mkdirs();
                
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, FOTO_VISO_REQUEST);
            }
        });
        
        fotoDocumentoButton_ = ((Button) rootView.findViewById(R.id.button_foto_documento));
        fotoDocumentoButton_.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                String root = Environment.getExternalStorageDirectory().toString() + Utils.APP_PATH;
                
                // Creating folders for Image
                String imageFolderPath = root + Utils.TEMP_IMG_PATH;
                File imagesFolder = new File(imageFolderPath);
                imagesFolder.mkdirs();
                
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, FOTO_DOCUMENTO_REQUEST);
            }
        });
        
        return rootView;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            
            switch (requestCode)
            {
                case FOTO_VISO_REQUEST:
                    this.saveCapturedImage(data, FOTO_VISO_REQUEST);
                    break;
                case FOTO_DOCUMENTO_REQUEST:
                    this.saveCapturedImage(data, FOTO_DOCUMENTO_REQUEST);
                    break;
                
                default:
                    Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    
    private void saveCapturedImage(Intent data, int imageType)
    {
        if (data != null)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ImageView imageView = null;
            String root = Environment.getExternalStorageDirectory().toString() + Utils.APP_PATH;
            String imagePathName = "";
            
            if (imageType == FOTO_VISO_REQUEST)
            {
                imagePathName = root + Utils.TEMP_IMG_PATH + Utils.TEMP_IMG_VISO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_viso);
                fotoVisoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            else if (imageType == FOTO_DOCUMENTO_REQUEST)
            {
                imagePathName = root + Utils.TEMP_IMG_PATH + Utils.TEMP_IMG_DOCUMENTO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_documento);
                fotoDocumentoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            
            try
            {
                FileOutputStream out = new FileOutputStream(imagePathName);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                Log.v("Salvataggio immagine:", "Saved: " + imagePathName);
            }
            catch (FileNotFoundException e)
            {
                Log.e("Errore:", e.toString());
            }
            catch (IOException e)
            {
                Log.e("Errore:", e.toString());
            }
            catch (Exception e)
            {
                Log.e("Errore:", e.toString());
            }
        }
    }
}
