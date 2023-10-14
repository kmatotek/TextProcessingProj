import java.util.Map;
public class Email {
    public int emailID;
    public String rawText;
    public Map<String,Integer> features;
    private boolean actualSpam;
    
    public Email(int id, String rawtex, Map<String,Integer> feature, boolean actualspam){ //Constructor to set the Email class variables to the variables passed in to Create an Email object
       emailID = id;
       rawText = rawtex;
       features = feature;
       actualSpam = actualspam;

    }

    public int getEmailID(){
        return this.emailID;
    }
    public String getRawText(){
        return this.rawText;
    }
    public Map<String,Integer> getFeatures(){
        return this.features;
    }

    public String toString(){
        return "Email ID# " + emailID + " - "+ rawText + " Features: " + features;
    }
    public boolean isActualSpam(){
        return this.actualSpam;
    }
}
