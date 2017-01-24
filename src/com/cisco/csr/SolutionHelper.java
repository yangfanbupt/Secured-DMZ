package com.cisco.csr;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.util.EC2MetadataUtils;

public class SolutionHelper implements RequestHandler<Map<String,Object>, Object> {

    @Override
    public Object handleRequest(Map<String,Object> input, Context context) {
    	LambdaLogger logger = context.getLogger();
    	logger.log("Input: " + input);
    	
    	Utils.logger = logger;

    	JSONObject response = new JSONObject();
        Map<String,Object> resourceProps = (Map<String, Object>) input.get("ResourceProperties");
        
        
        String responseURL = (String) input.get("ResponseURL");
        
        String requestType = (String) input.get("RequestType");
        Utils.log("ResponseURLInput: " + responseURL);
        Utils.log("RequestType: " + requestType);
        
        if(requestType.equalsIgnoreCase("Delete")){
        	//do nothing
        }else if(requestType.equalsIgnoreCase("Update")){
        	//do nothing
        }else if(requestType.equalsIgnoreCase("Create")){
        	if (resourceProps.containsKey("StoreMetaToS3")){
    			storeMetaToS3(resourceProps);
    		}
    		else if (resourceProps.containsKey("GetAZfromCSR")){
    			response = getAZfromCSR(resourceProps);
    		}
        }
		
		
		URL url;
		try {
			url = new URL(responseURL);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
		    connection.setDoOutput(true);
		    connection.setRequestMethod("PUT");
		    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		    JSONObject cloudFormationJsonResponse = new JSONObject();
		    
		    
		    cloudFormationJsonResponse.put("Status", "SUCCESS");
            cloudFormationJsonResponse.put("PhysicalResourceId", context.getLogStreamName());
            cloudFormationJsonResponse.put("StackId", input.get("StackId"));
            cloudFormationJsonResponse.put("RequestId", input.get("RequestId"));
            cloudFormationJsonResponse.put("LogicalResourceId", input.get("LogicalResourceId"));
            cloudFormationJsonResponse.put("Data", response);
            
            out.write(cloudFormationJsonResponse.toString());
            out.close();
            int responseCode = connection.getResponseCode();
            context.getLogger().log("Response Code: " + responseCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        // TODO: implement your handler
        return null;
    }
    
    private void storeMetaToS3(Map<String,Object> resourceProps){
    	 Utils.log("creating "+resourceProps.get("S3_FTDV_FILENAME"));
		 Utils.setENVFTDv(resourceProps);
		 String s = Utils.generateFTDvJson();
		 Utils.uploadS3File(resourceProps.get("BucketName").toString(), resourceProps.get("S3_FTDV_FILENAME").toString());
    }
    
    private JSONObject getAZfromCSR(Map<String,Object> resourceProps){
    	JSONObject js = new JSONObject();
    	
    	List<String> instanceIDs = new LinkedList<String>();
    	instanceIDs.add(resourceProps.get("CSR1ID").toString());
    	instanceIDs.add(resourceProps.get("CSR2ID").toString());
    	
    	AmazonEC2Client ec2Client = new AmazonEC2Client();
    	Regions regions = Regions.fromName(System.getenv("AWS_REGION"));
    	Region region = Region.getRegion(regions);
    	ec2Client.setRegion(region);
    	
    	DescribeInstancesRequest request =  new DescribeInstancesRequest();
        request.setInstanceIds(instanceIDs);
        
        DescribeInstancesResult result = ec2Client.describeInstances(request);
        List<Reservation> reservations = result.getReservations();
        
        for(Reservation res : reservations){
        	List<Instance> instances = res.getInstances();
            for(Instance ins : instances){
                Utils.log("Availability Zone  " + ins.getInstanceId() + " is " + ins.getPlacement().getAvailabilityZone());
                if(ins.getInstanceId().equals(resourceProps.get("CSR1ID").toString())) js.put("CSR1AZ", ins.getPlacement().getAvailabilityZone());
                if(ins.getInstanceId().equals(resourceProps.get("CSR2ID").toString())) js.put("CSR2AZ", ins.getPlacement().getAvailabilityZone());
            }
        }
        
    	return js;
    }
    

}



