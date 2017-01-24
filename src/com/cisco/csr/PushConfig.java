package com.cisco.csr;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.jcraft.jsch.*;

public class PushConfig {
	
	public static void main(String[] arg){
		
		//PushConfig pc = new PushConfig();
		//pc.sendConfig("","","");
		
		
	}

	public static void sendConfig(CSRConfig csr){
		JSch jsch = new JSch();
		//String username = "automate";
		//String hostname = "34.192.97.49";
		//String privateKeyPath = "SSHKEY/prikey.pem";
		Integer port = 22;
		
		/*String enableConfig = "confi terminal";
		String vrfConfig = "ip vrf ftdvLambda\n"+
							"rd 64512:100\n"+
							"route-target export 64512:0\n"+
							"route-target import 64512:0\n"+
							"exit\n";
		
		String intConfig = "interface GigabitEthernet3\n"+
							"ip vrf forwarding ftdvLambda\n"+
							"ip address 10.0.8.8 255.255.255.0\n"+
							"load-interval 30\n"+
							"negotiation auto\n"+
							"no shutdown\n"+
							"exit\n";
		
		String bgpConfig = "router bgp 64512\n"+
				 "address-family ipv4 vrf ftdvLambda\n"+
				  "redistribute connected\n"+
				  "neighbor 10.0.4.142 remote-as 65002\n"+
				  "neighbor 10.0.4.142 ebgp-multihop 255\n"+
				  "neighbor 10.0.4.142 activate\n"+
				 "exit-address-family\n"+
				 "exit\n";
		
		String staticRouteConfig = "ip route vrf ftdvLambda 10.0.4.0 255.255.255.0 10.0.8.1\n";*/
		
		try {
			
			jsch.addIdentity(csr.keyPath);
			jsch.setConfig("StrictHostKeyChecking", "no");
			Utils.log("identity added ");
			
			Session session = jsch.getSession(csr.username, csr.ip, port);
			Utils.log("session created.");
			
			
			session.connect();
			Utils.log("session connected.....");

            
            Channel channel = session.openChannel("shell");
            
            OutputStream ops = channel.getOutputStream();
            PrintStream ps = new PrintStream(ops,true);
            
            channel.connect();
            
            ps.println(csr.getEnableConfig());
            ps.println(csr.getVRFConfig());
            ps.println(csr.getINTConfig());
            ps.println(csr.getBGPConfig());
            ps.println(csr.getStaticRouteConfig());
            ps.println(csr.getEndConfig());
            //ps.println("show ip int b");
            
            InputStream input = channel.getInputStream();
          //start reading the input from the executed commands on the shell
          byte[] tmp = new byte[1024];
          while (true) {
              while (input.available() > 0) {
                  int i = input.read(tmp, 0, 1024);
                  if (i < 0) break;
                  Utils.log(new String(tmp, 0, i));
              }
              if (channel.isClosed()){
            	  Utils.log("exit-status: " + channel.getExitStatus());
                  break;
              }
              Thread.sleep(1000);
          }
            ps.flush();
            ps.close();
            channel.disconnect();
            session.disconnect();
            
            Utils.log("push finished:" + csr.getIp());
            
		} catch (IOException | JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
