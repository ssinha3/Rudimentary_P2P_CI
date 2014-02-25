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
public class RFC {
    public String rfcNumber;
    public String rfcTitle;
    public String peerHostname;
    public String peerListeningPort;

    public String getPeerListeningPort() {
        return peerListeningPort;
    }

    public void setPeerListeningPort(String peerListeningPort) {
        this.peerListeningPort = peerListeningPort;
    }
    
            
    public String getRfcNumber() {
        return rfcNumber;
    }

    public void setRfcNumber(String rfcNumber) {
        this.rfcNumber = rfcNumber;
    }

    public String getRfcTitle() {
        return rfcTitle;
    }

    public void setRfcTitle(String rfcTitle) {
        this.rfcTitle = rfcTitle;
    }

    public String getPeerHostname() {
        return peerHostname;
    }

    public void setPeerHostname(String peerHostname) {
        this.peerHostname = peerHostname;
    }
    
}
