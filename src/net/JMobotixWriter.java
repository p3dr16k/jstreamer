/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net;

import data.ClientWrapper;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread for buffer delivery
 * @author Patrick Facco
 */
public class JMobotixWriter extends Thread
{
    private ClientWrapper client;
    private byte[] buffer;
    private boolean flag;
    private DataOutputStream out;

    /**
     * Build an instance of JMobotixWriter
     * @param client a wrapper for a client
     * @throws IOException if can't obtain output stream
     */
    public JMobotixWriter(ClientWrapper client) throws IOException
    {
        this.client = client;
        flag = true;
        out = new DataOutputStream(client.getSocket().getOutputStream());
    }

    /**
     * Update buffer to deliver
     * @param newBuffer new buffer to deliver
     */
    public /*synchronized*/ void setBuffer(byte[] newBuffer)
    {
        buffer = new byte[newBuffer.length];
        System.arraycopy(newBuffer, 0, buffer, 0, newBuffer.length);       
    }
    
    public ClientWrapper getClient()
    {
        return client;
    }
    /**
     * Close stream
     * @throws IOException
     */
    public void close() throws IOException
    {
        out.close();
    }

    /**
     * Stop thread
     */
    public void stopWriter()
    {
        flag = false;
        try
        {
            close();
            System.out.println("[" + new Date()  + "] - Writer Stopped");
        }
        catch(IOException ex)
        {
            Logger.getLogger(JMobotixWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Busy-Waiting loop waiting for new buffer to deliver
     * @throws InterruptedException
     * @throws IOException
     */
    public synchronized void waitBuffer() throws InterruptedException, IOException
    {
        if(client.getFirstTime())
        {

           String toSend = "HTTP/1.0 200 OK \r\n" +
                            "Content-type: multipart/x-mixed-replace;" +
                            " boundary=\"--myboundary\" \r\n\r\n";
           // System.out.println("INVIO");
           // System.out.println(toSend);
           
            out.write(toSend.getBytes(), 0, toSend.getBytes().length);
            client.setFirstTime(false);
           
        }
       
        if(buffer != null)
        {
            if(buffer.length == 0)
            {
                stopWriter();
                System.exit(-3);
            }
            out.write(buffer, 0, buffer.length);
            buffer = null;
        }        
    }

    /**
     * Wait for buffer and deliver it
     */
    @Override
    public void run()
    {
        System.out.println("[" + new Date()  + "] - New writer started");
        while(flag)
        {
            try
            {
                waitBuffer();
                Thread.sleep(10);
            
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(JMobotixWriter.class.getName()).log(Level.SEVERE, null, ex);
                stopWriter();
            }
            catch (IOException ex)
            {
                
                System.out.println("[" + new Date()  + "] - Writer Stopped");
                stopWriter();
            }
        }
    }

    /**
     * Return true if this thread is running, false otherwise
     * @return true if this thread is running, false otherwise
     */
    public boolean getFlag()
    {
        return flag;
    }
}
