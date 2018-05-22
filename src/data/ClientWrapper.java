/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import java.net.Socket;

/**
 * A Wrapper for Client description
 * @author Parick Facco
 */
public class ClientWrapper
{
    private Socket socket;
    private boolean firstTime;

    public ClientWrapper(Socket socket, boolean firstTime)
    {
        this.socket = socket;
        this.firstTime = firstTime;
    }

    public void setSocket(Socket s)
    {
        socket = s;
    }

    public void setFirstTime(boolean ft)
    {
        firstTime = ft;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public boolean getFirstTime()
    {
        return firstTime;
    }

    @Override
    public String toString()
    {
        return socket.getInetAddress() + " " + firstTime;
    }
}
