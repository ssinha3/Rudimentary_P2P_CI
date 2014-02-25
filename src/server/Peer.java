/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

/**
 *
 * @author shayan
 */
public class Peer {
    public String peerHostname;
    public int portNumberUpload;

    public String getPeerHostname() {
        return peerHostname;
    }

    public void setPeerHostname(String peerHostname) {
        this.peerHostname = peerHostname;
    }

    public int getPortNumberUpload() {
        return portNumberUpload;
    }

    public void setPortNumberUpload(int portNumberUpload) {
        this.portNumberUpload = portNumberUpload;
    }
    
}
