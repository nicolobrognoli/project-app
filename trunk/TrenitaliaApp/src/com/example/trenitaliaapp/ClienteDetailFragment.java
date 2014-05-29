package com.example.trenitaliaapp;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

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

import com.example.trenitaliaapp.utils.SDCard;
import com.example.trenitaliaapp.utils.User;
import com.example.trenitaliaapp.utils.Utils;

public class ClienteDetailFragment extends Fragment
{
    public static final String ARG_ITEM_ID = "item_id";
    
    private static final String VISO_BITMAP = "viso_bitmap";
    
    private static final String DOCUMENTO_BITMAP = "documento_bitmap";
    
    private static final int FOTO_VISO_REQUEST = 2;
    
    private static final int FOTO_DOCUMENTO_REQUEST = 3;
    
    private static final int GALLERY = 100;
    
    private Button salvaButton_;
    
    private Button modificaButton_;
    
    private Button fotoVisoButton_;
    
    private Button fotoDocumentoButton_;
    
    private EditText nomeText_;
    
    private EditText cognomeText_;
    
    private EditText numeroText_;
    
    private User cliente_;
    
    private boolean fotoVisoAcquisita_ = false;
    
    private boolean fotoDocumentoAcquisita_ = false;
    
    private boolean isInputEnabled_ = true;
    
    private Bitmap bitmapViso_;
    
    private Bitmap bitmapDocumento_;
    
    private DettaglioCallbacks mDettaglioCallbacks = dettaglioCallbacks;
    
    private static DettaglioCallbacks dettaglioCallbacks = new DettaglioCallbacks() {
        
        @Override
        public void onStateChanged()
        {
        }
        
    };
    
    public interface DettaglioCallbacks
    {
        public void onStateChanged();
    }
    
    public ClienteDetailFragment()
    {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID))
        {
            SDCard sdCardUtils = new SDCard();
            Vector<User> clientiList = sdCardUtils.readAllUsers();
            // TODO: controllo più robusto
            cliente_ = clientiList.get(getArguments().getInt(ARG_ITEM_ID));
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_cliente_detail, container, false);
        
        nomeText_ = ((EditText) rootView.findViewById(R.id.edit_text_nome));
        cognomeText_ = ((EditText) rootView.findViewById(R.id.edit_text_cognome));
        numeroText_ = ((EditText) rootView.findViewById(R.id.edit_text_numero));
        
        salvaButton_ = ((Button) rootView.findViewById(R.id.button_salva));
        modificaButton_ = ((Button) rootView.findViewById(R.id.button_modifica));
        
        modificaButton_.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                View parent = (View) v.getParent();
                if (parent != null)
                {
                    if (isInputEnabled_)
                    {
                        String nome = nomeText_.getText().toString();
                        Log.v("Input:", "Nome:" + nome);
                        
                        String cognome = cognomeText_.getText().toString();
                        Log.v("Input:", "Cognome:" + cognome);
                        
                        String numero = numeroText_.getText().toString();
                        Log.v("Input:", "Numero:" + numero);
                        
                        boolean noEmpty = chechEmptyFields(nome, cognome, numero, fotoVisoAcquisita_, fotoDocumentoAcquisita_);
                        
                        AlertDialog.Builder esitoDialog = new AlertDialog.Builder(getActivity());
                        String positiveButtonTitle = getResources().getString(R.string.button_ok);
                        esitoDialog.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1)
                            {
                                // do nothing
                            }
                        });
                        
                        if (noEmpty)
                        {
                            User newUser = new User();
                            newUser.setNome(nome);
                            newUser.setCognome(cognome);
                            newUser.setNumero(numero);
                            
                            SimpleDateFormat formatter = new SimpleDateFormat(Utils.DATE_FORMAT);
                            newUser.setCreation(cliente_.getCreation());
                            newUser.setUpdate(formatter.format(new Date()));
                            
                            boolean createUserFileOk = SDCard.updateUser(cliente_, newUser);
                            boolean moveImagesOk = SDCard.moveTempImages(numero);
                            if (createUserFileOk && moveImagesOk)
                            {
                                esitoDialog.setMessage(getResources().getString(R.string.dialog_ok_text));
                                mDettaglioCallbacks.onStateChanged();
                            }
                            else
                            {
                                esitoDialog.setMessage(getResources().getString(R.string.dialog_ko_text));
                            }
                        }
                        else
                        {
                            esitoDialog.setTitle(getResources().getString(R.string.dialog_vuoti_title));
                            esitoDialog.setMessage(getResources().getString(R.string.dialog_vuoti_text));
                        }
                        esitoDialog.show();
                    }
                    else
                    {
                        modificaButton_.setText(salvaButton_.getText());
                        enableNewInput(true);
                    }
                }
            }
        });
        
        salvaButton_.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                View parent = (View) v.getParent();
                if (parent != null)
                {
                    String nome = nomeText_.getText().toString();
                    Log.v("Input:", "Nome:" + nome);
                    
                    String cognome = cognomeText_.getText().toString();
                    Log.v("Input:", "Cognome:" + cognome);
                    
                    String numero = numeroText_.getText().toString();
                    Log.v("Input:", "Numero:" + numero);
                    
                    boolean noEmpty = chechEmptyFields(nome, cognome, numero, fotoVisoAcquisita_, fotoDocumentoAcquisita_);
                    
                    AlertDialog.Builder esitoDialog = new AlertDialog.Builder(getActivity());
                    String positiveButtonTitle = getResources().getString(R.string.button_ok);
                    esitoDialog.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1)
                        {
                            // do nothing
                        }
                    });
                    
                    if (noEmpty)
                    {
                        User user = new User();
                        user.setNome(nome);
                        user.setCognome(cognome);
                        user.setNumero(numero);
                        
                        SimpleDateFormat formatter = new SimpleDateFormat(Utils.DATE_FORMAT);
                        user.setCreation(formatter.format(new Date()));
                        user.setUpdate(formatter.format(new Date()));
                        
                        boolean createUserFileOk = SDCard.writeToSDFile(user);
                        boolean moveImagesOk = SDCard.moveTempImages(numero);
                        if (createUserFileOk && moveImagesOk)
                        {
                            esitoDialog.setMessage(getResources().getString(R.string.dialog_ok_text));
                            mDettaglioCallbacks.onStateChanged();
                        }
                        else
                        {
                            esitoDialog.setMessage(getResources().getString(R.string.dialog_ko_text));
                            mDettaglioCallbacks.onStateChanged();
                        }
                    }
                    else
                    {
                        esitoDialog.setTitle(getResources().getString(R.string.dialog_vuoti_title));
                        esitoDialog.setMessage(getResources().getString(R.string.dialog_vuoti_text));
                    }
                    esitoDialog.show();
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
        
        if (cliente_ != null)
        {
            nomeText_.setText(cliente_.getNome());
            cognomeText_.setText(cliente_.getCognome());
            numeroText_.setText(cliente_.getNumero()); 
            
            
            salvaButton_.setVisibility(View.GONE);
            modificaButton_.setVisibility(View.VISIBLE);
            enableNewInput(false);
        }
        else
        {
            salvaButton_.setVisibility(View.VISIBLE);
            modificaButton_.setVisibility(View.GONE);
            enableNewInput(true);
            if (savedInstanceState == null)
            	SDCard.resetTempFolder();
        }
        
        return rootView;
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        
        if (cliente_ != null)
        {
        	bitmapViso_ = SDCard.getImage(cliente_, SDCard.VISO);
        	bitmapDocumento_ = SDCard.getImage(cliente_, SDCard.DOCUMENTO);
        }
        else if (savedInstanceState != null)
        {
        	bitmapViso_ = savedInstanceState.getParcelable(VISO_BITMAP);
        	bitmapDocumento_ = savedInstanceState.getParcelable(DOCUMENTO_BITMAP);
        }  
        
        if (bitmapViso_ != null)
        {
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_viso);
            imageView.setImageBitmap(bitmapViso_);
            imageView.setVisibility(View.VISIBLE);
            fotoVisoAcquisita_ = true;
        }
        
        
        if (bitmapDocumento_ != null)
        {
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_documento);
            imageView.setImageBitmap(bitmapDocumento_);
            imageView.setVisibility(View.VISIBLE);
            fotoDocumentoAcquisita_ = true;
        }                
    }
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof DettaglioCallbacks))
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        
        mDettaglioCallbacks = (DettaglioCallbacks) activity;
    }
    
    @Override
    public void onDetach()
    {
        super.onDetach();
        mDettaglioCallbacks = dettaglioCallbacks;
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
            	bitmapViso_ = bitmap;
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_VISO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_viso);
                fotoVisoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            else if (imageType == FOTO_DOCUMENTO_REQUEST)
            {
            	bitmapDocumento_ = bitmap;
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_DOCUMENTO;
                imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_documento);
                fotoDocumentoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            
            String tempPath = root + SDCard.TEMP_IMG_PATH;
            saveTempImage(tempPath, imagePathName, bitmap, imageType == FOTO_VISO_REQUEST);
        }
    }
    
    private void importImage(Intent data, int imageType)
    {
        if (data != null)
        {
            BitmapDrawable bmpDrawable = null;
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
                    
                    saveTempImage(tempPath, imagePathName, bitmap, imageType == FOTO_VISO_REQUEST);
                }
                else
                {
                    bmpDrawable = new BitmapDrawable(getResources(), data.getData().getPath());
                    imageView.setImageDrawable(bmpDrawable);
                    imageView.setVisibility(View.VISIBLE);
                    saveTempImage(tempPath, imagePathName, bmpDrawable.getBitmap(), imageType == FOTO_VISO_REQUEST);
                }
            }
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveTempImage(String tempPath, String imagePathName, Bitmap bitmap, boolean isSalvataggioViso)
    {
        if (isSalvataggioViso)
        {
            fotoVisoAcquisita_ = false;
        }
        else
        {
            fotoDocumentoAcquisita_ = false;
        }
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
            if (isSalvataggioViso)
            {
                fotoVisoAcquisita_ = true;
            }
            else
            {
                fotoDocumentoAcquisita_ = true;
            }
        }
        catch (Exception e)
        {
            Log.e("Errore:", e.toString());
            if (isSalvataggioViso)
            {
                fotoVisoAcquisita_ = false;
            }
            else
            {
                fotoDocumentoAcquisita_ = false;
            }
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
    
    private boolean chechEmptyFields(String nome, String cognome, String numero, boolean fotoViso, boolean fotoDocumento)
    {
        if (fotoViso && fotoDocumento)
        {
            if (nome != null && nome.length() > 0)
            {
                if (cognome != null && cognome.length() > 0)
                {
                    if (numero != null && numero.length() > 0)
                    {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    private void enableNewInput(boolean enable)
    {
        isInputEnabled_ = enable;
        nomeText_.setEnabled(enable);
        cognomeText_.setEnabled(enable);
        numeroText_.setEnabled(enable);
        fotoVisoButton_.setEnabled(enable);
        fotoDocumentoButton_.setEnabled(enable);
    }
    
    @Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putParcelable(VISO_BITMAP, bitmapViso_);
		state.putParcelable(DOCUMENTO_BITMAP, bitmapDocumento_);
	}
}
