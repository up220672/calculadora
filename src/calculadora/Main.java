package calculadora;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @authors 
 * - Daniel Eduardo Pedroza Rodríguez
 */

public class Main extends Application {
    
    public static Stage stage;
    
    public static void main (String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        Main.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Calculadora.fxml"));
        Scene ventanaPrincipal = new Scene(root);
        
        stage.setScene(ventanaPrincipal);
        stage.show();
    }
    
}
