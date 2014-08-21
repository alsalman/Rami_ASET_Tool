package jo.edu.just.XML;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import java.util.Properties;
import javax.xml.transform.OutputKeys;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;

import javax.xml.transform.dom.DOMSource;

import jo.edu.just.Flags;
import jo.edu.just.MainContainer;
import jo.edu.just.sharedSettings;
import jo.edu.just.Shapes.JoPoint;
import jo.edu.just.Shapes.joBitmap;
import jo.edu.just.Shapes.joPath;



public class writer {

	public String LastError= "";
	
	
	public String SendDataToServer(joBitmap joBmp){
		boolean SecondVisited = false;
		String WebRequest = "http://aset.informatik.uni-bremen.de/rec.php/?fObj=";
		
		//if (!joBmp.InEditMode ) return "you must select two objects";
		//if (joBmp.tempLayer.size() <2 ) return "you must select two objects";
		if (joBmp.content.size() < 2 ){
			LastError = "there is no drown objects to send the query";
			return "error";
		}
		if ( ((joPath)joBmp.content.get(0)).GetAnnotationString() == null ||
			 ((joPath)joBmp.content.get(1)).GetAnnotationString() == null ){
			LastError = "One or more of the objects has no Annotation";
			return "error";
		}
		if ( ((joPath)joBmp.content.get(0)).GetAnnotationString().compareTo("") == 0||
			 ((joPath)joBmp.content.get(1)).GetAnnotationString().compareTo("") == 0){
				LastError = "One or more of the objects has no Annotation";
				return "error";
			}
		
		
		WebRequest += ((joPath)joBmp.content.get(0)).GetAnnotationString()  + "&sObj=" + ((joPath)joBmp.content.get(1)).GetAnnotationString() + "&fgeom=";
		
		JoPoint   tmpFirstPoint = null;
		JoPoint tmpPT;
		//-----------------
		for(int i=0 ; i < 2;i++){
			if ( i == 1 && !SecondVisited ){ WebRequest += "&sgeom="; SecondVisited = true ;}
			float ct[] = ((joPath)joBmp.content.get(i)).GetCenterPoint();
			JoPoint CenterPoint = new JoPoint(ct[0], ct[1]);
 	   
			for(int j = 0 ; j < ((joPath)joBmp.content.get(i)).PointsCount();j++){
				 tmpPT = 
		 				   ((joPath)joBmp.content.get(i))
		 				   .GetTrasformedPoint(CenterPoint,
		 						   			 (( joPath)joBmp.content.get(i)).getPoint(j), 
		 						   			    joBmp.width, 
		 						   			    joBmp.height,
		 						   			    false);
				if (j==0 ){
					tmpFirstPoint = tmpPT;
				}
				if (j == ((joPath)joBmp.content.get(i)).PointsCount() -1) tmpPT = tmpFirstPoint;
 		   
				WebRequest += String.valueOf((int)tmpPT.x) + "%20" + String.valueOf((int) tmpPT.y) ;
				
				if (j+1  !=((joPath)joBmp.content.get(i)).PointsCount() ) WebRequest += "," ;
			}
		}
		//-----------------
		
		//if (WebRequest != null)
		//return  WebRequest;
		
		if (MainContainer.ModeOfServerUse == Flags.SERVER_MODE_SQL)  WebRequest += "&type=sql"  ;
		if (MainContainer.ModeOfServerUse == Flags.SERVER_MODE_QHTC){
			if (sharedSettings.QHTC_SERVER_REQUEST_TRANSITION_MODE == Flags.QHTC_HASH_REQUEST_MODE)
				WebRequest += "&type=QHTC_hash";
			if (sharedSettings.QHTC_SERVER_REQUEST_TRANSITION_MODE == Flags.QHTC_NORMAL_REQUEST_MODE)
				WebRequest += "&type=QHTC";
			
			
			
			
		}
		
		//log.("query to server",WebRequest);
		
		HttpResponse response = null;
		InputStream inputStream;
		try {        
		        HttpClient client = new DefaultHttpClient();
		        HttpGet request = new HttpGet();
		        request.setURI(new URI(WebRequest));
		        response = client.execute(request);
		        inputStream =  response.getEntity().getContent();
		    } catch (URISyntaxException e) {
		    	LastError = "connection Error!";
		    	return  "error";
		    } catch (ClientProtocolException e) {
		    	LastError = "connection Error!";
		    	return  "error";
		    } catch (IOException e) {
		    	LastError = "connection Error!";
		    	return  "error";
		    }   
		
		if (response == null || inputStream == null)  {
			LastError = "the app  got empty respone from the server";
			return "error";
		}
		
		 Writer writer = new StringWriter();

         char[] buffer = new char[2048];
         try {
             Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),2048);
             int n;
             while ((n = reader.read(buffer)) != -1) {
                 writer.write(buffer, 0, n);
             }
         }catch (Exception e){
        	LastError =  "the server respondes with currupted data";
        	return "error";
         }
         
         finally {
             try {
				inputStream.close();
			} catch (IOException e) {
				LastError = "the server sends currupted data";
				return "error";
			}
         }
         
         //log.("response from server is",writer.toString());
       // //log.("Last error from server",LastError);
         return writer.toString() + "\n" ;//+ WebRequest;
	}
	
	
	
	
	public String SendGeneralRequest(String RequestLine){
		//boolean SecondVisited = false;
		
		
	
		
		HttpResponse response = null;
		InputStream inputStream;
		try {        
		        HttpClient client = new DefaultHttpClient();
		        HttpGet request = new HttpGet();
		        request.setURI(new URI(RequestLine));
		        response = client.execute(request);
		        inputStream =  response.getEntity().getContent();
		    } catch (URISyntaxException e) {
		    	LastError = "connection Error!";
		    	return  "error";
		    } catch (ClientProtocolException e) {
		    	LastError = "connection Error!";
		    	return  "error";
		    } catch (IOException e) {
		    	LastError = "connection Error!";
		    	return  "error";
		    }   
		
		if (response == null || inputStream == null)  {
			LastError = "the app  got empty respone from the server";
			return "error";
		}
		
		 Writer writer = new StringWriter();

		 
		 //char[] buffer = new char [(int) response.getEntity().getContentLength()];
         char[] buffer = new char[1024];
         try {
             Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
             int n;
             while ((n = reader.read(buffer)) != -1) {
                 writer.write(buffer, 0, n);
             }
         }catch (Exception e){
        	LastError =  "the server respondes with currupted data";
        	return "error";
         }
         
         finally {
             try {
				inputStream.close();
			} catch (IOException e) {
				LastError = "the server sends currupted data";
				return "error";
			}
         }
         
         //log.("response from server is",writer.toString());
       // Log.d("Last error from server",LastError);
         return writer.toString() + "\n" ;//+ WebRequest;
	}
	
	
	public String GenerateXML (joBitmap joBmp,boolean UsingTimeStamps){
		String xmlString="" ; 
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder documentBuilder;
         Document document;
    try {
		 documentBuilder = documentBuilderFactory.newDocumentBuilder();
		 document = documentBuilder.newDocument();
        
        

		 Element rootElement = document.createElement("AppData");
		 rootElement.setAttribute("Version", "1.0");
		 rootElement.setAttribute("UsingTimeStamps", String.valueOf(UsingTimeStamps).toUpperCase());
		 rootElement.setAttribute("NumberOfObjects", String.valueOf(joBmp.content.size()));
		 rootElement.setAttribute("BackGroundColor", String.valueOf( joBmp.GetBackColor() ));
		 document.appendChild(rootElement);
   
       
       for(int i=0 ; i < joBmp.content.size();i++){
       
    	   Element articleElement = document.createElement("Object");
    	   articleElement.setAttribute("ID",String.valueOf(i+1)  );
    	   articleElement.setAttribute("Name","NULL");
    	   articleElement.setAttribute("Type","NULL");
    	   
    	   if(UsingTimeStamps){
    	   articleElement.setAttribute("CreationTimeStamp",joBmp.content.get(i).CreateTime.format("%Y-%m-%dT%H-%M-%S"));
    	   articleElement.setAttribute("FinalizationTimeStamp",joBmp.content.get(i).FinalizeTime.format("%Y-%m-%dT%H-%M-%S"));
    	   }
    	   
    	   
    	   float ct[] = ((joPath)joBmp.content.get(i)).GetCenterPoint();
    	   JoPoint CenterPoint = new JoPoint(ct[0], ct[1]);
    	   
    	   for(int j = 0 ; j < ((joPath)joBmp.content.get(i)).PointsCount();j++){
    		   Element ptML = document.createElement("Point");
    		   JoPoint tmpPT = 
    				   ((joPath)joBmp.content.get(i))
    				   .GetTrasformedPoint(CenterPoint,
    						   			 (( joPath)joBmp.content.get(i)).getPoint(j), 
    						   			    joBmp.width, 
    						   			    joBmp.height,
    						   			    false);
    		   
    		   ptML.setAttribute("X",String.valueOf(	tmpPT.x ));
    		   ptML.setAttribute("Y",String.valueOf(	tmpPT.y ));
    		   
    		   articleElement.appendChild(ptML);
    		   
    	   }
    	   rootElement.appendChild(articleElement);
       }
    	   
    	   TransformerFactory factory = TransformerFactory.newInstance();
           Transformer transformer = factory.newTransformer();
           Properties outFormat = new Properties();
           outFormat.setProperty(OutputKeys.INDENT, "yes");
           outFormat.setProperty(OutputKeys.METHOD, "xml");
           outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
           outFormat.setProperty(OutputKeys.VERSION, "1.0");
           outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
           transformer.setOutputProperties(outFormat);
           DOMSource domSource = 
           new DOMSource(document.getDocumentElement());
           OutputStream output = new ByteArrayOutputStream();
           StreamResult result = new StreamResult(output);
           transformer.transform(domSource, result);
           xmlString = output.toString();
           
           
           
           //log.("savenfo", xmlString);
           
    		} catch (ParserConfigurationException e) 	  {return "";
    		} catch (TransformerConfigurationException e) {return "";
    		} catch (TransformerException e) 			  {return "";
       		} catch (Exception e)						  {return "";
       		}											   return xmlString;
	}
	
	
	
	
	
}
