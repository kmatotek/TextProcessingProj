import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

public class EmailClassifierGUI {
private JFrame frame;
private JTextArea emailText;
private JTextField emailIDInput;
private JButton displayEmailButton;
private JButton calculateSummaryDataButton;
private JButton classifySpamButton;
private JButton calculateDistanceButton;
private ReadFile readFile; // Instance of ReadFile class
private JTextField startEmailIDInput;
private JTextField endEmailIDInput;

public EmailClassifierGUI(ReadFile readFile) {
    this.readFile = readFile;

    frame = new JFrame("Email Classifier");
    frame.setLayout(new BorderLayout());

    startEmailIDInput = new JTextField(10); //Creating text fields/text area
    endEmailIDInput = new JTextField(10);
    emailIDInput = new JTextField(10);
    emailText = new JTextArea(10, 30);

    displayEmailButton = new JButton("Display Email"); //Creatin buttons
    calculateSummaryDataButton = new JButton("Display Summary Data");
    classifySpamButton = new JButton("Classify as Spam?");
    calculateDistanceButton = new JButton("Calculate Distance");

    JPanel topPanel = new JPanel(); //Adding label, text field and buttons to panel
    topPanel.add(new JLabel("Enter Email ID: "));
    topPanel.add(emailIDInput);
    topPanel.add(displayEmailButton);
    topPanel.add(calculateSummaryDataButton);
    topPanel.add(classifySpamButton);
    topPanel.add(calculateDistanceButton);

    frame.add(topPanel, BorderLayout.NORTH);
    frame.add(new JScrollPane(emailText), BorderLayout.CENTER); //Able to Scroll through long emails

    displayEmailButton.addActionListener(new ActionListener()
        {
        public void actionPerformed(ActionEvent e) {
            try {
                int emailID = Integer.parseInt(emailIDInput.getText()); //Retrieving the input as an int (email id)
                Email email = readFile.getEmailWithID(emailID); // Retrieve the email from ReadFile
                if (email != null) {
                    emailText.setText(email.toString());
                } 
                else {
                    emailText.setText("Email not found.");
                }
            } 
            catch (NumberFormatException ex) { //String doesn't have appropiate format to convert to int
                emailText.setText("Invalid Email ID.");
            }
        }
    });

    calculateSummaryDataButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                int startID = Integer.parseInt(JOptionPane.showInputDialog("Enter Start Email ID:"));
                int endID = Integer.parseInt(JOptionPane.showInputDialog("Enter End Email ID:"));
                ArrayList<Email> emailsInRange = ReadFile.getEmailsWithID(startID, endID); //Getting Array list of Emails between the two given ids
                Map<String, Integer> summaryData = ReadFile.CalculateSummaryData(emailsInRange); //Calculating the summary data
                emailText.setText("The following data is the median of each feature: "+summaryData.toString()); //Display
            } 
            catch (NumberFormatException ex) {
                emailText.setText("Invalid Email ID(s).");
            }
        }
    });
    

    classifySpamButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                int emailID = Integer.parseInt(emailIDInput.getText());
                Email email = readFile.getEmailWithID(emailID); //Creating new email with given id
                if (email != null) { //Does the id exist?
                    boolean isSpam = ReadFile.isSpamUsingNearestNeighbors(email,2); //Checking if spam or not
                    emailText.setText("Is Spam: " + isSpam);
                } 
                else { //Email id doesn't exist
                    emailText.setText("Email not found.");
                }
            } 
            catch (NumberFormatException ex) {
                emailText.setText("Invalid Email ID.");
            }
        }
    });

    calculateDistanceButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object[] options = {"Between Two Emails", "From Spam/Not Spam Features"}; //Two options when clicking this button
            int choice = JOptionPane.showOptionDialog(frame, "Choose what to calculate:", "Distance Calculation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])+1; //Otption box with JOptionPane
        
            if (choice == 1) { //Option 1: Calc distance between just two emails option
                try {
                    int emailID1 = Integer.parseInt(JOptionPane.showInputDialog("Enter Email ID 1:"));
                    int emailID2 = Integer.parseInt(JOptionPane.showInputDialog("Enter Email ID 2:"));
                    Email email1 = readFile.getEmailWithID(emailID1);
                    Email email2 = readFile.getEmailWithID(emailID2);
                    if (email1 != null && email2 != null) { //Calculate distance if both exist
                        double distanceBetweenBoth = ReadFile.calculateEuclideanDistance(email1.getFeatures(),email2.getFeatures());
                        emailText.setText("Euclidian distance between Email "+email1.getEmailID()+" and Email "+email2.getEmailID()+" is "+distanceBetweenBoth); //Display distance between the two emails
                    } 
                    else {
                        emailText.setText("Email not found.");
                    }
                } 
                catch (NumberFormatException ex) {
                    emailText.setText("Invalid Email ID.");
                }
            } 
            else if (choice == 2) { //Option 2, //Getting dist between spam / not spam
                try {
                    int emailID = Integer.parseInt(JOptionPane.showInputDialog("Enter Email ID:"));
                    Email email = readFile.getEmailWithID(emailID);
                    if (email != null) {
                        double distanceToSpam = ReadFile.calculateDistanceBetweenSpamFeatures(email); 
                        double distanceToNotSpam = ReadFile.calculateDistanceBetweenNotSpamFeatures(email);
                        emailText.setText("Distance to Spam Features: " + distanceToSpam + "\nDistance to Not Spam Features: " + distanceToNotSpam);
                    } 
                    else {
                        emailText.setText("Email not found.");
                    }
                } 
                catch (NumberFormatException ex) {
                    emailText.setText("Invalid Email ID.");
                }
            }
        }
    });
    
    
    

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit application when closed
    frame.pack();
    frame.setVisible(true); //Making visible
}

public static void main(String[] args) {
            ReadFile readFile = new ReadFile(); // Initialize ReadFile
            EmailClassifierGUI gui = new EmailClassifierGUI(readFile); // Pass ReadFile instance to the GUI
        }
}
