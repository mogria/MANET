package ch.hslu.mobsys.manet;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Logger

/**
 * Created by severin on 19.05.17.
 */
public class ManetGUIController {


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
    private void test()
    {
        System.out.println("tesr");

    }

    @FXML
    private void send(){
        MulticastMessage message = new MulticastMessage();
        message.setIdentifier(txtIdent.getText());
        message.setMessage(txtMessage.getText());
        int uid=0;
        try {
            uid = Integer.parseInt(txtUid.getText());
        }catch (NumberFormatException e){

        }
        message.setUId(uid);

        Router router = new Router();
        router.sendMessage(message);
    }

}
