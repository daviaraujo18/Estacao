/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hardware;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.StringTokenizer;

/**
 *
 * Esta classe recupera informacoes do hardware(HD / MAC Address)
 * @author Anderson Soares
 */
public class RecoverAddressMAC {
    
    public static void main(String[] args) throws IOException, ParseException {
        System.out.println("Endereco MAC: "+windowsParseMacAddress(windowsRunIpconfigCommand()));
    }
    
    private static String windowsRunIpconfigCommand() throws IOException {
        Process p = Runtime.getRuntime().exec("ipconfig /all");
        
        InputStream stoutStream = new BufferedInputStream(p.getInputStream());
        StringBuffer buffer = new StringBuffer();
        
        while(true) {
            int c = stoutStream.read();
            if (c == -1) break;
            buffer.append((char) c);
        }
        
        String outputText = buffer.toString();
        
        stoutStream.close();
        
        return outputText;
    }
    
     private final static String windowsParseMacAddress(String ipConfigResponse) throws ParseException {
        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Localhost: " +localHost);
        } catch(java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(), 0);
        }

        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
        String lastMacAddress = null;

        while(tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();

            // see if line contains IP address
            if(line.contains(localHost) && lastMacAddress != null) {
                return lastMacAddress;
            }

            // see if line contains MAC address
            int macAddressPosition = line.indexOf(":");
            if(macAddressPosition <= 0) continue;

            String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
            if(windowsIsMacAddress(macAddressCandidate)) {
                lastMacAddress = macAddressCandidate;
                System.out.println("Last Address Candidate: "+lastMacAddress);
                continue;
            }
        }

         System.out.println("Lancando Exception");
        ParseException ex = new ParseException("cannot read MAC address from [" + ipConfigResponse + "]", 0);
        ex.printStackTrace();
        throw ex;
    }

    private final static boolean windowsIsMacAddress(String macAddressCandidate) {
        // TODO: use a smart regular expression
        if(macAddressCandidate.length() != 17) return false;

        return true;
    }

    
}
