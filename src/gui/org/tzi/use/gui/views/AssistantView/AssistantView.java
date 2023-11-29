package org.tzi.use.gui.views.AssistantView;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PageFormat;
import java.io.BufferedReader;
import java.net.HttpURLConnection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ModelBrowser;
import org.tzi.use.gui.views.PrintableView;
import org.tzi.use.gui.views.View;
import org.tzi.use.uml.sys.MSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// MOSI Chat View class
// custom view class for chat bot
@SuppressWarnings("serial")
public class AssistantView extends JPanel implements View, PrintableView{


   protected final MainWindow fMainWindow;
  
   private final MSystem fSystem;


    // Chat GUI objects.
    private JButton chatButton;
    private JButton faqButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextArea chatTextArea;
    private JTextArea faqTextArea;
    private JButton rightFAQButton;
    private JButton leftFAQButton;
    private JButton backFAQButton;
    private JTextField textField;
    private JButton sendButton;

    private FAQNode rootNode;
    private FAQNode currentNode;


    public AssistantView( MainWindow mainWindow, MSystem system, boolean loadLayout ) {
        this.setFocusable(true);
        fMainWindow = mainWindow;
        fSystem = system;
 
        setLayout(new BorderLayout());
 
        // Add View components here.
        // Create a panel for the top buttons
        JPanel buttonPanel = new JPanel();
        chatButton = new JButton("Chat");
        faqButton = new JButton("FAQ");
        buttonPanel.add(chatButton);
        buttonPanel.add(faqButton);
 
        chatTextArea = new JTextArea(10, 30);
        chatTextArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
        
        // Creates FAQ text area inside ScrollPane.
        faqTextArea = new JTextArea(10, 30);
        faqTextArea.setEditable(false);

        faqTextArea.setLineWrap(true); 
        faqTextArea.setWrapStyleWord(true);
        JScrollPane faqScrollPane = new JScrollPane(faqTextArea);

        // Creates FAQ tree.
        FAQTree faqTree = new FAQTree();

        // Stores root node.
        this.rootNode = faqTree.getCurrentQuestion();
        this.currentNode = faqTree.getCurrentQuestion();







        
        // Set initial questions on Faq buttons.
        leftFAQButton = new JButton(rootNode.getLeftChild().getButtonText());
        rightFAQButton = new JButton(rootNode.getRightChild().getButtonText());
        backFAQButton = new JButton("Back");

        // Sets back button as disabled (initially).
        backFAQButton.setEnabled(false);
        backFAQButton.setBorderPainted( false );
        backFAQButton.setFocusPainted( false );
 







        // FAQ panel with 3 FAQ button prompts.
        JPanel faqButtonPanel = new JPanel();
        faqButtonPanel.add(leftFAQButton);
        faqButtonPanel.add(rightFAQButton);
        faqButtonPanel.add(backFAQButton);
 
        // Action listeners for FAQ buttons.
        leftFAQButton.addActionListener(new LeftFAQButtonListener());
        rightFAQButton.addActionListener(new RightFAQButtonListener());
        backFAQButton.addActionListener(new BackFAQButtonListener());
 
        JPanel faqPanel = new JPanel(new BorderLayout());
        faqPanel.add(faqScrollPane, BorderLayout.CENTER);
        faqPanel.add(faqButtonPanel, BorderLayout.SOUTH);

        // Card Layout for Chat and FAQ
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Add Chat and FAQ Chat as cards in cardPanel.
        cardPanel.add(chatScrollPane, "Chat");
        cardPanel.add(faqPanel, "FAQ");

        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Chat");
            }
        });

        faqButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "FAQ");
            }
        });

        textField = new JTextField(20);
        sendButton = new JButton("Send");
        
        // Text field Listener Objects.
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
 
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();  // Consume the Enter key event
                    sendMessage();
                }
            }
 
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        // Send Button Listener Object.
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
 
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(textField);
        bottomPanel.add(sendButton);
 
        add(buttonPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Left FAQ Button Action Listener.
    public class LeftFAQButtonListener implements ActionListener {

        public LeftFAQButtonListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            // Post question and answer.
            faqTextArea.append("User: " + currentNode.getLeftChild().getQuestion() + "\n");
            faqTextArea.append("Assistant: " + currentNode.getLeftChild().getAnswer() + "\n\n");

            // Check if we can go down tree.
            if (currentNode.getLeftChild().isLeaf()) {
                // The next question node is a leaf.
                // So we disable button, can only chose different question or go back.
                leftFAQButton.setEnabled(false);
                leftFAQButton.setBorderPainted( false );
                leftFAQButton.setFocusPainted( false );

            } else {
                // Next node
                // Set current node to question node selected.
                currentNode = currentNode.getLeftChild();

                // Update buttons based on current node.
                leftFAQButton.setText(currentNode.getLeftChild().getButtonText());
                rightFAQButton.setText(currentNode.getRightChild().getButtonText());

                backFAQButton.setEnabled(true);
                backFAQButton.setBorderPainted( true );
                backFAQButton.setFocusPainted( true );
            }
        }
    }

    // Right FAQ Button Action Listener.
    private class RightFAQButtonListener implements ActionListener {

        public RightFAQButtonListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Post question and answer.
            faqTextArea.append("User: " + currentNode.getRightChild().getQuestion() + "\n");
            faqTextArea.append("Assistant: " + currentNode.getRightChild().getAnswer() + "\n\n");

            // Check if we can go down tree.
            if (currentNode.getRightChild().isLeaf()) {
                // The next question node is a leaf.
                // So we disable button, can only chose different question or go back.
                rightFAQButton.setEnabled(false);
                rightFAQButton.setBorderPainted( false );
                rightFAQButton.setFocusPainted( false );
            } else {

                // Next node
                // Set current node to question node selected.
                currentNode = currentNode.getRightChild();

                // Update buttons based on current node.
                leftFAQButton.setText(currentNode.getLeftChild().getButtonText());
                rightFAQButton.setText(currentNode.getRightChild().getButtonText());

                backFAQButton.setEnabled(true);
                backFAQButton.setBorderPainted( true );
                backFAQButton.setFocusPainted( true );
            }

            // When it is appended, based on what question was selected:
            // - Rename buttons to new questions.
            // - Create new FAQ ActionListeners for each button based on the question.
            // Implement data structure containing the question structure.
        }
    }

    // Back FAQ Button Action Listener.
    private class BackFAQButtonListener implements ActionListener {

        public BackFAQButtonListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Check if currentNode isn't the root.
            if(currentNode.equals(rootNode)){
                // Disable back button if we can't go further back in tree.
                backFAQButton.setEnabled(false);
                backFAQButton.setBorderPainted( false );
                backFAQButton.setFocusPainted( false );

            } else{
                // Switch current node to the nodes parent.
                currentNode = currentNode.getParent();
                // Update buttons based on current node.
                leftFAQButton.setText(currentNode.getLeftChild().getButtonText());
                rightFAQButton.setText(currentNode.getRightChild().getButtonText());
                
                // Enable question buttons.
                leftFAQButton.setEnabled(true);
                leftFAQButton.setBorderPainted( true );
                leftFAQButton.setFocusPainted( true );

                rightFAQButton.setEnabled(true);
                rightFAQButton.setBorderPainted( true );
                rightFAQButton.setFocusPainted( true );

                // Can't go back further up the tree.
                if(currentNode.equals(rootNode)){
                    // Disable back button.
                    backFAQButton.setEnabled(false);
                    backFAQButton.setBorderPainted( false );
                    backFAQButton.setFocusPainted( false );
                }
            }
        }
    }

    
    // MAB TODO: Send questoin to API, and add reponse in chatTextArea. 
    private void sendMessage() {
        String message = textField.getText();
        if (!message.trim().isEmpty()) {
            chatTextArea.append("You: " + message + "\n\n");
            


            try {
            String apiKey = "YOUR_API_KEY";
            String endpoint = "https://api.openai.com/v1/engines/davinci-codex/completions";

            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Set up your request payload here
            String payload = "{\"prompt\": \"Write something here.\"}";

            connection.setDoOutput(true);
            connection.getOutputStream().write(payload.getBytes("UTF-8"));

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            System.out.println(response.toString());

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
            


            // Reset textField.
            textField.setText("");
        }
    }

       
   public MSystem system() {
       return fSystem;
   }
  
   /**d
    * Returns the model browser.
    */
   public ModelBrowser getModelBrowser() {
       return fMainWindow.getModelBrowser();
   }
  
   /**
    * Determines if this is the selected view.
    * @return <code>true</code> if it is the selected view, otherwise
    * <code>false</false>
    */
    public boolean isSelectedView() {
       if ( fMainWindow.getSelectedView() != null ) {
           return fMainWindow.getSelectedView().equals( this );
       }
       return false;
    }


   @Override
   public void printView(PageFormat pf) {
       // TODO Auto-generated method stub
       throw new UnsupportedOperationException("Unimplemented method 'printView'");
   }


   @Override
   public void export(Graphics2D g) {
       // TODO Auto-generated method stub
       throw new UnsupportedOperationException("Unimplemented method 'export'");
   }


   @Override
   public float getContentHeight() {
       // TODO Auto-generated method stub
       throw new UnsupportedOperationException("Unimplemented method 'getContentHeight'");
   }


   @Override
   public float getContentWidth() {
       // TODO Auto-generated method stub
       throw new UnsupportedOperationException("Unimplemented method 'getContentWidth'");
   }


   @Override
   public void detachModel() {
       // TODO Auto-generated method stub
       throw new UnsupportedOperationException("Unimplemented method 'detachModel'");
   }
}


