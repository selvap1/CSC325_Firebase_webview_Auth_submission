package com.example.csc325_firebase_webview_auth.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import netscape.javascript.JSObject;
import org.w3c.dom.Document; // Import the Document class from W3C DOM

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.example.csc325_firebase_webview_auth.view.App;
import javafx.event.ActionEvent;

public class WebContainerController implements Initializable {

    private Document doc; // Now this will be recognized
    private DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static String HTML_STRING2 = //
            "<html>"//
                    + "<head> " //
                    + "  <script language='javascript'> " //
                    + "     function changeBgColor()  { "//
                    + "       var color= document.getElementById('ueberschr').value; "//
                    + "       document.body.style.backgroundColor= color; " //
                    + "     } " //
                    + "  </script> "//
                    + "</head> "//
                    + "<body> "//
                    + "   <h2>This is Html content</h2> <input id='ueberschr' value='yellow' />" //
                    + "   <button onclick='app12.showTime();changeBgColor();'>Call To JavaFX</button> "//
                    + "</body> "//
                    + "</html> "//
            ;

    @FXML
    private Label label; // FXML reference for label

    @FXML
    private WebView webView; // FXML reference for WebView
    private WebEngine webEngine;

    @FXML
    private void goAction(ActionEvent evt) {
        webEngine.load("http://google.com");
    }

    @FXML
    private void setLabel(ActionEvent e) {
        if (doc != null) {
            doc.getElementById("ueberschr").setAttribute("value", "Red");
        } else {
            System.out.println("Document is not loaded yet.");
        }
    }

    @FXML
    private void switchBackStage(ActionEvent e) {
        try {
            App.setRoot("/files/AccessFBView.fxml"); // Ensure the path is correct
        } catch (IOException ex) {
            Logger.getLogger(WebContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            webEngine = webView.getEngine();
            webEngine.loadContent(HTML_STRING2);

            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov, State t, State newState) {
                    if (newState == State.SUCCEEDED) {
                        doc = webEngine.getDocument();
                        // Get window object of the page.
                        JSObject jsobj = (JSObject) webEngine.executeScript("window");
                        // Set member for 'window' object.
                        jsobj.setMember("app12", new Bridge());
                    }
                }
            });
            webView.setContextMenuEnabled(false);
            webEngine.setJavaScriptEnabled(true);
        } catch (Exception ex) {
            Logger.getLogger(WebContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class Bridge {
        public void showTime() {
            System.out.println("Show Time");
            if (label != null) {
                label.setText("Now is: " + df.format(new Date()));
            } else {
                System.out.println("Label is not initialized.");
            }
        }
    }
}
