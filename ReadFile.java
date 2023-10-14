import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.io.File;

public class ReadFile {
    public static ArrayList<Email> fullEmailList = new ArrayList<Email>();
    public static String rawText;
    public static Map<String, Integer> features;
    public int wordCount;
    public int urlCount;
    public int numberCount;
    public int iCount;
    public int hyperlinkCount;
    public int youCount;
    public static int idCounter;
    public static ArrayList<Email> spamEmails;
    public static ArrayList<Email> notSpamEmails;
    private static boolean predictionCorrect;
    public static final double totalSpamEmails = 500; 
    public static final double totalNotSpamEmails = 2500; 
    public static double spamPredictionAccuracy = 0;
    public static double notSpamPredictionAccuracy = 0;

    public static void main(String[] args) throws FileNotFoundException {
        ReadFile readFile = new ReadFile();
        //EmailClassifierGUI gui = new EmailClassifierGUI(readFile);
        notSpamEmails = getEmailsWithID(1, 2500); // Initialize the notSpamEmails list
        spamEmails = getEmailsWithID(2501, 3000); // Initialize SpamEmail list
      
        try {
        FileWriter writer = new FileWriter("SPAM_DATA.csv"); //Creating output text file
        double correctlyPredictedSpam = 0;
        double correctlyPredictedNotSpam = 0;
        for (Email email : fullEmailList) {
            boolean predictedSpam = isSpamUsingNearestNeighbors(email, 2);
            boolean actualSpam = email.isActualSpam();
            
            if(predictedSpam == actualSpam){ 
                predictionCorrect = true;
            }
            else predictionCorrect = false;
            writer.write("Email ID: " + email.getEmailID() + ", Predicted isSpam: " + predictedSpam + ", Actual iSpam: " + actualSpam + ", predicted correctly? " + predictionCorrect + "\n");
            if (actualSpam) { //Getting accuracy for predictions on a spam Email
                if (predictedSpam == actualSpam) {
                    correctlyPredictedSpam++;
                }
            } 
            else { //Getting accuracy for predictions on a not spam email
                if (predictedSpam == actualSpam) {
                    correctlyPredictedNotSpam++;
                }
            }
        }
        spamPredictionAccuracy = (correctlyPredictedSpam / totalSpamEmails)*100; //Calculate predicitons and multiiply by 100 to show %
        notSpamPredictionAccuracy = (correctlyPredictedNotSpam / totalNotSpamEmails)*100;
        double a = Math.round(spamPredictionAccuracy * 100.0) / 100.0;
        double b = Math.round(notSpamPredictionAccuracy * 100.0) / 100.0;
        writer.write("Spam prediction Accuracy: " + a + "%, Not spam prediction accuracy: "+b+"%");
        writer.close();
    } catch (IOException e) {
        System.out.println("An error occurred.");
    }
}

    public static void readAllEmails() throws FileNotFoundException { //Function to read csv file of emails
        Scanner input = new Scanner(new File("spam_or_not_spam.csv"));
        idCounter = 1;
        if (input.hasNextLine()) { //Skipping through first line
            input.nextLine();
        }
    while (input.hasNextLine()) {
        String line = input.nextLine();
        String[] parts = line.split(","); // Splitting the line by comma
        String rawText = parts[0]; // Extracting the raw text
        int label = Integer.parseInt(parts[1]); // Extracting the label

        boolean isActualSpam = (label == 1); // Setting isActualSpam based on label

        ReadFile current = new ReadFile(rawText); // Creating a ReadFile instance
        current.getFeatures(); // Calling the getFeatures method

        Email currentEmail = new Email(idCounter, rawText, features, isActualSpam); // Creating Email object
        fullEmailList.add(currentEmail); // Adding the email to the list

        idCounter++;
    }
    input.close();
}

    public ReadFile() { //Reading all emails through constructor
        idCounter = 1;
        try {
            readAllEmails();
        } catch (FileNotFoundException e) {
            System.out.println("Error");
        } 
        notSpamEmails = getEmailsWithID(1, 2500); // Initialize the notSpamEmails list
        spamEmails = getEmailsWithID(2501, 3000);
    }
   
    public ReadFile(String rawTex) {
        rawText = rawTex;
        features = new HashMap<>();
         
    }

    public String getRawText() {
        return rawText;
    }

    public void setFeatures(Map<String, Integer> feature) {
        features = feature;
    }

    public void getFeatures() { //Iterating through line of each email, finding they're counts
        String[] words = rawText.split("\\s+");

        for (String word : words) {
            this.wordCount++;
            if (word.equals("URL")) this.urlCount++;
            if (word.equals("NUMBER")) this.numberCount++;
            if (word.equals("i")) this.iCount++;
            if (word.equals("hyperlink")) this.hyperlinkCount++;
            if (word.equals("you")) this.youCount++;
        }
        
        features.put("You Count", youCount);
        features.put("Hyperlink Count", hyperlinkCount);
        features.put("i Count", iCount);
        features.put("Number Count", numberCount);
        features.put("Url Count", urlCount);
        features.put("Word Count",wordCount);
        
    }

    public static ArrayList<Email> getEmailsWithID(int startID, int endID) { //Returning an ArrayList of the Emails specified
        ArrayList<Email> emailsInRange = new ArrayList<Email>();
        for (Email email : fullEmailList) {
            int emailID = email.getEmailID();

            if (emailID >= startID && emailID <= endID) {
                emailsInRange.add(email);
            }
        }

        return emailsInRange;
    }

    public static Email getEmailWithID(int emailID) { //Finding specified Email with id
        for (Email email : fullEmailList) {
            if (emailID == email.getEmailID()) {
                return email;
            }
        }
        return null;
    }

     public static Map<String,Integer> CalculateSummaryData(ArrayList<Email> list){ //Getting features of ArrayList of Emails
       
        Map<String, Integer> summaryData = new HashMap<>();

        // Lists to store individual word counts for each email
        List<Integer> wordCounts = new ArrayList<>();
        List<Integer> urlCounts = new ArrayList<>();
        List<Integer> numberCounts = new ArrayList<>();
        List<Integer> iCounts = new ArrayList<>();
        List<Integer> hyperlinkCounts = new ArrayList<>();
        List<Integer> youCounts = new ArrayList<>();

        for (Email email : list) {
            Map<String, Integer> emailFeatures = email.getFeatures();
            
            // Add word counts to their list
            wordCounts.add(emailFeatures.get("Word Count"));
            urlCounts.add(emailFeatures.get("Url Count"));
            numberCounts.add(emailFeatures.get("Number Count"));
            iCounts.add(emailFeatures.get("i Count"));
            hyperlinkCounts.add(emailFeatures.get("Hyperlink Count"));
            youCounts.add(emailFeatures.get("You Count"));
        }


        // Median counts
        int medianWordCount = calculateMedian(wordCounts);
        int medianUrlCount = calculateMedian(urlCounts);
        int medianNumberCount = calculateMedian(numberCounts);
        int medianICount = calculateMedian(iCounts);
        int medianHyperlinkCount = calculateMedian(hyperlinkCounts);
        int medianYouCount = calculateMedian(youCounts);

        summaryData.put("Url Count", medianUrlCount);
        summaryData.put("Word Count", medianWordCount);
        summaryData.put("Number Count", medianNumberCount);
        summaryData.put("i Count", medianICount);
        summaryData.put("HyperLink Count", medianHyperlinkCount);
        summaryData.put("You Count", medianYouCount);

      

        return summaryData;
    }

   
    private static int calculateMedian(List<Integer> values) {
        Collections.sort(values); //Sorting numerically
        int middle = values.size() / 2;
        if (values.size() % 2 == 1) { //if odd number
            return values.get(middle);
        } 
        else { //if even
            int lower = values.get(middle - 1);
            int upper = values.get(middle);
            return (lower + upper) / 2;
        }
    }

   
    public static double calculateEuclideanDistance(Map<String, Integer> map1, Map<String, Integer> map2) {
        double sumOfSquaredDifferences = 0.0;

        for (Map.Entry<String, Integer> entry : map1.entrySet()) { //Iterating through Each entry in map1
            String feature = entry.getKey(); //Setting feature equal to appropiate key to find value 2
            int value1 = entry.getValue();
            int value2 = map2.getOrDefault(feature, 0);
            double squaredDifference = Math.pow(value1 - value2, 2);
            sumOfSquaredDifferences += squaredDifference;
        }

        for (Map.Entry<String, Integer> entry : map2.entrySet()) { //Iterating through each entry in map2
            if (!map1.containsKey(entry.getKey())) {
                int value1 = entry.getValue();
                double squaredDifference = Math.pow(value1, 2);
                sumOfSquaredDifferences += squaredDifference;
            }
        }

        return Math.sqrt(sumOfSquaredDifferences);
    }


   

    public static boolean isSpamUsingEuclidian(Email thisEmail) {
        Map<String, Integer> thisEmailFeatures = thisEmail.getFeatures();
    
        //Getting spam features and not spam features
        Map<String, Integer> averageNotSpamData = CalculateSummaryData(notSpamEmails);
        Map<String, Integer> averageSpamData = CalculateSummaryData(spamEmails);
    
        //Calculate Euclidean distances
        double distanceToNotSpam = calculateEuclideanDistance(thisEmailFeatures, averageNotSpamData);
        double distanceToSpam = calculateEuclideanDistance(thisEmailFeatures, averageSpamData);
    
    
        
        //Compare distance to Spam and distance to notSpam
        if (distanceToSpam < distanceToNotSpam) {
            return true; //It's spam
        }   
        else {
            return false; //It's not spam
        }
    }
    public static ArrayList<Email> getNearestNeighbors(Email testEmail, int n) {
    List<Email> allEmails = new ArrayList<>(fullEmailList);
    allEmails.remove(testEmail); // Remove testEmail from list

    //Calculate distances between the test email and all other emails
    Map<Email, Double> distances = new HashMap<>();
    for (Email email : allEmails) {
        double distance = calculateEuclideanDistance(testEmail.getFeatures(), email.getFeatures());
        distances.put(email, distance);
    }

    //Sort the emails by distance
    allEmails.sort(new Comparator<Email>() {
        public int compare(Email e1, Email e2) {
            return Double.compare(distances.get(e1), distances.get(e2));
        }
    });

    // Return the top n nearest neighbors
    return new ArrayList<>(allEmails.subList(0, n));
}

    public static boolean isSpamUsingNearestNeighbors(Email thisEmail, int n) {
        List<Email> nearestNeighbors = getNearestNeighbors(thisEmail, n);

        // Count the number of spam and non-spam emails among the nearest neighbors
        int spamCount = 0;
        int notSpamCount = 0;
        for (Email neighbor : nearestNeighbors) {
            if (spamEmails.contains(neighbor)) {
                spamCount++;
            } else if (notSpamEmails.contains(neighbor)) {
                notSpamCount++;
            }
        }

        // Determine the predicted class based on the majority of the nearest neighbors
        return spamCount > notSpamCount;
    }
    public static double calculateDistanceBetweenSpamFeatures(Email email) {
        // Calculate distance between spam features for given email
        Map<String, Integer> emailFeatures = email.getFeatures();
        Map<String, Integer> averageSpamData = CalculateSummaryData(spamEmails);
        double distance = calculateEuclideanDistance(emailFeatures, averageSpamData);
        return distance;
    }
    
    public static double calculateDistanceBetweenNotSpamFeatures(Email email) {
        // Calculate distance between not spam features for given email
        Map<String, Integer> emailFeatures = email.getFeatures();
        Map<String, Integer> averageNotSpamData = CalculateSummaryData(notSpamEmails);
        double distance = calculateEuclideanDistance(emailFeatures, averageNotSpamData);
        return distance;
    }
    
    

 
}

    
    

