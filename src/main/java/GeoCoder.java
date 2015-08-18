import java.io.*;

import java.net.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeoCoder {
	Double[] coord = new Double[2];
	String zip ="";
	String url="";
	String callStatus="";
	
	public GeoCoder(String address_tab_city_tab_state, int addressIndex, int placeIndex, int stateIndex, int zipIndex, String splitOn) throws IOException, InterruptedException{
		gmapsGeocodetoLatLng codedAddress;
		String[] split = address_tab_city_tab_state.split(splitOn);
		String strAddress = split[addressIndex].replace(" ","+")+",+"+split[placeIndex].replace(" ", "+")+",+"+split[stateIndex];
		zip = split[zipIndex].substring(0, 5);
		url = "https://maps.googleapis.com/maps/api/geocode/json?address="+strAddress+"&components=postal_code:"+zip+"&key=google_maps_key";
		System.out.println(url);
		URLConnectionReader myHttpReq = new URLConnectionReader(url);
		codedAddress = new gmapsGeocodetoLatLng(myHttpReq.result(),zip);
		//if initial response is not OK
		if(!codedAddress.callStatus().equalsIgnoreCase("OK")){
			System.out.println("NOT OK RESPONSE");
			url = "https://maps.googleapis.com/maps/api/geocode/json?address="+strAddress+"&key=google_maps_key";
			myHttpReq = new URLConnectionReader(url);
			codedAddress = new gmapsGeocodetoLatLng(myHttpReq.result(),zip);
		}
		coord[0]=codedAddress.result()[0];
		coord[1]=codedAddress.result()[1];

	}
	public Double[] getCoord(){
		return coord;
	}
	public String getZip(){
		return zip;
	}
	
	public String getUrl(){
		return url;
	}
	public String getApiStatus(){
		return callStatus;
	}
}


class URLConnectionReader {
	String output = "";
	
    public URLConnectionReader(String url) throws IOException{
        URL httpReq = new URL(url);
        URLConnection con = httpReq.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
        	{output+=inputLine;}
        in.close();
    }
    public String result(){
    	return output;
    }
}
    
class gmapsGeocodetoLatLng{
	Double[] coordinates = new Double[2];
	String status="test";
	boolean zipSearch=true;
	String zip="";
	public gmapsGeocodetoLatLng(){
		
	}
	
	public gmapsGeocodetoLatLng(String input, String zip){
		JSONObject jsonObject = new JSONObject(input);
		JSONObject geometry = null;
		status =  jsonObject.getString("status");
		
		if(jsonObject.getString("status").equalsIgnoreCase("Ok")){
			JSONArray results = (JSONArray) jsonObject.get("results");
			boolean match=false;
			if(zipSearch){
				for(int x=0;x<results.length()&&match==false;x++){
					JSONObject jsonIterator = (JSONObject) results.get(x);
					JSONArray addressComp = (JSONArray) jsonIterator.get("address_components");
					int zipIndex = getJSONZipIndex(addressComp);
					JSONObject zipCell = (JSONObject) addressComp.get(zipIndex);
					String zipCheck = zipCell.getString("short_name");
					if(zipCheck.equalsIgnoreCase(zip)){
						geometry = jsonIterator;
						match=true;
					}
				}
			}else
			{geometry = (JSONObject) results.get(0);}
			if(match==true){
			JSONObject location = (JSONObject) geometry.get("geometry");
			JSONObject viewport = (JSONObject) location.get("location");
			coordinates[0]= viewport.getDouble("lat");
			coordinates[1]= viewport.getDouble("lng");
			}
		}else{System.out.println("Problem with "+input);}
	}
	
	public int getJSONZipIndex(JSONArray input){
		int result=0;
		boolean found = false;
		for(int x=0;x<input.length() && found==false;x++){
			JSONObject currObject=(JSONObject) input.get(x);
			JSONArray currType = (JSONArray) currObject.get("types");
			if(currType.length()>0)
					{
				String type = currType.getString(0);
				if(type.equalsIgnoreCase("postal_code")){
					result=x;
					found=true;
				}
			}
		}
		return result;
	}
	
	public void writeResultToFile(WriteFile dest) throws IOException{
		dest.writeToFile(coordinates[0]+" \t "+coordinates[1]);
	}

	public void disableZipSearch(){
		zipSearch=false;
	}
	public Double[] result(){
		return coordinates;
	}
	
	public String callStatus(){
		return status;
	}
}

class coordDistance{
	//Double[0]=lat
	//Double[1]=lng
	Double[] origin=new Double[2];
	Double[] target=new Double[2];
	public coordDistance(Double[] from, Double[] to){
		System.out.println(from[0]+":"+from[1]);
		System.out.println(to[0]+":"+to[1]);
		
		origin=from;
		target=to;
	}
	
	public coordDistance(Double[] from){
		origin=from;
	}
	public coordDistance(Double lat, Double lng){
		origin[0]=lat;
		origin[1]=lng;
	}
	
	public void setTarget(Double[] to){
		target=to;
	}
	public void setTarget(Double lat, Double lng){
		target[0]=lat;
		target[1]=lng;
	}
	
	public Double getDistance(){
		Double distance= distance(origin,target,'M');
		return distance;
	}
	
	private Double distance(Double[] from, Double[] to, char unit) {
		  Double theta = from[1] - to[1];
		  Double dist = Math.sin(deg2rad(from[0])) * Math.sin(deg2rad(to[0])) + Math.cos(deg2rad(from[0])) * Math.cos(deg2rad(to[0])) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  if (unit == 'K') {
		    dist = dist * 1.609344;
		  } else if (unit == 'N') {
		  	dist = dist * 0.8684;
		    }
		  return (dist);
		}
	 
	private Double rad2deg(Double rad) {  
	  return (rad * 180 / Math.PI); 
	} 
	
	private Double deg2rad(Double deg) { 
		return (deg * Math.PI / 180.0);  
		} 
}
