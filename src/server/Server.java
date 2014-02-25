/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author shayan
 */
public class Server {

    public static ServerSocket serverSocket = null;
    public static Socket peerSocket = null;
    public static ArrayList<peerThread> peerThreadList = new ArrayList<peerThread>();
    public static List<Peer> peers = null;
    public static List<RFC> rfcs = null;
    
    public Server() {
        try {
            serverSocket = new ServerSocket(7735);
            peers = new ArrayList<Peer>();
            rfcs = new ArrayList<RFC>();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        while (true) {
            try {
                peerSocket = serverSocket.accept();
                peerThread peerthread = new peerThread(peerSocket, peers, rfcs);
                peerThreadList.add(peerthread);
                Peer peer = new Peer();
                peer.setPeerHostname("localhost");
                peer.setPortNumberUpload(7736);
                peers.add(peer);
                peerthread.start();

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}

class peerThread extends Thread {

    private BufferedReader br;
    private String str;
    public DataInputStream is = null;
    public PrintWriter os = null;
    public Socket peerSocket = null;
    public final static int SUCESSFUL = 1;
    public List<Peer> peers;
    public List<RFC> rfcs;
    
    public peerThread(Socket peerSocket, List<Peer> peers, List<RFC> rfcs) {
        this.peerSocket = peerSocket;
        this.rfcs = rfcs;
        this.peers = peers;
    }
        
    public void run() {
        try {
        is = new DataInputStream(peerSocket.getInputStream());
        os = new PrintWriter(peerSocket.getOutputStream(), true);
        while (true) {
            try {
                String line = is.readLine();
                if(null!=line) {
                    System.out.println(line);
                    String [] strsplit = line.split(":");
                    
                    if(strsplit[0].equalsIgnoreCase("add")){
                        String rfcId = strsplit[1];
                        String hostname = strsplit[2];
                        String portNum = strsplit[3];
                        String title = strsplit[4];
                        RFC rfc = new RFC();
                        rfc.setRfcNumber(rfcId);
                        rfc.setPeerHostname(hostname);
                        rfc.setPeerListeningPort(portNum);
                        rfc.setRfcTitle(title);
                        rfcs.add(rfc);
                        
                    } else if(strsplit[0].equalsIgnoreCase("list")) {
                        String response = "";
                        for(RFC rfc: rfcs) {
                            response += rfc.getRfcNumber() +":" + rfc.getPeerHostname()+ ":"+ rfc.getPeerListeningPort()+ ":"+ rfc.getRfcTitle()+";";
                        }
                        if(rfcs.size()==0)
                            response = "No RFC's Found";
                        os.println(response);
                        os.flush();
                    } else if(strsplit[0].equalsIgnoreCase("lookup")) {
                        String response = "";
                        for(RFC rfc: rfcs) {
                            if(rfc.getRfcNumber().equalsIgnoreCase(strsplit[1].trim())) {
                                response += rfc.getRfcNumber() +":" + rfc.getPeerHostname()+ ":"+ rfc.getPeerListeningPort()+ ":"+ rfc.getRfcTitle()+";";
                            }
                        }
                        if(response.equals(""))
                            response = "No Such RFC Found";
                        
                        os.println(response);
                        os.flush();
                    } else if (strsplit[0].equalsIgnoreCase("end")) {
                        List<RFC> toBeRemoved = new ArrayList<RFC>();
                        for(RFC rfc: rfcs) {
                            System.out.println(rfc.getRfcNumber());
                            if(rfc.getPeerListeningPort().equalsIgnoreCase(strsplit[2])) {
                             //rfcs.remove(rfc);
                              toBeRemoved.add(rfc);
                            }
                        }
                        rfcs.removeAll(toBeRemoved);
                        System.out.println(rfcs.size());
                    }
                }
                
                
                //os.write(SUCESSFUL);
            } catch (IOException ex) {
                Logger.getLogger(peerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }
}
