package com.example.trenitaliaapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import android.os.Environment;
import android.util.Log;

public class SDCard
{
    
    public static final String APPFOLDER = "Trenitalia";
    
    private static final String USERTXT = "user.txt";
    
    private static final String USERXML = "user.xml";
    
    private static final String NOME = "Nome";
    
    private static final String COGNOME = "Cognome";
    
    private static final String NUMERO = "Numero";
    
    private static final String CREATION = "Creazione";
    
    private static final String UPDATE = "Aggiornamento";
    
    private static final String TAG = "MEDIA";
    
    public static final String TEMP_IMG_PATH = "/temp_images";
    
    public static final String TEMP_IMG_VISO = "/viso_temp.png";
    
    public static final String TEMP_IMG_DOCUMENTO = "/documento_temp.png";
    
    public static final String IMG_VISO = "/viso.png";
    
    public static final String IMG_DOCUMENTO = "/documento.png";
    
    public static void writeToSDFile(User user)
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
        }
        
    }
    
    private static String getSimpleText(User user)
    {
        String simpleText = NOME + ": " + user.getNome() + "\n" + COGNOME + ": " + user.getCognome() + "\n" + NUMERO + ": " + user.getNumero() + "\n" + CREATION + ": " + user.getCreation() + "\n" + UPDATE + ":";
        Log.v(TAG, "Simple text: " + simpleText);
        return simpleText;
        
    }
    
    private static String getXMLElement(User user)
    {
        String element = "<user>\n\t<" + NOME + ">" + user.getNome() + "</" + NOME + ">\n\t<" + COGNOME + ">" + user.getCognome() + "</" + COGNOME + ">\n\t<" + NUMERO + ">" + user.getNumero() + "</" + NUMERO + ">\n\t<" + CREATION + ">" + user.getCreation() + "</" + CREATION + ">\n\t<" + UPDATE + "></" + UPDATE + ">\n</user>";
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
        
        File[] list = dir.listFiles();
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
            User user = user = readSingleXMLFile(userFile);
            Log.d(TAG, "ho letto l'utente numero: " + user.getNumero());
            users.add(user);
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
    
    public boolean deleteUser(User user)
    {
        File root = android.os.Environment.getExternalStorageDirectory();
        
        File oldDir = new File(root.getAbsolutePath() + "/" + APPFOLDER + "/" + user.getNumero());
        File backupDir = new File(root.getAbsolutePath() + "/" + APPFOLDER + "/." + user.getNumero());
        boolean success = oldDir.renameTo(backupDir);
        return success;
    }
    
    public static void moveTempImages(String number)
    {
        String srcPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + APPFOLDER + TEMP_IMG_PATH;
        String destPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + APPFOLDER + "/" + number;
        
        File visoTempImg = new File(srcPath + TEMP_IMG_VISO);
        File visoDestImg = new File(destPath + IMG_VISO);
        
        SDCard.copy(visoTempImg, visoDestImg);
        
        File documentoTempImg = new File(srcPath + TEMP_IMG_DOCUMENTO);
        File documentoDestImg = new File(destPath + IMG_DOCUMENTO);
        SDCard.copy(documentoTempImg, documentoDestImg);
    }
    
    private static void copy(File src, File dst)
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
            // do nothing
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
                // do nothing
            }
        }
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
    
}