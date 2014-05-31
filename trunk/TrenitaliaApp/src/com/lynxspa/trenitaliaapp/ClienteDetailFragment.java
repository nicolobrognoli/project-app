package com.lynxspa.trenitaliaapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.lynxspa.trenitaliaapp.R;
import com.lynxspa.trenitaliaapp.utils.SDCard;
import com.lynxspa.trenitaliaapp.utils.User;
import com.lynxspa.trenitaliaapp.utils.Utils;

public class ClienteDetailFragment extends Fragment
{
    public static final String ARG_ITEM_ID = "item_id";
    
    private static final String VISO_BITMAP = "viso_bitmap";
    
    private static final String DOCUMENTO_BITMAP = "documento_bitmap";
    
    private static final int FOTO_VISO_REQUEST = 2;
    
    private static final int FOTO_DOCUMENTO_REQUEST = 3;
    
    private static final int GALLERY = 100;
    
    private Uri intentData_;
    
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
    
    private ProgressDialog dialogAttesa_;
    
    private boolean loadingViso_ = false;
    
    private boolean loadingDocumento_ = false;
    
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
                            
                            String createUserFileOk = SDCard.updateUser(cliente_, newUser);
                            SDCard.moveTempImages(numero);
                            if (createUserFileOk.equalsIgnoreCase(SDCard.SUCCESS))
                            {
                                esitoDialog.setMessage(getResources().getString(R.string.dialog_ok_text));
                                mDettaglioCallbacks.onStateChanged();
                            }
                            else if (createUserFileOk.equalsIgnoreCase(SDCard.DIR_ESISTENTE))
                            {
                                esitoDialog.setMessage(getResources().getString(R.string.dialog_dir_exist_text));
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
                        
                        String createUserFileOk = SDCard.writeToSDFile(user, false);
                        boolean moveImagesOk = false;
                        if (createUserFileOk.equalsIgnoreCase(SDCard.SUCCESS))
                            moveImagesOk = SDCard.moveTempImages(numero);
                        if (createUserFileOk.equalsIgnoreCase(SDCard.SUCCESS) && moveImagesOk)
                        {
                            esitoDialog.setMessage(getResources().getString(R.string.dialog_ok_text));
                            mDettaglioCallbacks.onStateChanged();
                        }
                        else if (createUserFileOk.equalsIgnoreCase(SDCard.DIR_ESISTENTE))
                        {
                            esitoDialog.setMessage(getResources().getString(R.string.dialog_dir_exist_text));
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
            SDCard.resetTempFolder();
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
        
        if (savedInstanceState != null)
        {
            bitmapViso_ = savedInstanceState.getParcelable(VISO_BITMAP);
            bitmapDocumento_ = savedInstanceState.getParcelable(DOCUMENTO_BITMAP);
            setupImagePreview(VISO_BITMAP);
            setupImagePreview(DOCUMENTO_BITMAP);
        }
        else if (cliente_ != null)
        {
            
            dialogAttesa_ = ProgressDialog.show(getActivity(), null, "Caricamento", true);
            loadingDocumento_ = loadingViso_ = true;
            new LoadImagePreviewFromGallery().execute(VISO_BITMAP);
            
            new LoadImagePreviewFromGallery().execute(DOCUMENTO_BITMAP);
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
    
    private void setupImagePreview(String imageType)
    {
        if (imageType == VISO_BITMAP && bitmapViso_ != null)
        {
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_viso);
            setThumbImageView(imageView, bitmapViso_);
            imageView.setVisibility(View.VISIBLE);
            fotoVisoAcquisita_ = true;
        }
        
        if (imageType == DOCUMENTO_BITMAP && bitmapDocumento_ != null)
        {
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageview_foto_documento);
            setThumbImageView(imageView, bitmapDocumento_);
            imageView.setVisibility(View.VISIBLE);
            fotoDocumentoAcquisita_ = true;
        }
    }
    
    private void saveCapturedImage(Intent data, int imageType)
    {
        String type = "";
        if (imageType == FOTO_VISO_REQUEST)
        {
            type = VISO_BITMAP;
        }
        else if (imageType == FOTO_DOCUMENTO_REQUEST)
        {
            type = DOCUMENTO_BITMAP;
        }
        dialogAttesa_ = ProgressDialog.show(getActivity(), null, "Caricamento", true);
        new LoadImagePreviewFromTemp().execute(type);
        
    }
    
    private void importImage(Intent data, int imageType)
    {
        if (data != null)
        {
            intentData_ = data.getData();
            String type = "";
            if (imageType == FOTO_VISO_REQUEST)
            {
                type = VISO_BITMAP;
            }
            else if (imageType == FOTO_DOCUMENTO_REQUEST)
            {
                type = DOCUMENTO_BITMAP;
            }
            dialogAttesa_ = ProgressDialog.show(getActivity(), null, "Caricamento", true);
            new ImportImageFromGallery().execute(type);
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Cancellato", Toast.LENGTH_SHORT).show();
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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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
                String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + SDCard.APPFOLDER;
                String tempPath = "";
                if (requestCode == FOTO_VISO_REQUEST)
                    tempPath = root + SDCard.TEMP_IMG_PATH + "/" + SDCard.TEMP_IMG_VISO;
                else if (requestCode == FOTO_DOCUMENTO_REQUEST)
                    tempPath = root + SDCard.TEMP_IMG_PATH + "/" + SDCard.TEMP_IMG_DOCUMENTO;
                File file = new File(tempPath);
                Uri tempUri = Uri.fromFile(file);
                pictureActionIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, tempUri);
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
        if (enable)
        {
            fotoVisoButton_.setTextColor(getResources().getColor(R.color.ti_deep_green));
            fotoDocumentoButton_.setTextColor(getResources().getColor(R.color.ti_deep_green));
        }
        else
        {
            fotoVisoButton_.setTextColor(getResources().getColor(R.color.ti_light_gray));
            fotoDocumentoButton_.setTextColor(getResources().getColor(R.color.ti_light_gray));
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle state)
    {
        super.onSaveInstanceState(state);
        state.putParcelable(VISO_BITMAP, bitmapViso_);
        state.putParcelable(DOCUMENTO_BITMAP, bitmapDocumento_);
    }
    
    private class LoadImagePreviewFromGallery extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            
            String imageType = params[0];
            if (imageType == VISO_BITMAP)
            {
                bitmapViso_ = SDCard.getPreviewImage(cliente_, SDCard.VISO);
            }
            
            if (imageType == DOCUMENTO_BITMAP)
            {
                bitmapDocumento_ = SDCard.getPreviewImage(cliente_, SDCard.DOCUMENTO);
            }
            
            return imageType;
        }
        
        @Override
        protected void onPostExecute(String result)
        {
            if (result == VISO_BITMAP)
            {
                loadingViso_ = false;
            }
            
            if (result == DOCUMENTO_BITMAP)
            {
                loadingDocumento_ = false;
            }
            if (dialogAttesa_ != null && !loadingDocumento_ && !loadingViso_)
                dialogAttesa_.dismiss();
            setupImagePreview(result);
        }
        
        @Override
        protected void onPreExecute()
        {
        }
        
        @Override
        protected void onProgressUpdate(Void... values)
        {
        }
    }
    
    private class LoadImagePreviewFromTemp extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            
            String imageType = params[0];
            if (imageType == VISO_BITMAP)
            {
                bitmapViso_ = SDCard.getPreviewImageTemp(SDCard.VISO);
            }
            
            if (imageType == DOCUMENTO_BITMAP)
            {
                bitmapDocumento_ = SDCard.getPreviewImageTemp(SDCard.DOCUMENTO);
            }
            
            return imageType;
        }
        
        @Override
        protected void onPostExecute(String result)
        {
            if (result == VISO_BITMAP)
            {
                loadingViso_ = false;
            }
            
            if (result == DOCUMENTO_BITMAP)
            {
                loadingDocumento_ = false;
            }
            if (dialogAttesa_ != null && !loadingDocumento_ && !loadingViso_)
                dialogAttesa_.dismiss();
            setupImagePreview(result);
        }
        
        @Override
        protected void onPreExecute()
        {
        }
        
        @Override
        protected void onProgressUpdate(Void... values)
        {
        }
    }
    
    private class ImportImageFromGallery extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            
            String imageType = params[0];
            BitmapDrawable bmpDrawable = null;
            
            if (intentData_ != null)
            {
                Cursor cursor = getActivity().getContentResolver().query(intentData_, null, null, null, null);
                if (cursor != null)
                {
                    cursor.moveToFirst();
                    
                    int idx = cursor.getColumnIndex(ImageColumns.DATA);
                    String fileSrc = cursor.getString(idx);
                    Bitmap bitmap = BitmapFactory.decodeFile(fileSrc);
                    
                    // TODO
                    if (imageType == VISO_BITMAP)
                    {
                        bitmapViso_ = bitmap;
                    }
                    else if (imageType == DOCUMENTO_BITMAP)
                    {
                        bitmapDocumento_ = bitmap;
                    }
                }
                else
                {
                    bmpDrawable = new BitmapDrawable(getResources(), intentData_.getPath());
                    // TODO
                    if (imageType == VISO_BITMAP)
                    {
                        bitmapViso_ = bmpDrawable.getBitmap();
                    }
                    else if (imageType == DOCUMENTO_BITMAP)
                    {
                        bitmapDocumento_ = bmpDrawable.getBitmap();
                    }
                }
            }
            
            return imageType;
        }
        
        @Override
        protected void onPostExecute(String result)
        {
            if (result == VISO_BITMAP)
            {
                loadingViso_ = false;
            }
            
            if (result == DOCUMENTO_BITMAP)
            {
                loadingDocumento_ = false;
            }
            
            if (result == VISO_BITMAP)
            {
                fotoVisoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            else if (result == DOCUMENTO_BITMAP)
            {
                fotoDocumentoButton_.setText(getResources().getString(R.string.button_foto_modifica));
            }
            new SaveTempImageFromGallery().execute(result);
            setupImagePreview(result);
        }
        
        @Override
        protected void onPreExecute()
        {
        }
        
        @Override
        protected void onProgressUpdate(Void... values)
        {
        }
    }
    
    private class SaveTempImageFromGallery extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            
            String imageType = params[0];
            String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + SDCard.APPFOLDER + "/";
            String tempPath = root + SDCard.TEMP_IMG_PATH;
            String imagePathName = "";
            Bitmap bitmap = null;
            
            if (imageType == VISO_BITMAP)
            {
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_VISO;
                bitmap = bitmapViso_;
                loadingViso_ = false;
            }
            else if (imageType == DOCUMENTO_BITMAP)
            {
                imagePathName = root + SDCard.TEMP_IMG_PATH + SDCard.TEMP_IMG_DOCUMENTO;
                bitmap = bitmapDocumento_;
                loadingDocumento_ = false;
            }
            saveTempImage(tempPath, imagePathName, bitmap, imageType == VISO_BITMAP);
            if (dialogAttesa_ != null && !loadingDocumento_ && !loadingViso_)
                dialogAttesa_.dismiss();
            
            return imageType;
        }
        
        @Override
        protected void onPostExecute(String result)
        {
            if (result == VISO_BITMAP)
            {
                loadingViso_ = false;
            }
            
            if (result == DOCUMENTO_BITMAP)
            {
                loadingDocumento_ = false;
            }
            if (dialogAttesa_ != null && !loadingDocumento_ && !loadingViso_)
                dialogAttesa_.dismiss();
        }
        
        @Override
        protected void onPreExecute()
        {
        }
        
        @Override
        protected void onProgressUpdate(Void... values)
        {
        }
    }
    
    private void setThumbImageView(ImageView imageview, Bitmap bitmap)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, os);
        
        byte[] array = os.toByteArray();
        
        imageview.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
        imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }
    
}
