/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

//import static client.Client.peerSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Peer;
import server.RFC;
import server.Server;
import static server.Server.peerSocket;
import static server.Server.peerThreadList;
import static server.Server.peers;
import static server.Server.rfcs;
import static server.Server.serverSocket;

/**
 *
 * @author shayan
 */

public class Client extends Thread {
    
    //public static ServerSocket serverSocket = null;
    public static Socket peerSocket = null;
    public static Socket clientSocket = null;
    private static DataInputStream is = null;
    private static PrintWriter os = null;
    private static DataInputStream is_client = null;
    private static PrintWriter os_client = null;
    private static BufferedReader br;
    private static String str;
    private static String strPortNum;
    private static Client client;
    public static final String VERSION = "P2P-CI/1.0";
    
    public Client(String strPortNum) {

        this.strPortNum = strPortNum;
        File file = new File(strPortNum);
        file.mkdir();
    }
    
    public void run() {
        try {

            peerSocket = new Socket("localhost", 7735);
            os = new PrintWriter(peerSocket.getOutputStream(), true);//new PrintStream(peerSocket.getOutputStream());
            is = new DataInputStream(peerSocket.getInputStream());
            
            while(true) {
                if(null!=peerSocket) {
                    System.out.println("Enter Command");
                    br = new BufferedReader(new InputStreamReader(System.in));
                    str = br.readLine();                    
                    if (str.equalsIgnoreCase("add")) {
                        System.out.println("Enter RFC Number");
                        br = new BufferedReader(new InputStreamReader(System.in));
                        String rfcId = br.readLine();
                        String strRFC = str + ":" + rfcId; 
                        strRFC += ":localhost:" + strPortNum + ":";
                        
                 int i=0;
                 String firstline="";
                  List<String> files = textFiles(strPortNum);
                  int found = 0;
                        for(String file: files ) {
                            if(file.equalsIgnoreCase(rfcId + ".txt")){
                                found = 1;
                                System.out.println("Found");
                                BufferedReader reader = new BufferedReader(new FileReader(strPortNum+"/"+rfcId +".txt"));
                                String contenttot = "";
                                String content = "";
                                
                                int k = 0;
                                    while ((content = reader.readLine()) != null) {
                                        k++;
                                        if(k==2)
                                        {
                                            firstline=content;
                                            //k=1;
                                        }
                                        contenttot += content+"\n";
                                    }
                                //os.println(contenttot);
                                //os.flush();
                                                               
                                i++;
                            }
                        }
                 
                        
                        
                        //System.out.println("Enter RFC Title");
                        //br = new BufferedReader(new InputStreamReader(System.in));
                        //String RFCtitle = br.readLine();
                        if(found==1) {
                            strRFC += firstline;
                            os.println(strRFC);
                        } else {
                            System.out.println("404 File Not Found");
                        }
                        os.flush();
                    } else if (str.equalsIgnoreCase("list")) {
                        os.println("list");
                        os.flush();
                        String response = is.readLine();
                        if(null!=response)
                            System.out.println(response);
                    } else if (str.equalsIgnoreCase("lookup")) {
                        System.out.println("Enter RFC Number");
                        br = new BufferedReader(new InputStreamReader(System.in));
                        String strRFC = str + ":" + br.readLine(); 
                        os.println(strRFC);
                        os.flush();
                        String response = is.readLine();
                        if(null!=response)
                            System.out.println(response);
                        
                    } else if (str.equalsIgnoreCase("end")) {
                        os.println("end:localhost:"+strPortNum);
                        Socket clientSoc = new Socket("localhost", Integer.parseInt(strPortNum));
                        PrintWriter os_client = new PrintWriter(clientSoc.getOutputStream(), true);//new PrintStream(peerSocket.getOutputStream());
                        DataInputStream is_client = new DataInputStream(clientSoc.getInputStream());
                        String stopCommand = "client:selfstop:current:process";
                        os_client.println(stopCommand);
                        os_client.flush();
                        os.flush();
                        os.close();
                        break;
                    } else if (str.equalsIgnoreCase("get")) {
                        String portNum = "";
                        System.out.println("Enter RFC Number");
                        br = new BufferedReader(new InputStreamReader(System.in));
                        String rfcId = br.readLine();
                        String strRFC = str + ":" + rfcId;
                        System.out.println("Enter Hostname");
                        br = new BufferedReader(new InputStreamReader(System.in));
                        String hostname = br.readLine();
                        strRFC += ":" + hostname;
                        System.out.println("Enter PortNumber");
                        portNum = br.readLine();
                        strRFC += ":" + portNum;
                        clientSocket = new Socket("localhost", Integer.parseInt(portNum));
                        os_client = new PrintWriter(clientSocket.getOutputStream(), true);//new PrintStream(peerSocket.getOutputStream());
                        is_client = new DataInputStream(clientSocket.getInputStream());
                        os_client.println(strRFC);
                        os_client.flush();
                        String response1 = "";
                        String response = "";
                        int k=0;
                        String firstline ="";
                        String lastModified = "";
                        while(!(response = is_client.readLine()).trim().equalsIgnoreCase("eof") ){
                            k++;
                            if(k==1) {
                                lastModified += response;
                            }
                            if(k==2) {
                                firstline += response;
                            }
                            if(k!=1) {
                                response1 += response + "\n";
                            }
                        }
                        try {
                            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(strPortNum+"/"+rfcId+".txt")));
                            out.println(response1);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String formatedResponse = VERSION + " " + "200 OK\n";
                        formatedResponse += "Date: " + new Date().toGMTString() + "\n";
                        formatedResponse += "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n";
                        formatedResponse +=  "Last Modified: " + new SimpleDateFormat("yyyy MM dd HH:mm:ss").format(new Date(Long.parseLong(lastModified))) + "\n";
                        formatedResponse += "Content-Length: "+ new File(strPortNum+"/"+rfcId+".txt").length() + "\n";
                        formatedResponse += "Content-Type: Text\n" ;
                        formatedResponse += response1;
                        os.println("add:"+rfcId+":"+hostname+":"+strPortNum+":"+firstline);
                        os.flush();
                        if(null!=formatedResponse)
                            System.out.println(formatedResponse);
                        else {
                        System.out.println("Bad Command");
                    }
                        
                    } 

                }
            }

            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
  List<String> textFiles(String directory) {
  List<String> textFiles = new ArrayList<String>();
  File dir = new File(directory);
  for (File file : dir.listFiles()) {
    if (file.getName().endsWith((".txt"))) {
      textFiles.add(file.getName());
    }
  }
  return textFiles;
}    
    
    public static void main(String args[]) {
        
            System.out.println("Enter the port number: ");
            br = new BufferedReader(new InputStreamReader(System.in));
        try {
            strPortNum = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
            client = new Client(strPortNum);
            client.start();            
            if(client.isAlive()) {
                clientServer cServer = new clientServer(strPortNum);
                cServer.start();
            }

    }
    
}

class clientServer extends Thread {
    
    public static ServerSocket serverSocket = null;
    public static Socket peerSocket = null;
    public static List<RFC> rfcs = null;
    public DataInputStream is = null;
    public PrintWriter os = null;
    public String port = null;
    public static final String VERSION = " P2P-CI/1.0";
    public clientServer(String port) {
        try {
            
            System.out.println("port===>"+port);
            this.port = port;
            serverSocket = new ServerSocket(Integer.parseInt(port));
            rfcs = new ArrayList<RFC>();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public void run() {
        try {
            peerSocket = serverSocket.accept();
            is = new DataInputStream(peerSocket.getInputStream());
            os = new PrintWriter(peerSocket.getOutputStream(), true);
        
        while (true) {
            try {
                String line = is.readLine();
                if(null!=line) {
                   
                   String formatedRequest = "GET RFC ";//GET FC 1234 P2P-CI/1.0 \n Host: <hostname> \n OS: MAC
                    String [] strsplit = line.split(":");
                 if(strsplit[0].equalsIgnoreCase("get")){
                        String rfcId = strsplit[1];
                        formatedRequest += rfcId;
                        formatedRequest += VERSION + "\n";
                        String hostname = strsplit[2];
                        String portNum = strsplit[3]; //"clent:selfstop:current:process"
                        formatedRequest += "Host: "+ hostname + "\n";
                        formatedRequest += "OS: "+System.getProperty("os.name") + " " + System.getProperty("os.version");                       
                        System.out.println(formatedRequest);
                        List<String> files = textFiles(port+"/");
                        int i = 0;
                        for(String file: files ) {
                            if(file.equalsIgnoreCase(rfcId + ".txt")){
                                System.out.println("Found");
                                //os.println("test");
                                long lmodified = new File(port+"/"+rfcId +".txt").lastModified();
                                String strLastModified = Long.toString(lmodified);
                                BufferedReader reader = new BufferedReader(new FileReader(port+"/"+rfcId +".txt"));
                                String contenttot = "";
                                String content = "";
                                contenttot = strLastModified+ "\n";
                                while ((content = reader.readLine()) != null) {

                                    contenttot += content+"\n";
                                }
                                os.println(contenttot);
                                os.flush();
                                                               
                                i++;
                            }
                        }
                        if(i==0) {
                            os.println("Not Found");
                            os.flush();
                        }
                 } else if(strsplit[0].equalsIgnoreCase("client")) {
                        if(strsplit[1].equalsIgnoreCase("selfstop")) {
                            System.out.println("killing ... ");
                            this.interrupt();
                            return;
                        }
                 }else {
                     //bad req
                 }
                }
            } catch (IOException ex) {
                //Logger.getLogger(peerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }
        
  List<String> textFiles(String directory) {
  List<String> textFiles = new ArrayList<String>();
  File dir = new File(directory);
  for (File file : dir.listFiles()) {
    if (file.getName().endsWith((".txt"))) {
      textFiles.add(file.getName());
    }
  }
  return textFiles;
}    
        
}


