/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net;

import com.Ostermiller.util.CircularByteBuffer;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Encoder;

/**
 * Read frame from webcam and save it in a buffer
 * @author Patrick Facco
 */
public class MobotixReader implements Runnable
{
    //public static final String TO_APPEND = "/cgi-bin/faststream.jpg";
    public static final String SEPARATOR = "--Mobotix";

    private String username;
    private String password;
    private String link;
    private CircularByteBuffer buffer;
    private InputStream inputStream;
    private boolean flag;   
    private OutputStream fillBuffer;

    /**
     * Build an istance of MobotixReader
     * @param username username for autenthication on the cam
     * @param password password for autenthication on the cam
     * @param link cam's link address
     */
    public MobotixReader(String username, String password, String link)
    {
        this.username = username;
        this.password = password;
        this.link = link;       
        flag = true;       
    }

    /**
     * Set new username
     * @param u new username
     */
    public void setUsername(String u)
    {
        username = u;
    }

    /**
     * Set new password
     * @param p new password
     */
    public void setPassword(String p)
    {
        password = p;
    }

    /**
     * Set new ip address
     * @param link new ip address
     */
    public void setLink(String link)
    {
        this.link = link;
    }

    /**Set new buffer
     * @param b new buffer
     */
    public void setBuffer(CircularByteBuffer b)
    {
        buffer = b;
    }  

    /**
     * Gets username
     * @return username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Gets password
     * @return password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Gets ip
     * @return ip address of webcam
     */
    public String getLink()
    {
        return link;
    }

    /**
     * Gets buffer
     * @return buffer
     */
    public CircularByteBuffer getBuffer()
    {
        return buffer;
    }   

    /**
     * Connect reader to webcam using basic authentication
     */
    public void connect()
    {
        URL url;
        try
        {
            url = new URL(/*"http://" + ip + TO_APPEND*/link);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput (true);
            urlConnection.setDoOutput (true);
            urlConnection.setUseCaches (false);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            urlConnection.setRequestProperty("Referer", url.getHost());
            urlConnection.setRequestProperty("Keep-Alive", "115");
            urlConnection.setRequestProperty("Authorization", "Basic " + encode(username+":"+password));
            inputStream = urlConnection.getInputStream();
            System.out.println("[" + new Date() + "] - Connected to " + url.toString());

        }
        catch(NoRouteToHostException ex)
        {
            Logger.getLogger(MobotixReader.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[" + new Date() + "] - NoRouteToHostException");
            disconnect();
            System.exit(-3);
        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(MobotixReader.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[" + new Date() + "] - MalformedUrlException");
            disconnect();
            System.exit(-3);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MobotixReader.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("[" + new Date() + "] - IOException");
            disconnect();
            System.exit(-3);
        }
       
    }

    private static String encode(String source)
    {
        return new BASE64Encoder().encode(source.getBytes());
    }

    /**
     * Close connection
     */
    public void disconnect()
    {
        try
        {
            inputStream.close();
            System.out.println("[" + new Date() + "] - Disconnected");
        }
        catch (IOException ex)
        {
            Logger.getLogger(MobotixReader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-3);
        }
    }

    /**
     * Read stream's header and return it
     * @param in input stream, the source of data
     * @return a String that rapresent stream's header
     * @throws IOException if inputstream is unavailable
     */
    public String readHeader(DataInputStream in) throws IOException
    {
        byte[] tmp = new byte[1024];
        String ris = "";
        String hTmp = new String(tmp);
        do
        {
            in.read(tmp);
            hTmp = new String(tmp);
           
            ris += normalize(hTmp);
        }
        while(!hTmp.contains("Content-Length"));      
        return ris;
    }

    /**
     * Remove space from string
     * @param s string to normalize
     * @return string normalized
     */
    public static String normalize(String s)
    {
        int index = 0;
        byte[] tmp = s.getBytes();
        while(tmp[index] != 0)
            index++;

        return s.substring(0, index);
    }

    /**
     * Read dim bytes from input stream
     * @param in input stream for data
     * @param dim dim of bytes readed
     */
    public void readFrame(DataInputStream in, int dim)
    {       
       try
       {           
           byte[] b = new byte[dim];
           int readed = in.read(b);
           
           if(readed <= 0)
           {
               disconnect();
               System.exit(-3);
           }
           
           byte[] btmp = new byte[readed];
           System.arraycopy(b, 0, btmp, 0, readed);
           
           fillBuffer.write(btmp);
           
         
       }      
       catch (IOException ex)
       {
           Logger.getLogger(MobotixReader.class.getName()).log(Level.SEVERE, null, ex);
           System.exit(-3);
       }
      
    }

    /**
     * Stop reading bytes and stop thread
     */
    public void stopRead()
    {
        flag = false;
    }   

    /**
     * Read frame and write it on buffer
     */
    public void run()
    {
        try
        {
            connect();
            DataInputStream in = new DataInputStream(inputStream);
            String header = readHeader(in);            
            int imageLength = Integer.parseInt(header.substring(header.indexOf("Content-Length: ")+16, header.indexOf("Content-Length: ") +21 ).trim());            
            
            buffer = new CircularByteBuffer(imageLength, true);
            
            fillBuffer = buffer.getOutputStream();
            fillBuffer.write(header.getBytes());

            while(flag)
            {
                readFrame(in, imageLength);                
            }
            in.close();

            disconnect();
        }
        catch(IOException ioe)
        {            
            stopRead();
        }      
    }  
}
