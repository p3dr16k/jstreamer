/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net;

import com.Ostermiller.util.CircularByteBuffer;
import data.ClientWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pool of writer
 * @author Patrick Facco
 */
public class JMobotixWritePool implements Runnable
{
    private List<JMobotixWriter> clients;
    private CircularByteBuffer buffer;
    private boolean flag;
    private InputStream emptyBuffer;

    /**
     * Build an istance of JMobotixWritePool
     * @param buffer
     */
    public JMobotixWritePool(CircularByteBuffer buffer)
    {
        try
        {
            this.buffer = buffer;
            clients = new LinkedList<JMobotixWriter>();
            flag = true;
            emptyBuffer = buffer.getInputStream();
        }
        catch(NullPointerException  npe)
        {
            System.exit(-3);
        }
    }

    /**
     * Add new client to a pool
     * @param client client to add
     * @throws IOException
     */
    public synchronized  void addClient(ClientWrapper client) throws IOException
    {
        JMobotixWriter jmw = new JMobotixWriter(client);
        clients.add(jmw);
        new Thread(jmw).start();
        System.out.println("[" + new Date()  + "] - New Client " + client.getSocket().getInetAddress() + " added");
        System.out.println("[" + new Date()  + "] - Client Pool Size " + clients.size());
    }

    /**
     * Stop this thread
     */
    public void stop()
    {
        flag = false;
    }

    /**
     * Deliver buffer to all writers in a pool
     */
    @Override
    public void run()
    {
        while(flag)
        {
            try
            {
                byte[] btmp = new byte[buffer.getAvailable()];
                int readed = emptyBuffer.read(btmp);
                
                //System.out.println("Client connessi " + clients.size());
                if(readed > 0)
                {
                   // System.out.println("pubblicati " + readed + " bytes");
                    for (int i = 0; i < clients.size(); i++)
                    {
                        JMobotixWriter jmw = clients.get(i);
                        if(jmw.getFlag())
                            jmw.setBuffer(btmp);
                        else
                        {
                            jmw.stopWriter();
                            jmw.getClient().getSocket().close();
                            clients.remove(jmw);
                            System.out.println("[" + new Date()  + "] - Client " + jmw.getClient().getSocket().getInetAddress() + " removed");
                            System.out.println("[" + new Date()  + "] - Client Pool Size " + clients.size());
                        }
                    }
                }
            }
            catch (IOException ex)
            {
                Logger.getLogger(JMobotixWritePool.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
}
