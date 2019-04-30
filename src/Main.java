import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import javax.mail.*;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {


    public static void main(String[] args) {

        // variables
        LinkedList<Classes> classes = new LinkedList<>();
        // waxman, chen, obrenic, test
        int[] sectionsToCheck = {54504, 54527, 54495, 54524};
        boolean[] checkedSections = {false, false, false, false};




        int i = 0;
        int check = 1;
        while(i != checkedSections.length) {
            long start = System.currentTimeMillis();


            System.out.println("Attempt number: " + (check++));

            // populate classes
            getData(classes);

            //check if any sections we want opened up
            for(int j = 0; j < sectionsToCheck.length; j++) {
                if(!checkedSections[j])
                    for(Classes course: classes)
                        if(course.checkClassNumber(sectionsToCheck[j])) {
                            checkedSections[j] = true;
                            i++;
                            sendEmail(course.getHeader(), course.getBody(sectionsToCheck[j]));
                            break;
                        }
            }

            System.out.println("Time it took:" + (System.currentTimeMillis() - start));
            try {
                // check every 30 seconds
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



    }

    public static void getData(LinkedList<Classes> classes) {
        // link to website
        String htmlLink = "https://globalsearch.cuny.edu/CFGlobalSearchTool/search.jsp";

        // setup web client
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setCssEnabled(false);

        // turn off warning messages
        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "fatal");
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        Logger.getLogger("org.apache.http").setLevel(Level.OFF);


        try {
            /*
                PICK COLLEGE AND TERM
            */
            // connect to page
            HtmlPage page = client.getPage(htmlLink);
            // find form
            HtmlForm form = page.getFormByName("searchform");
            // Queens College Checkbox
            HtmlCheckBoxInput qcCheckBox = form.getInputByValue("QNS01");
            // handle for terms drop down menu
            HtmlSelect termSelect = form.getSelectByName("term_value");
            // next button
            HtmlInput nextButton = form.getInputByName("next_btn");


            // check QC and spring 2019, then click the button to proceed to next page
            qcCheckBox.setChecked(true);
            // 1199 is fall 2018
            termSelect.setSelectedAttribute(termSelect.getOptionByValue("1199"), true);
            HtmlPage page2 = nextButton.click(); // handle of new page

            /*
                PICK SUBJECT
            */
            // find form
            form = page2.getFormByName("class_search_form");
            // handle for subject drop down menu and Course Career menu (CMSC and UGRD)
            HtmlSelect subjectSelect = form.getSelectByName("subject_name");
            HtmlSelect careerSelect = form.getSelectByName("courseCareer");
            // uncheck open classes only (don't implement yet)

            // get search button
            HtmlInput searchButton = form.getInputByName("search_btn_search");

            // pick Computer Science(CMSC) and undergrad(UGRD)
            subjectSelect.setSelectedAttribute(subjectSelect.getOptionByValue("CMSC"), true);
            careerSelect.setSelectedAttribute(careerSelect.getOptionByValue("UGRD"), true);

            // get and extract the data
            page = searchButton.click();
            parseClasses(page.asText(), classes);
            System.out.println(page.asText());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void parseClasses(String data, LinkedList<Classes> classes) {

        String[] split = data.split("\r\n");
        String[] s;
        String desc, name;
        Classes temp;
        boolean done;

        for(int i = 0; i < split.length; i++) {
            // if we find a class
            if(split[i].contains("CSCI")) {
                // extract class name and description
                split[i] = split[i].trim();
                name = split[i].substring(0, 8).trim();
                desc = split[i].substring(8).replaceAll("-", " ").trim();
                temp = new Classes(name, desc);

                // extract sections
                i += 2;
                done = true;
                while(done) {
                    split[i] = split[i].trim();
                    if(split[i].length() == 0 || split[i].charAt(0) != '5') {
                        done = false;
                        continue;
                    }
                    s = split[i].split("\t");

                    temp.addSection(Integer.parseInt(s[0]), s[2].trim(), s[4].trim());

                    i++;
                }
                classes.add(temp);
            }
        }



    }


    public static void sendEmail(String subject, String body) {

        //setup email
        /*
            gmail
            acc: classcheckerjava@gmail.com
            pass: CSCI+399
         */

        // receiver
        String receiver1 = "shobandeep@gmail.com";
        //String receiver2 = "jasiriscool@yahoo.com";
        final String sender = "classcheckerjava@gmail.com";
        final String password = "";

        //log
//        System.out.println("- an open class was found.\n" + subject + "\n" + body +
//                "\n- attempting to send an email to: " + receiver1 + " and " + receiver2);

        // Getting system properties
        Properties properties = System.getProperties();

        // Setting up mail server
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        // SSL stuff
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        // Gmail needs to know account info
        properties.setProperty("mail.smtp.auth", "true");
        // SMTP port
        properties.setProperty("mail.smtp.port", "465");

        // have over properties to session object and give values for auth
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try
        {

            // make new message, give it the session info and sender/receiver
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver1));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver2));

            // set message content
            message.setSubject(subject);
            message.setText(body);


            // Send message
            Transport.send(message);
        }
        catch (MessagingException e)
        {
            System.out.println("- failed to send email");
            e.printStackTrace();
        }
        System.out.println("- email was sent successfully");
    }



}