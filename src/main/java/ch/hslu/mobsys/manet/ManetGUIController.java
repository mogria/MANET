package ch.hslu.mobsys.manet;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Created by severin on 19.05.17.
 */
public class ManetGUIController {

    FixedSizeList messageWindow;
    Router router;
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
        router = new Router(messageWindow);
        router.addMessageHandler(message -> {
            System.out.println("Test");
        });
        router.run();
    }

    @FXML
    private void send(){
        MulticastMessage messageToSend = new MulticastMessage();
        messageToSend.setIdentifier(txtIdent.getText());
        messageToSend.setMessage(txtMessage.getText());
        int uid=0;
        try {
            uid = Integer.parseInt(txtUid.getText());
        }catch (NumberFormatException e){

        }
        messageToSend.setUId(uid);


    }

}
