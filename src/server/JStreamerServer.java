/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.Ostermiller.util.CircularByteBuffer;
import data.ClientWrapper;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import net.JMobotixWritePool;
import net.MobotixReader;


/**
 * Create a server
 * @author Patrick Facco
 */
public class JStreamerServer
{
    public final static int DEFAULT_DIM = 1024;

    private final MobotixReader reader;
    private CircularByteBuffer buffer;
    private final ServerSocket serverSocket;
    private final int port;
    private final String username;
    private final String password;
    private final String sourceIp;
    private boolean flag;
    

    public JStreamerServer(int port, String username, String password,
            String sourceIp) throws IOException
    {
        this.port = port;
        this.username = username;
        this.password = password;
        this.sourceIp = sourceIp;
        buffer = new CircularByteBuffer(DEFAULT_DIM/*, true*/);
        reader = new MobotixReader(username, password, sourceIp/*, buffer, sleep*/);
        serverSocket = new ServerSocket(port);
        flag = true;
    }

    public void printCredits()
    {
        System.out.println(" __________________________________________________");
        System.out.println("|                                                  |");
        System.out.println("|       Proxy for Mobotix Webcam v.0.1(beta)       |");
        System.out.println("|               Author: Patrick Facco              |");
        System.out.println("|                     facco@csp.it                 |");
        System.out.println("|__________________________________________________|");
    }

    public void run() throws IOException
    {
        new Thread(reader).start();       
        boolean first = true;
        JMobotixWritePool jmwp = new JMobotixWritePool(buffer);
        printCredits();
        System.out.println("[" + new Date()  + "] - Reader succefully started");

        while (flag)
        {
            Socket s = serverSocket.accept();
            System.out.println("[" + new Date()  + "] - New Connection Accepted");
            ClientWrapper c = new ClientWrapper(s, true);
            System.out.println("[" + new Date()  + "] - new Client Created");
            buffer = ((MobotixReader) reader).getBuffer();
            if(first)
            {
                jmwp = new JMobotixWritePool(buffer);
                jmwp.addClient(c);
                new Thread(jmwp).start();
                first = false;
            }
            else
            {
                jmwp.addClient(c);
                System.out.println("[" + new Date()  + "] - new Client Added");
            }            
           
        }
    }

    public void stop()
    {
        flag = false;
    }

    @Override
    public String toString()
    {
        return "JStreamerServer{" + "reader=" + reader + ", buffer=" + buffer + ", serverSocket=" + serverSocket + ", port=" + port + ", username=" + username + ", password=" + password + ", sourceIp=" + sourceIp + ", flag=" + flag + '}';
    }     
 
}
