package com.cisco.csr;

public class CSRConfig {
	
	//CSR login information
	String username = "";
	String ip = "";
	String keyPath = "";
	
	//CSR IOS-XE configurations
	String vrfName = "";
	String rd = "";
	String rt = "";
	String g2IP = "";
	String g2Mask = "";
	String ftdvIP =  "";
	int ftdvBGPAS;
	int csrBGPAS;
	String ftdvSubnet = "";
	String ftdvSubnetMask = "";
	String g2Gateway = "";
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getVrfName() {
		return vrfName;
	}

	public void setVrfName(String vrfName) {
		this.vrfName = vrfName;
	}

	public String getRd() {
		return rd;
	}

	public void setRd(String rd) {
		this.rd = rd;
	}

	public String getRt() {
		return rt;
	}

	public void setRt(String rt) {
		this.rt = rt;
	}

	public String getG2IP() {
		return g2IP;
	}

	public void setG2IP(String g2ip) {
		g2IP = g2ip;
	}

	public String getG2Mask() {
		return g2Mask;
	}

	public void setG2Mask(String g2Mask) {
		this.g2Mask = g2Mask;
	}

	public String getFtdvIP() {
		return ftdvIP;
	}

	public void setFtdvIP(String ftdvIP) {
		this.ftdvIP = ftdvIP;
	}

	public int getFtdvBGPAS() {
		return ftdvBGPAS;
	}

	public void setFtdvBGPAS(int ftdvBGPAS) {
		this.ftdvBGPAS = ftdvBGPAS;
	}

	public int getCsrBGPAS() {
		return csrBGPAS;
	}

	public void setCsrBGPAS(int csrBGPAS) {
		this.csrBGPAS = csrBGPAS;
	}

	

	public String getFtdvSubnet() {
		return ftdvSubnet;
	}

	public void setFtdvSubnet(String ftdvSubnet) {
		this.ftdvSubnet = ftdvSubnet;
	}

	public String getFtdvSubnetMask() {
		return ftdvSubnetMask;
	}

	public void setFtdvSubnetMask(String ftdvSubnetMask) {
		this.ftdvSubnetMask = ftdvSubnetMask;
	}

	public String getG2Gateway() {
		return g2Gateway;
	}

	public void setG2Gateway(String g2Gateway) {
		this.g2Gateway = g2Gateway;
	}

	
	
	public String getVRFConfig(){
		String vrfConfig = "ip vrf "+vrfName+"\n"+
				"rd "+rd+"\n"+
				"route-target export "+rt+"\n"+
				"route-target import "+rt+"\n"+
				"exit\n";
		
		return vrfConfig;
	}
	
	public String getEndConfig(){
		return "end\n " +"exit\n";
	}
	
	public String getINTConfig(){
		String intConfig = "interface GigabitEthernet2\n"+
				"ip vrf forwarding "+ vrfName +"\n"+
				"ip address "+ g2IP+ " "+ g2Mask +"\n"+
				"load-interval 30\n"+
				"negotiation auto\n"+
				"no shutdown\n"+
				"exit\n";
		return intConfig;
	}
	
	public String getBGPConfig(){
		String bgpConfig = "router bgp "+csrBGPAS+"\n"+
				 "address-family ipv4 vrf "+vrfName +"\n"+
				  "redistribute connected\n"+
				  "neighbor "+ftdvIP+" remote-as "+ftdvBGPAS+"\n"+
				  "neighbor "+ftdvIP+" ebgp-multihop 255\n"+
				  "neighbor "+ftdvIP+" activate\n"+
				 "exit-address-family\n"+
				 "exit\n";
		return bgpConfig;
	}
	
	public String getStaticRouteConfig(){
		String staticRouteConfig = "ip route vrf "+vrfName+" "+ftdvSubnet+" "+ftdvSubnetMask+" "+g2Gateway+"\n";
		return staticRouteConfig;
	}
	
	public String getEnableConfig(){
		return "confi terminal";
	}
	

}
