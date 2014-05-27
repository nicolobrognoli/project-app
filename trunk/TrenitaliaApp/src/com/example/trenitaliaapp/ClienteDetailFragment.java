package com.example.trenitaliaapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
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
import com.example.trenitaliaapp.utils.SDCard;
import com.example.trenitaliaapp.utils.User;
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
    
    private static final int GALLERY = 100;
    
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
        
        SDCard.resetTempFolder();
        
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
                    
                    boolean noEmpty = chechEmptyFields();
                    
                    if (noEmpty)
                    {
                        User user = new User();
                        user.setNome(nome);
                        user.setCognome(cognome);
                        user.setNumero(numero);
                        
                        SimpleDateFormat formatter = new SimpleDateFormat(Utils.DATE_FORMAT);
                        user.setCreation(formatter.format(new Date()));
                        
                        SDCard.writeToSDFile(user);
                        SDCard.moveTempImages(numero);
                    }
                }
            }
        });
        
        fotoVisoButton_ = ((Button) rootView.findViewById(R.id.button_foto_viso));
        fotoVisoButton_.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                String root = Environment.getExternalStorageDirectory().toString() + SDCard.APPFOLDER;
                
                // Creating folders for Image
                String imageFolderPath = root + SDCard.TEMP_IMG_PATH;
                File imagesFolder = new File(imageFolderPath);
                imagesFolder.mkdirs();
                
                startDialog(FOTO_VISO_REQUEST);
            }
        });
        
        fotoDocumentoButton_ = ((Button) rootView.findViewById(R.id.button_foto_documento));
        fotoDocumentoButton_.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                String root = Environment.getExternalStorageDirectory().toString() + SDCard.APPFOLDER;
                
                // Creating folders for Image
                String imageFolderPath = root + SDCard.TEMP_IMG_PATH;
                File imagesFolder = new File(imageFolderPath);
                imagesFolder.mkdirs();
                
                startDialog(FOTO_DOCUMENTO_REQUEST);
            }
        });
        
        return rootView;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            int code = requestCode - GALLERY;
            if (code > 0)
            {
                // Acquisizione da galleria
                switch (code)
                {
                    case FOTO_VISO_REQUEST:
                        this.importImage(data, FOTO_VISO_REQUEST);
                        break;
                    case FOTO_DOCUMENTO_REQUEST:
                        this.importImage(data, FOTO_DOCUMENTO_REQUEST);
                        break;
                    
                    default:
                        Toast.makeText(getActivity(), "Errore acquisizione immagine.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            else
            {
                // Acquisizione da fotocamera
                switch (requestCode)
                {
                    case FOTO_VISO_REQUEST:
                        this.saveCapturedImage(data, FOTO_VISO_REQUEST);
                        break;
                    case FOTO_DOCUMENTO_REQUEST:
                        this.saveCapturedImage(data, FOTO_DOCUMENTO_REQUEST);
                        break;
                    
                    default:
                        Toast.makeText(getActivity(), "Errore acquisizione immagine.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            
        }
    }
    
    private void saveCapturedImage(Intent data, int imageType)
    {
        if (data != null)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ImageView imageView = null;
            String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + SDCard.APPFOLDER + "/";
            String imagePathName = "";
            
            if (imageType == FOTO_VISO_REQUEST)
            {
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_VISO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_viso);
                fotoVisoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            else if (imageType == FOTO_DOCUMENTO_REQUEST)
            {
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_DOCUMENTO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_documento);
                fotoDocumentoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            
            String tempPath = root + SDCard.TEMP_IMG_PATH;
            saveTempImage(tempPath, imagePathName, bitmap);
        }
    }
    
    private void importImage(Intent data, int imageType)
    {
        if (data != null)
        {
            BitmapDrawable bmpDrawable = null;
            ImageView imageView = null;
            String root = Environment.getExternalStorageDirectory().toString() + SDCard.APPFOLDER;
            String imagePathName = "";
            
            if (imageType == FOTO_VISO_REQUEST)
            {
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_VISO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_viso);
                fotoVisoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            else if (imageType == FOTO_DOCUMENTO_REQUEST)
            {
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_DOCUMENTO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_documento);
                fotoDocumentoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            
            if (data != null && data.getData() != null)
            {
                Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
                String tempPath = root + SDCard.TEMP_IMG_PATH;
                if (cursor != null)
                {
                    cursor.moveToFirst();
                    
                    int idx = cursor.getColumnIndex(ImageColumns.DATA);
                    String fileSrc = cursor.getString(idx);
                    Bitmap bitmap = BitmapFactory.decodeFile(fileSrc);
                    
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    
                    saveTempImage(tempPath, imagePathName, bitmap);
                }
                else
                {
                    bmpDrawable = new BitmapDrawable(getResources(), data.getData().getPath());
                    imageView.setImageDrawable(bmpDrawable);
                    imageView.setVisibility(View.VISIBLE);
                    saveTempImage(tempPath, imagePathName, bmpDrawable.getBitmap());
                }
            }
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveTempImage(String tempPath, String imagePathName, Bitmap bitmap)
    {
        try
        {
            File dir = new File(tempPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            
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
    
    private void startDialog(final int requestCode)
    {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle(getResources().getString(R.string.dialog_title));
        myAlertDialog.setMessage(getResources().getString(R.string.dialog_text));
        
        String positiveButtonTitle = getResources().getString(R.string.button_galleria);
        myAlertDialog.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1)
            {
                Intent pictureActionIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
                pictureActionIntent.setType("image/*");
                pictureActionIntent.putExtra("return-data", true);
                startActivityForResult(pictureActionIntent, requestCode + GALLERY);
            }
        });
        
        String negativeButtonTitle = getResources().getString(R.string.button_camera);
        myAlertDialog.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1)
            {
                Intent pictureActionIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pictureActionIntent, requestCode);
                
            }
        });
        myAlertDialog.show();
    }
    
    private boolean chechEmptyFields()
    {
        return true;
    }
}
