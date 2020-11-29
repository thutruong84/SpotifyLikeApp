package sla;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner; 
  
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip; 
import javax.sound.sampled.LineUnavailableException; 
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

/*
    To compile: javac SpotifyLikeApp.java
    To run: java SpotifyLikeApp
 */

// declares a class for the app
public class SpotifyLikeApp {

    // global variables for the app
    String status;
    
    static Clip audioClip;
    

    // "main" makes this class a java app that can be executed
    public static void main(final String[] args) {

        // create a scanner for user input
        Scanner input = new Scanner(System.in);

        String userInput = "";
        while (!userInput.equals("q")) {

            menu();

            // get input
            userInput = input.nextLine();

            // accept upper or lower case commands
            userInput.toLowerCase();

            // do something
            handleMenu(userInput);

        }

        // close the scanner
        input.close();

    }


    /*
     * displays the menu for the app
     */
    public static void menu() {

        System.out.println("---- SpotifyLikeApp ----");
        System.out.println("[H]ome");
        System.out.println("[S]earch by title");
        System.out.println("[L]ibrary");
        System.out.println("[F]avorites");
        System.out.println("[Q]uit");

        System.out.println("");
        System.out.print("Enter q to Quit:");

    }

    /*
     * handles the user input for the app
     */
    public static void handleMenu(String userInput) {
    
        switch(userInput) {

            case "h":
            	System.out.println("-->Home<--");
            	System.out.println("---All songs in playlist---");
            	for (String key: songJSON().keySet()) {
            		System.out.println(key);
        		}
                play();
                break;

            case "s":
                System.out.println("-->Search by title<--");
                System.out.println("Enter a title to search for your song:");
                Scanner inputS = new Scanner(System.in);
           	    String searchSong = inputS.nextLine();
           	    ArrayList<String> array = new ArrayList<String>(10);
           	    for (String key: songJSON().keySet()) {
           	    	array.add(key);
     		    }
           	    boolean ans = array.contains(searchSong);
           	    if (ans) {    
           	    	System.out.println("*** The song you are searching for is in playlist. You must enter the song name again to play it ***"); 
           	        play();
           	    }
           	        
           	    else {
                    System.out.println("### The song you are searching for is NOT in playlist. Please search again ###"); 
                    handleMenu("s");
           	    }
                
           	    break;

            case "l":
                System.out.println("-->Library<--");
                System.out.println("Enter 1 to search by released year");
                System.out.println("Enter 2 to search by genre"); 
                Scanner inputL = new Scanner(System.in);
                String searchLibrary = inputL.nextLine();
                libraryMenu(searchLibrary);
                break;

            case "f":
            	System.out.println("---All songs in your favorite song playlist---");
    			
    	        for (Map.Entry<String,List<String>> entry : songJSON().entrySet()) {
    	        	if (entry.getValue().get(4).equals("Y")) {
    	        		System.out.println(entry.getKey());
    	        	}
    	        }
    			play();
    			break;
          
            case "q":
                System.out.println("-->Quit<--");
                break;

            default:
                break;
        }

    }

   
    /*
     * plays an audio file
     */
    public static void play() {
    	 Scanner input = new Scanner(System.in);
    	 
    	 System.out.print("Enter song name to play: ");

         String songName = input.nextLine();
         
         HashSet<String> availableSong = new HashSet<String>();
         for (String key: songJSON().keySet()) {
        	 availableSong.add(key);
 		}
      
         boolean containSong = availableSong.contains(songName);
         if (containSong) {
         String songFile = songName + ".wav";
         
         
        // open the audio file
        final File file = new File(songFile);
       
        try {
        
            // create clip 
            audioClip = AudioSystem.getClip();

            // get input stream
            final AudioInputStream in = getAudioInputStream(file);

            audioClip.open(in);
            audioClip.setMicrosecondPosition(0);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            
            
            System.out.println("--> Playing: " + songName);
            List<String> wantedInfo = songJSON().get(songName);
           
            System.out.println("--> Artist: " + wantedInfo.get(0));
            System.out.println("--> Released year: " + wantedInfo.get(1));
            System.out.println("--> Genre: " + wantedInfo.get(2));
            System.out.println("--> Relative path: " + wantedInfo.get(3));
            System.out.println("--> isFavorite: " + wantedInfo.get(4));
            System.out.println("");
            
            int index = Integer.parseInt(wantedInfo.get(5));
            Scanner scanner = new Scanner(System.in);
    		
    		String response = "";
    			
    		while(!response.equals("Q")) {
    			System.out.println("---- Menu for this song ----");
    			System.out.println("[P]ause");
    			System.out.println("[S]tart playing at where you pause");
    			System.out.println("[B]egin song again (reset song to the beginning)");
    			System.out.println("[R]ewind");
    			System.out.println("[F]oward");
    			System.out.println("[Q]uit this song and return to main menu");
    			System.out.print("Enter your choice: ");
    			
    			response = scanner.next();
    			response = response.toUpperCase();
    			
    			switch(response) {
    				case ("P"): audioClip.stop();
    				break;
    				
    				case ("S"): audioClip.start();
    				break;
   
    				case ("B"): audioClip.setMicrosecondPosition(0);
    				break;
    				
    				case ("R"):
    					Long positionR = audioClip.getMicrosecondPosition();
    				    System.out.println("--------------------------------------------------");
    					System.out.println("This song was at " + positionR/1000000 + " seconds before you forward.");
    					
    					audioClip.start();
    					audioClip.setMicrosecondPosition(positionR - 5000000);
    					
    					Long currpositionR = audioClip.getMicrosecondPosition();
    					
    					System.out.println("This song is now at " + currpositionR/1000000 + " seconds after you forward.");
    					System.out.println("--------------------------------------------------");
    					break;
    					
    				
    				case ("F"):
    					Long positionF = audioClip.getMicrosecondPosition();
    				    System.out.println("--------------------------------------------------");
    					System.out.println("The song was at " + positionF/1000000 + " seconds befor you forward.");
    					
    					audioClip.start();
    					audioClip.setMicrosecondPosition(positionF + 5000000);
    					
    					Long currpositionF = audioClip.getMicrosecondPosition();
    					
    					System.out.println("The song is now at " + currpositionF/1000000 + " seconds after you forward.");
    					System.out.println("--------------------------------------------------");
    					break;
    					
    				case ("Q"): 
    					audioClip.close();
    					isFavorite(index);
    				
    				break;
    				default: System.out.println("Not a valid response");
    			}
    		 }
            
    	
        

        } catch(Exception e) {
            e.printStackTrace(); 
        }
        }
        else {
        	 System.out.println("### The song you entered is not in playlist. Please enter a different song name ###");
        	 play();
        }
         return;
        
    }
    
    static Map<String, List<String>> songJSON () {
//Read in song.json data
    	
    	JSONParser parser = new JSONParser();
		
		JSONArray songList = null;
		
		try (FileReader reader = new FileReader("song.json"))
        {
            //Read JSON file
            Object obj = parser.parse(reader);
 
            songList = (JSONArray) obj;
 
		}
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch(IOException e) { e.printStackTrace(); }
		catch(Exception e) { e.printStackTrace(); }
		

        String name;
        String artist;
        String year;
        String genre;
        String path;
        String isFavorite;
        String index;
        
        
        JSONObject obj;
        Map<String,List<String>> playList = new HashMap<String,List<String>>();
        
        // loop over birthdayList
        for (Integer i = 0; i < songList.size() ; i++) {

            // parse the object and pull out the name and birthday
        	// store the name and birthday into HashMap called bdayList
        	List<String> info = new ArrayList<String>();
        	obj = (JSONObject) songList.get(i);
            name = (String) obj.get("name");
            artist = (String) obj.get("artist");
            year = (String) obj.get("year");
            genre = (String) obj.get("genre");
            path = (String) obj.get("path");
            isFavorite = (String) obj.get("isFavorite");
            index = (String) obj.get("index");
            info.add(artist);
            info.add(year);
            info.add(genre);
            info.add(path);
            info.add(isFavorite);
            info.add(index);
           
            playList.put(name,info);
            
          
            

        }
		return playList;
    }
    
    static void libraryMenu (String searchLibrary) {
    	switch(searchLibrary) {

    		case "1":
    			System.out.println("All available released year:");
    			HashSet<String> yearSet = new HashSet<String>();
    			
    	        for (List<String> value: songJSON().values()) {
    	        	yearSet.add(value.get(1));
    			}
    	        
    	        for (String elem: yearSet) {            
    	        	System.out.println(elem);        
    	        }
    	        System.out.println("Enter a year to display songs released in that year");
    	        Scanner input1 = new Scanner(System.in);
                String wantedYear = input1.nextLine();
                
                switch(wantedYear) {
                	case "2019":
                		System.out.println("---Songs released in 2019---");
                		System.out.println("Journey of King");
                		System.out.println("Tanzen");
                		System.out.println("Wirklich Wichtig");
                		System.out.println("Burn It Down");
                		System.out.println("Zumbido");
                		play();
                		break;
                		
                	case "2018":
                		System.out.println("---Songs released in 2018---");
                		System.out.println("Vacaciones Salsa");
                		System.out.println("Welcome");
                		play();
                		break;
                		
                	case "2017":
                		System.out.println("---Song released in 2017---");
                		System.out.println("Storybook");
                		play();
                		break;
                		
                	case "2015":
                		System.out.println("---Song released in 2015---");
                		System.out.println("El Preso Numero Nueve");
                		play();
                		break;
                		
                	case "2010":
                		System.out.println("---Song released in 2010---");
                		System.out.println("Cement Lunch");
                		play();
                		break;
                		
                	default:
                		libraryMenu("1");
                		break;
                		
            		
                }
    		break;
    		
    		case "2":
    			System.out.println("All available genres:");
    			HashSet<String> genreSet = new HashSet<String>();
    	        for (List<String> value: songJSON().values()) {
    	        	genreSet.add(value.get(2));
    			}
    	        
    	        for (String elem: genreSet) {            
    	        	System.out.println(elem);        
    	        }
    	        
    	        System.out.println("Enter a genre to display songs in that genre");
    	        Scanner input2 = new Scanner(System.in);
                String wantedGenre = input2.nextLine();
                
                switch(wantedGenre) {
                	case "Soul-RnB":
                		System.out.println("---Songs in Soul-RnB genre---");
                		System.out.println("Cement Lunch");
                		System.out.println("Tanzen");
                		System.out.println("Wirklich Wichtig");
                		play();
                		break;
                		
                	case "Electronic":
                		System.out.println("---Songs in Electronic genre---");
                		System.out.println("Journey of King");
                		System.out.println("Welcome");
                		System.out.println("Burn It Down");
                		System.out.println("Zumbido");
                		play();
                		break;
                		
                	case "Pop":
                		System.out.println("---Song in Pop genre---");
                		System.out.println("Storybook");
                		play();
                		break;
                		
                	case "Folk":
                		System.out.println("---Songs in Folk genre---");
                		System.out.println("Vacaciones Salsa");
                		System.out.println("El Preso Numero Nueve");
                		play();
                		break;
                		
                	default:
                		libraryMenu("2");
                		break;
                }
    		break;
    		
    		default:
    			handleMenu("l");
        		break;
    	}
    }
    
    public static void isFavorite(Integer index)  {
   	 Scanner input = new Scanner(System.in);
   	 
   	 System.out.print("Is this song your favorite? (Y/N)");

     String response = input.nextLine();
     response.toUpperCase();
     
     switch (response) {
     	case "Y": 
     	// reads a json data file
            
            JSONParser parser = new JSONParser();
    		
            JSONArray songInfoList = null;
    		
    		try (FileReader reader = new FileReader("song.json"))
            {
                //Read JSON file
    			 Object obj = parser.parse(reader);
    			 
    			 songInfoList = (JSONArray) obj;
    	         JSONObject wantedSong = new JSONObject();
    	         wantedSong = (JSONObject) songInfoList.get(index);
    	         wantedSong.put("isFavorite","Y");
    	 		
    	        
    	         FileOutputStream outputStream = new FileOutputStream("song.json"); 
    	         byte[] strToBytes = songInfoList.toString().getBytes(); 
    	         outputStream.write(strToBytes);
                
                
     
    		}
    		catch (FileNotFoundException e) { e.printStackTrace(); }
    		catch(IOException e) { e.printStackTrace(); }
    		catch(Exception e) { e.printStackTrace(); }
    		break;
     	
     	case "N":
     		
     		System.out.println("### This song is now updated as NOT being in your favorite song playlist ###.");
     		break;
     		default: System.out.println("Not a valid response");
    		
    		
    		
     		
    	 	
    }
    }

}

