package com.cisco.csr;

import java.io.IOException;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

public class LambdaFunctionHandler implements RequestHandler<S3Event, Object> {

    @Override
    public Object handleRequest(S3Event input, Context context) {
    	LambdaLogger logger = context.getLogger();
    	logger.log("Input: " + input);
    	
    	Utils.logger = logger;

        String response = "";
        //PushConfig pc = new PushConfig();
		//pc.sendConfig();
        try {
        		Utils.setENVTransit();
        		Utils.getTransitVPCConfig();
        		Utils.getFTDvConfig();
        		Utils.downloadKey();
        		Utils.pushCSR1Configuration();
        		Utils.pushCSR2Configuration();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // TODO: implement your handler
        return "this is a lambda function" + response;
    }

}

