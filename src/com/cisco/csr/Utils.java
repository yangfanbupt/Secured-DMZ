package com.cisco.csr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class Utils {
	
	
	//TransitVPC
	public static String USER_NAME_KEY = "USER_NAME";
	public static String USER_NAME = "";
	public static String PRIVATE_KEY_KEY = "PRIVATE_KEY";
	public static String PRIVATE_KEY = "";
	public static String PUBLIC_KEY_KEY = "PUBLIC_KEY";
	public static String PUBLIC_KEY = "";
	public static String PASSWORD_KEY = "PASSWORD";
	public static String PASSWORD = "";
	public static String CSR1_IP_KEY = "EIP1";
	public static String CSR1_IP = "";
	public static String CSR2_IP_KEY = "EIP2";
	public static String CSR2_IP = "";
	//public static String KMS_KEY_KEY = "KMS_KEY";
	//public static String KMS_KEY = "";
	
	//FTDv
	public static String CSR1_KEY = "CSR1";
	public static String CSR2_KEY = "CSR2";
	public static String FTDV_KEY = "FTDv";
	public static String VRF_NAME_KEY = "vrfName";
	public static String CSR1_VRF_NAME = "";
	public static String CSR2_VRF_NAME = "";
	public static String RD_KEY = "rd";
	public static String CSR1_RD = "";
	public static String CSR2_RD = "";
	public static String RT_KEY = "rt";
	public static String CSR1_RT = "";
	public static String CSR2_RT = "";
	public static String G2_IP_KEY = "g2IP";
	public static String CSR1_G2_IP = "";
	public static String CSR2_G2_IP = "";
	public static String G2_MASK_KEY = "g2Mask";
	public static String CSR1_G2_MASK = "";
	public static String CSR2_G2_MASK = "";
	public static String AS_KEY = "AS";
	public static String CSR1_AS = "";
	public static String CSR2_AS = "";
	public static String G2_GW_KEY = "g2Gateway";
	public static String CSR1_G2_GW = "";
	public static String CSR2_G2_GW = "";
	public static String FTDV_AS_KEY = "AS";
	public static String FTDV_AS = "";
	public static String FTDV_IP_KEY = "ftdvIP";
	public static String FTDV_IP = "";
	public static String FTDV_SUBNET_KEY = "ftdvSubnet";
	public static String FTDV_SUBNET = "";
	public static String FTDV_SUBNET_MASK_KEY = "ftdvSubnetMask";
	public static String FTDV_SUBNET_MASK = "";
	
	static Region region = null;
	
	public static CSRConfig csr1 = new CSRConfig();
	public static CSRConfig csr2 = new CSRConfig();
	
	public static String S3_TRANSIT_BUCKET_NAME_KEY = "S3_TRANSIT_BUCKET_NAME";
	public static String S3_TRANSIT_BUCKET_PREFIX_KEY = "S3_TRANSIT_BUCKET_PREFIX";
	public static String S3_TRANSIT_CONFIG_FILE_NAME_KEY = "S3_TRANSIT_CONFIG_FILE_NAME";
	
	public static String S3_TRANSIT_BUCKET_NAME = "awsmptransitnetworkvpcwiththecs-vpnconfigs3bucket-rqezpfacpkq8";
	public static String S3_TRANSIT_BUCKET_PREFIX ="vpnconfigs/";
	public static String S3_TRANSIT_CONFIG_FILE_NAME = "transit_vpc_config.txt";
	
	public static String S3_FTDV_BUCKET_NAME = "";
	public static String S3_FTDV_KEY = "";
	
	public static LambdaLogger logger = null;
	
	public static void log(Object o){
		logger.log(o.toString());
	}
	
	public static String downloadKey() throws IOException, InterruptedException{
		AmazonS3 s3Client = new AmazonS3Client();
		//Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		
		s3Client.setRegion(region);
		
		S3Object object = s3Client.getObject(S3_TRANSIT_BUCKET_NAME, S3_TRANSIT_BUCKET_PREFIX+PRIVATE_KEY);
		
		InputStream in = object.getObjectContent();
		byte[] buf = new byte[1024];
		OutputStream out = new FileOutputStream("/tmp/"+PRIVATE_KEY);
		
		int count = 0;
		while( ( count = in.read(buf)) != -1)
		{
		   out.write(buf, 0, count);
		}
		out.close();
		in.close();
		
		return "";
		
	}
	
	public static void setENVFTDv(Map<String,Object> resourceProps){
		Regions regions = Regions.fromName(System.getenv("AWS_REGION"));
		region = Region.getRegion(regions);
		
		CSR1_VRF_NAME = "ftdvVRF";
		CSR1_RD = "64512:100";
		CSR1_RT = "64512:0";		
		CSR1_G2_IP = (String) resourceProps.get("CSR1_G2_IP");
		CSR1_G2_MASK = "255.255.255.0";
		CSR1_AS = "64512";
		CSR1_G2_GW = "10.0.5.1";
		
		CSR2_VRF_NAME = "ftdvVRF";
		CSR2_RD = "64512:100";
		CSR2_RT = "64512:0";		
		CSR2_G2_IP = (String) resourceProps.get("CSR2_G2_IP");
		CSR2_G2_MASK = "255.255.255.0";
		CSR2_AS = "64512";
		CSR2_G2_GW = "10.0.6.1";
		 

		FTDV_AS = "65001";
		FTDV_IP = (String) resourceProps.get("FTDV_G2_IP");
		FTDV_SUBNET = "10.0.4.0";
		FTDV_SUBNET_MASK = "255.255.255.0";		
		
		Utils.log("CSR1_G2_IP:"+CSR1_G2_IP);
		Utils.log("CSR2_G2_IP:"+CSR2_G2_IP);
		Utils.log("FTDV_G2_IP:"+FTDV_IP);
		 
	}
	
	public static void setENVTransit(){
		Regions regions = Regions.fromName(System.getenv("AWS_REGION"));
		region = Region.getRegion(regions);
		
		S3_TRANSIT_BUCKET_NAME = System.getenv(S3_TRANSIT_BUCKET_NAME_KEY);
		S3_TRANSIT_BUCKET_PREFIX = System.getenv(S3_TRANSIT_BUCKET_PREFIX_KEY);
		S3_TRANSIT_CONFIG_FILE_NAME = System.getenv(S3_TRANSIT_CONFIG_FILE_NAME_KEY);
		
		S3_FTDV_BUCKET_NAME = System.getenv("S3_FTDV_BUCKET_NAME");
		S3_FTDV_KEY = System.getenv("S3_FTDV_FILENAME");
		
		
	}
	
	public static void pushCSR1Configuration(){
		csr1.setUsername(USER_NAME);
		csr1.setIp(CSR1_IP);
		csr1.setKeyPath("/tmp/"+PRIVATE_KEY);
		
		PushConfig.sendConfig(csr1);
	}
	
	public static void pushCSR2Configuration(){
		csr2.setUsername(USER_NAME);
		csr2.setIp(CSR2_IP);
		csr2.setKeyPath("/tmp/"+PRIVATE_KEY);
		
		PushConfig.sendConfig(csr2);
	}
	
	public static String getTransitVPCConfig() throws IOException{
		
		String configs = getS3Config(S3_TRANSIT_BUCKET_NAME, S3_TRANSIT_BUCKET_PREFIX+S3_TRANSIT_CONFIG_FILE_NAME);
		splitTransitVPCConfigs(configs);
		
		Utils.log("TransitVPCConfigurations: " + configs);
		return configs;
	}
	
	public static String getFTDvConfig() throws IOException{
		String configs = getS3Config(S3_FTDV_BUCKET_NAME, S3_FTDV_KEY);
		
		readFTDvJson(configs);
		
		Utils.log("FTDvConfigurations: " + configs);
		return configs;
	}
	
	public static String getS3Config(String buketName, String key) throws IOException{
		
		/*AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (/Users/fan/.aws/credentials), and is in valid format.",
                    e);
        }*/

		
		AmazonS3 s3Client = new AmazonS3Client();
		//Region usEast1 = Region.getRegion(Regions.US_EAST_1);

		s3Client.setRegion(region);
		
		S3Object object = s3Client.getObject(buketName, key);
		
		Utils.log("Content-Type: "  + object.getObjectMetadata().getContentType());
        String configs = displayTextInputStream(object.getObjectContent());
        
		return configs;
	}
	
	 private static String displayTextInputStream(InputStream input) throws IOException {
		 String data = "";
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        String line = "";
	        while (line != null) {
	        	line = reader.readLine();
	        	data = data+line;
	        	Utils.log("    " + line);
	            break;
	        }
	        return data;
	    }
	 
	 public static void uploadS3File(String bucketName, String key){
		 String content = generateFTDvJson();
		 String fileName = "ftdv";
		 String fileSuffix = ".conf";
		 
		 /*AWSCredentials credentials = null;
	        try {
	            credentials = new ProfileCredentialsProvider("default").getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " +
	                    "Please make sure that your credentials file is at the correct " +
	                    "location (/Users/fan/.aws/credentials), and is in valid format.",
	                    e);
	        }*/

	        //AmazonS3 s3 = new AmazonS3Client(credentials);
	        //Region usEAST1 = Region.getRegion(Regions.US_EAST_1);
	        // s3.setRegion(usEAST1);
	        
	        AmazonS3 s3 = new AmazonS3Client();

	        s3.setRegion(region);
	        
	        try{
	        	//s3.deleteBucket(bucketName);
	        	
	        	//s3.createBucket(bucketName);
	        	List<Bucket> bList = s3.listBuckets();
	        	boolean existed = false;
	        	for (Bucket b : bList){
	        		if (b.getName().equalsIgnoreCase(bucketName))
	        			existed = true;
	        	}
	        	
	        	if (!existed) s3.createBucket(bucketName);
	        	
	        	s3.putObject(new PutObjectRequest(bucketName, key, createSampleFile(fileName, fileSuffix, content)));

	        }
	        catch (Exception e){
	        	e.printStackTrace();
	        }
	 }
	 
	 private static File createSampleFile(String filename, String suffix, String content) throws IOException {
	        File file = File.createTempFile(filename, suffix);
	        file.deleteOnExit();

	        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
	        writer.write(content);
	        writer.close();

	        return file;
	    }
	 
	 private static void splitTransitVPCConfigs(String configs){
		 
		 Utils.log("inside of Lambda!!");
		 
		 String json = "["+configs+"]";
		 JSONArray obj = new JSONArray(json);
		 json = json.replace("'", "\"");
		 Utils.log(json);
		 Utils.log(obj.length());
		 for(int i = 0; i < obj.length(); i++){
			 Utils.log(obj.getJSONObject(i));
		 }
		 
		 configs.trim(); // take out  leading and trailing whitespace
		 configs = configs.replace("'", "");
		 Utils.log(configs);
		 configs = configs.substring(1, configs.length()-1); // take out {}
		 
		 String[] dataArray = configs.split(",");
		 for (String data: dataArray){
			 String object[] = data.split(":");
			 if(object[0].equalsIgnoreCase(USER_NAME_KEY)) 
			 	USER_NAME = object[1];
			 if(object[0].equalsIgnoreCase(PRIVATE_KEY_KEY)) 
				 PRIVATE_KEY = object[1];
			 if(object[0].equalsIgnoreCase(PUBLIC_KEY_KEY)) 
				 PUBLIC_KEY = object[1];
			 if(object[0].equalsIgnoreCase(PASSWORD_KEY)) 
				 PASSWORD = object[1];
			 if(object[0].equalsIgnoreCase(CSR1_IP_KEY)) 
				 CSR1_IP = object[1];
			 if(object[0].equalsIgnoreCase(CSR2_IP_KEY)) 
				 CSR2_IP = object[1];
		 }
		 
		 Utils.log(USER_NAME);
		 Utils.log(PRIVATE_KEY);
		 Utils.log(PUBLIC_KEY);
		 Utils.log(PASSWORD);
		 Utils.log(CSR1_IP);
		 Utils.log(CSR2_IP);
	 }
	 
	 public static void readFTDvJson(String s){
		 JSONObject rootObj = new JSONObject(s);
		 JSONObject csr1Obj = rootObj.getJSONObject(CSR1_KEY);
		 JSONObject csr2Obj = rootObj.getJSONObject(CSR2_KEY);
		 JSONObject ftdvObj = rootObj.getJSONObject(FTDV_KEY);
		 
		 CSR1_VRF_NAME = csr1Obj.getString(VRF_NAME_KEY);
		 CSR1_RD = csr1Obj.getString(RD_KEY);
		 CSR1_RT = csr1Obj.getString(RT_KEY);
		 CSR1_G2_IP = csr1Obj.getString(G2_IP_KEY);
		 CSR1_G2_MASK = csr1Obj.getString(G2_MASK_KEY);
		 CSR1_AS = csr1Obj.getString(AS_KEY);
		 CSR1_G2_GW = csr1Obj.getString(G2_GW_KEY);
		 
		 CSR2_VRF_NAME = csr2Obj.getString(VRF_NAME_KEY);
		 CSR2_RD = csr2Obj.getString(RD_KEY);
		 CSR2_RT = csr2Obj.getString(RT_KEY);
		 CSR2_G2_IP = csr2Obj.getString(G2_IP_KEY);
		 CSR2_G2_MASK = csr2Obj.getString(G2_MASK_KEY);
		 CSR2_AS = csr2Obj.getString(AS_KEY);
		 CSR2_G2_GW = csr2Obj.getString(G2_GW_KEY);
		 
		 FTDV_AS = ftdvObj.getString(FTDV_AS_KEY);
		 FTDV_IP = ftdvObj.getString(FTDV_IP_KEY);
		 FTDV_SUBNET = ftdvObj.getString(FTDV_SUBNET_KEY);
		 FTDV_SUBNET_MASK = ftdvObj.getString(FTDV_SUBNET_MASK_KEY);
		 
		 csr1.setVrfName(CSR1_VRF_NAME);
		 csr1.setRd(CSR1_RD);
		 csr1.setRt(CSR1_RT);
		 csr1.setG2IP(CSR1_G2_IP);
		 csr1.setG2Mask(CSR1_G2_MASK);
		 csr1.setCsrBGPAS(Integer.valueOf(CSR1_AS));
		 csr1.setFtdvBGPAS(Integer.valueOf(FTDV_AS));
		 csr1.setFtdvIP(FTDV_IP);
		 csr1.setFtdvSubnet(FTDV_SUBNET);
		 csr1.setFtdvSubnetMask(FTDV_SUBNET_MASK);
		 csr1.setG2Gateway(CSR1_G2_GW);
		 
		 csr2.setVrfName(CSR2_VRF_NAME);
		 csr2.setRd(CSR2_RD);
		 csr2.setRt(CSR2_RT);
		 csr2.setG2IP(CSR2_G2_IP);
		 csr2.setG2Mask(CSR2_G2_MASK);
		 csr2.setCsrBGPAS(Integer.valueOf(CSR2_AS));
		 csr2.setFtdvBGPAS(Integer.valueOf(FTDV_AS));
		 csr2.setFtdvIP(FTDV_IP);
		 csr2.setFtdvSubnet(FTDV_SUBNET);
		 csr2.setFtdvSubnetMask(FTDV_SUBNET_MASK);
		 csr2.setG2Gateway(CSR2_G2_GW);
		 
	 }
	 
	 public static String generateFTDvJson(){
		 
		 JSONObject csr1Obj = new JSONObject();
		 /*csr1Obj.put(VRF_NAME_KEY, "ftdvVRF");
		 csr1Obj.put(RD_KEY, "64512:100");
		 csr1Obj.put(RT_KEY, "64512:0");
		 csr1Obj.put(G2_IP_KEY, "10.0.5.5");
		 csr1Obj.put(G2_MASK_KEY, "255.255.255.0");
		 csr1Obj.put(AS_KEY, "65412");
		 csr1Obj.put(G2_GW_KEY, "10.0.5.1");*/
		 
		 csr1Obj.put(VRF_NAME_KEY, CSR1_VRF_NAME);
		 csr1Obj.put(RD_KEY, CSR1_RD);
		 csr1Obj.put(RT_KEY, CSR1_RT);
		 csr1Obj.put(G2_IP_KEY, CSR1_G2_IP);
		 csr1Obj.put(G2_MASK_KEY, CSR1_G2_MASK);
		 csr1Obj.put(AS_KEY, CSR1_AS);
		 csr1Obj.put(G2_GW_KEY, CSR1_G2_GW);
		 
		 
		 JSONObject csr2Obj = new JSONObject();
		 /*csr2Obj.put(VRF_NAME_KEY, "ftdvVRF");
		 csr2Obj.put(RD_KEY, "64512:100");
		 csr2Obj.put(RT_KEY, "64512:0");
		 csr2Obj.put(G2_IP_KEY, "10.0.6.6");
		 csr2Obj.put(G2_MASK_KEY, "255.255.255.0");
		 csr2Obj.put(AS_KEY, "65412");
		 csr2Obj.put(G2_GW_KEY, "10.0.6.1");*/
		 
		 csr2Obj.put(VRF_NAME_KEY, CSR2_VRF_NAME);
		 csr2Obj.put(RD_KEY, CSR2_RD);
		 csr2Obj.put(RT_KEY, CSR2_RT);
		 csr2Obj.put(G2_IP_KEY, CSR2_G2_IP);
		 csr2Obj.put(G2_MASK_KEY, CSR2_G2_MASK);
		 csr2Obj.put(AS_KEY, CSR2_AS);
		 csr2Obj.put(G2_GW_KEY, CSR2_G2_GW);
		 
		 JSONObject ftdvObj = new JSONObject();
		 /*ftdvObj.put(FTDV_AS_KEY, "65001");
		 ftdvObj.put(FTDV_IP_KEY, "10.0.4.4");
		 ftdvObj.put(FTDV_SUBNET_KEY, "10.0.4.0");
		 ftdvObj.put(FTDV_SUBNET_MASK_KEY, "255.255.255.0");*/
		 ftdvObj.put(FTDV_AS_KEY, FTDV_AS);
		 ftdvObj.put(FTDV_IP_KEY, FTDV_IP);
		 ftdvObj.put(FTDV_SUBNET_KEY, FTDV_SUBNET);
		 ftdvObj.put(FTDV_SUBNET_MASK_KEY, FTDV_SUBNET_MASK);
		 
		 JSONObject rootObj = new JSONObject();
		 rootObj.put(CSR1_KEY, csr1Obj);
		 rootObj.put(CSR2_KEY, csr2Obj);
		 rootObj.put(FTDV_KEY, ftdvObj);
		 
		 
		 return rootObj.toString();
	 }
	 
	 public static void main(String[] args){
		 
		 String s = generateFTDvJson();
		 
		 readFTDvJson(s);
		 
		 //uploadS3File();
		 
		/* try{
			 Utils.getS3Config();
			 Utils.log(USER_NAME);
			 Utils.log(PRIVATE_KEY);
			 Utils.log(PUBLIC_KEY);
			 Utils.log(PASSWORD);
			 Utils.log(CSR1_IP);
			 Utils.log(CSR2_IP);
		 }
		 catch (IOException e){
			 e.printStackTrace();
		 }*/
	 }
	
}
