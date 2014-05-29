package com.example.trenitaliaapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class SDCard
{
    
    public static final String APPFOLDER = "Trenitalia";
    
    public static final int VISO = 0;
    
    public static final int DOCUMENTO = 1;
    
    private static final String USERTXT = "user.txt";
    
    private static final String USERXML = "user.xml";
    
    private static final String VISO_FILENAME = "viso.png";
    
    private static final String DOCUMENTO_FILENAME = "documento.png";
    
    private static final String NOME = "Nome";
    
    private static final String COGNOME = "Cognome";
    
    private static final String NUMERO = "Numero";
    
    private static final String CREATION = "Creazione";
    
    private static final String UPDATE = "Aggiornamento";
    
    private static final String TAG = "TRENITALIA/SDCARD";
    
    public static final String TEMP_IMG_PATH = "/temp_images";
    
    public static final String TEMP_IMG_VISO = "/viso_temp.png";
    
    public static final String TEMP_IMG_DOCUMENTO = "/documento_temp.png";
    
    public static final String IMG_VISO = "/viso.png";
    
    public static final String IMG_DOCUMENTO = "/documento.png";
    
    public static boolean writeToSDFile(User user)
    {
        
        File root = android.os.Environment.getExternalStorageDirectory();
        Log.v(TAG, "\nExternal file system root: " + root);
        
        File dir = new File(root.getAbsolutePath() + "/" + APPFOLDER + "/" + user.getNumero());
        if (!dir.exists())
        {
            Log.v(TAG, "User dir doesn't exist! creating " + root.getAbsolutePath() + "/" + APPFOLDER + "/" + user.getNumero());
            dir.mkdirs();
        }
        
        File filetxt = new File(dir, USERTXT);
        
        try
        {
            FileOutputStream f = new FileOutputStream(filetxt);
            PrintWriter pw = new PrintWriter(f);
            
            pw.print(getSimpleText(user));
            pw.flush();
            pw.close();
            f.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        
        // XML user file
        File filexml = new File(dir, USERXML);
        
        try
        {
            FileOutputStream f = new FileOutputStream(filexml);
            PrintWriter pw = new PrintWriter(f);
            
            pw.println(getXMLElement(user));
            pw.flush();
            pw.close();
            f.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    private static String getSimpleText(User user)
    {
        String simpleText = NOME + ": " + user.getNome() + "\n" + COGNOME + ": " + user.getCognome() + "\n" + NUMERO + ": " + user.getNumero() + "\n" + CREATION + ": " + user.getCreation() + "\n" + UPDATE + ": " + user.getUpdate() + "\n";
        Log.v(TAG, "Simple text: " + simpleText);
        return simpleText;
        
    }
    
    private static String getXMLElement(User user)
    {
        String element = "<user>\n\t<" + NOME + ">" + user.getNome() + "</" + NOME + ">\n\t<" + COGNOME + ">" + user.getCognome() + "</" + COGNOME + ">\n\t<" + NUMERO + ">" + user.getNumero() + "</" + NUMERO + ">\n\t<" + CREATION + ">" + user.getCreation() + "</" + CREATION + ">\n\t<" + UPDATE + ">" + user.getUpdate() + "</" + UPDATE + ">\n</user>";
        Log.v(TAG, "XML element: " + element);
        return element;
    }
    
    public Vector<User> readAllUsers()
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/" + APPFOLDER);
        
        if (!dir.exists())
        {
            Log.v(TAG, "APP dir doesn't exist!");
            return null;
        }
        
        File[] list = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name)
            {
                return !name.startsWith(".");
            }
        });
        if (list.length == 0)
        {
            Log.v(TAG, "APP dir is empty!");
            return null;
        }
        
        Vector<User> users = new Vector<User>();
        
        for (File f : list)
        {
            
            File userFile = new File(f.getAbsoluteFile() + "/" + USERXML);
            Log.d(TAG, "reading file " + userFile.getAbsolutePath());
            User user = readSingleXMLFile(userFile);
            if (user != null)
            {
                Log.d(TAG, "ho letto l'utente numero: " + user.getNumero());
                users.add(user);
            }
        }
        
        return users;
    }
    
    private User readSingleXMLFile(File file)
    {
        User user = new User();
        try
        {
            InputStream is = new FileInputStream(file.getPath());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(is));
            NodeList nodeList = doc.getElementsByTagName("user");
            
            Node node = nodeList.item(0);
            Element userElement = (Element) node;
            
            user.setNome(getValue(userElement, NOME));
            user.setCognome(getValue(userElement, COGNOME));
            user.setNumero(getValue(userElement, NUMERO));
            user.setCreation(getValue(userElement, CREATION));
            user.setUpdate(getValue(userElement, CREATION));
            
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return null;
        }
        
        return user;
        
    }
    
    private String getValue(Element item, String name)
    {
        NodeList nodes = item.getElementsByTagName(name);
        return this.getTextNodeValue(nodes.item(0));
    }
    
    private final String getTextNodeValue(Node node)
    {
        Node child;
        if (node != null)
        {
            if (node.hasChildNodes())
            {
                child = node.getFirstChild();
                while (child != null)
                {
                    if (child.getNodeType() == Node.TEXT_NODE)
                    {
                        return child.getNodeValue();
                    }
                    child = child.getNextSibling();
                }
            }
        }
        return "";
    }
    
    public static boolean deleteUser(User user)
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        
        File oldDir = new File(root.getAbsolutePath() + "/" + APPFOLDER + "/" + user.getNumero());
        
        // non cancella la directory dell'utente ma la rinomina comina come directory nascosta per consentire un ripristino.
        File backupDir = new File(root.getAbsolutePath() + "/" + APPFOLDER + "/." + user.getNumero());
        boolean success = oldDir.renameTo(backupDir);
        return success;
    }
    
    public static boolean moveTempImages(String number)
    {
        String srcPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + APPFOLDER + TEMP_IMG_PATH;
        String destPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + APPFOLDER + "/" + number;
        
        File visoTempImg = new File(srcPath + TEMP_IMG_VISO);
        File visoDestImg = new File(destPath + IMG_VISO);
        
        boolean visoCopyOk = SDCard.copy(visoTempImg, visoDestImg);
        
        File documentoTempImg = new File(srcPath + TEMP_IMG_DOCUMENTO);
        File documentoDestImg = new File(destPath + IMG_DOCUMENTO);
        boolean documentoCopyOk = SDCard.copy(documentoTempImg, documentoDestImg);
        
        return visoCopyOk && documentoCopyOk;
    }
    
    private static boolean copy(File src, File dst)
    {
        InputStream in = null;
        OutputStream out = null;
        try
        {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            try
            {
                in.close();
                out.close();
            }
            catch (Exception e2)
            {
                return false;
            }
        }
        return true;
    }
    
    public static void resetTempFolder()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + APPFOLDER + TEMP_IMG_PATH;
        try
        {
            File visoImg = new File(path + TEMP_IMG_VISO);
            if (visoImg.delete())
            {
                Log.v("Delete", visoImg.getName() + " is deleted!");
            }
            else
            {
                Log.v("Delete", "Delete operation is failed.");
            }
            File documentiImg = new File(path + TEMP_IMG_DOCUMENTO);
            if (documentiImg.delete())
            {
                Log.v("Delete", documentiImg.getName() + " is deleted!");
            }
            else
            {
                Log.v("Delete", "Delete operation is failed.");
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static boolean updateUser(User oldUser, User newUser)
    {
        if (!oldUser.getNumero().equalsIgnoreCase(newUser.getNumero()))
        { // ho cambiato codice, rinomino la dir
            File root = android.os.Environment.getExternalStorageDirectory();
            
            File oldDir = new File(root.getAbsolutePath() + "/" + APPFOLDER + "/" + oldUser.getNumero());
            File newDir = new File(root.getAbsolutePath() + "/" + APPFOLDER + "/" + newUser.getNumero());
            oldDir.renameTo(newDir);
        }
        
        return writeToSDFile(newUser);        
    }
    
    public static Bitmap getImage(User user, int tipo)
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        
        String fileName = root.getAbsolutePath() + "/" + APPFOLDER + "/" + user.getNumero() + "/";
        
        if (tipo == VISO)
        {
            fileName = fileName + VISO_FILENAME;
        }
        else if (tipo == DOCUMENTO)
        {
            fileName = fileName + DOCUMENTO_FILENAME;
        }
        
        Bitmap bitmap;
        
        try
        {
            bitmap = BitmapFactory.decodeFile(fileName);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return null;
        }
        return bitmap;
    }
    
    public static Bitmap getPreviewImage(User user, int tipo)
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        
        String fileName = root.getAbsolutePath() + "/" + APPFOLDER + "/" + user.getNumero() + "/";
        
        if (tipo == VISO)
        {
            fileName = fileName + VISO_FILENAME;
        }
        else if (tipo == DOCUMENTO)
        {
            fileName = fileName + DOCUMENTO_FILENAME;
        }
        
        Bitmap bitmap;
        
        try
        {
            
            
         // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 50, 50);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(fileName, options);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return null;
        }
        return bitmap;
    }
    
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
    
}