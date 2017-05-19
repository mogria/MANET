package ch.hslu.mobsys.manet;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by severin on 19.05.17.
 */
public class ManetGUIController {

    private Logger logger = LogManager.getLogger(ManetGUIController.class);
    FixedSizeList messageWindow;
    Router router;
    Sender sender;
    @FXML
    private Button outputText;

    @FXML
    private TextField txtIdent;
    @FXML
    private TextField txtMessage;
    @FXML
    private TextField txtUid;
    @FXML
    private TextField txtProbability;
    @FXML
    private TableView tblReceivedMessages;

    @FXML
    private void test()
    {
        System.out.println("tesr");

    }

    @FXML
    public void initialize() {

        messageWindow = new FixedSizeList(new ArrayList());
        sender = new Sender();
        router = new Router(messageWindow,sender);

        router.addMessageHandler(message -> {
            messageWindow.add(message);
            System.out.println("sent");
            logger.info("Message Received");
        });
        new Thread(router).start();


    }

    @FXML
    private void send(){
        logger.info("Send pressed");
        MulticastMessage messageToSend = new MulticastMessage();
        messageToSend.setIdentifier(txtIdent.getText());
        messageToSend.setMessage(txtMessage.getText());
        int uid=0;
        try {
            uid = Integer.parseInt(txtUid.getText());
        }catch (NumberFormatException e){

        }

        messageToSend.setUId(uid);
        sender.sendMessage(messageToSend);

    }

}
