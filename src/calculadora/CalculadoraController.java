package calculadora;

import static calculadora.InfixToPostfix.convertirAPostfijo;
import static calculadora.InfixToPostfix.evaluarExpresionPostfija;
import static calculadora.InfixToPostfix.retornarInfijoFormateado;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CalculadoraController implements Initializable {
    
    private Timeline timeline;
    
    private String entrada = "";
    private int posicionCursor = 0;
    private boolean isEntradaSelected = true;
    private boolean shift = false;
    private boolean alpha = false;
    private boolean estaEncendida = false;
    
    String infijoFormateado;
    List<String> postfijo;
    String resultado;
    
    private boolean hayResultadoEnPantalla = false;
    private int tipoResultado = 0; // 0 para valor, 1 para infijo, 2 para postfijo
    private int posicionResultado = 0; // Para el desplazamiento del texto en resultadoLabel

    @FXML
    private Label entradaLabel;
    @FXML
    private Label resultadoLabel;
    @FXML
    private Label shiftLabel;
    @FXML
    private Label alphaLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Main.stage.setResizable(false);
        actualizarVista();
        apagarCalculadora();
    }
    
    private void apagarCalculadora() {
        resultadoLabel.setText("0");
        resultadoLabel.setOpacity(0.0);
        shiftLabel.setOpacity(0.0);
        alphaLabel.setOpacity(0.0);
        entrada = "";
        posicionCursor = 0;
        isEntradaSelected = true;
        shift = false;
        alpha = false;
        if (!estaEncendida) {
            entradaLabel.setOpacity(0.0);
        }
        if (estaEncendida) {
            entradaLabel.setText("Casio");
        }
        // Detener y limpiar la instancia anterior de la línea de tiempo si existe
        if (timeline != null) {
            timeline.stop();
            timeline.getKeyFrames().clear();
        }
        // Crear una nueva línea de tiempo para ocultar la etiqueta de entrada después de 2 segundos
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            entradaLabel.setText("");
            entradaLabel.setOpacity(0.0);
            estaEncendida = false;
        }));
        timeline.setCycleCount(1);
        timeline.play(); // Iniciar la línea de tiempo
    }
    
    private void encenderCalculadora(){
        resultadoLabel.setText("0");
        resultadoLabel.setOpacity(0.4);
        entradaLabel.setText("");
        entradaLabel.setOpacity(1);
        shiftLabel.setOpacity(0.4);
        alphaLabel.setOpacity(0);
        entrada = "";
        posicionCursor = 0;
        isEntradaSelected = true;
        shift = false;
        alpha = false;
        estaEncendida = true;
        actualizarVista();
    }
    
    private void resetTeclasEspeciales() {
        shift = false;
        shiftLabel.setOpacity(0.4);
        alpha = false;
        alphaLabel.setOpacity(0.4);
    }

    private void actualizarVista() {
        
        int longitud = entrada.length();
        StringBuilder sb = new StringBuilder();

        // Construir la vista manteniendo el cursor dentro de una ventana fija
        int ventanaInicio = Math.max(0, Math.min(posicionCursor - 1, longitud - 12));
        int ventanaFin = Math.min(longitud, ventanaInicio + 12);

        for (int i = ventanaInicio; i < ventanaFin; i++) {
            if (i == posicionCursor) {
                sb.append("|"); // Agregar el cursor
            }
            sb.append(entrada.charAt(i));
        }

        // Si el cursor está al final de la entrada, agregar el cursor al final
        if (posicionCursor == longitud) {
            sb.append("|");
        }

        entradaLabel.setText(sb.toString());
        updateOpacities();
    }
    
    private void updateOpacities() {
        if (isEntradaSelected) {
            entradaLabel.setOpacity(1.0);
            resultadoLabel.setOpacity(0.4);
        } else {
            entradaLabel.setOpacity(0.4);
            resultadoLabel.setOpacity(1.0);
        }
    }

    @FXML
    private void botonArriba(ActionEvent event) {
        if (!estaEncendida) return;

        if (hayResultadoEnPantalla) {
            tipoResultado = (tipoResultado + 1) % 3; // Cicla entre 0, 1, 2
            actualizarTipoResultado();
        } else {
            resetTeclasEspeciales();
            isEntradaSelected = true;
            updateOpacities();
        }
    }

    @FXML
    private void botonAbajo(ActionEvent event) {
        if (!estaEncendida) return;

        if (hayResultadoEnPantalla) {
            if (tipoResultado == 0) {
                tipoResultado = 2; // Ciclo hacia atrás desde 0 a 2
            } else {
                tipoResultado -= 1;
            }
            actualizarTipoResultado();
        } else {
            resetTeclasEspeciales();
            isEntradaSelected = false;
            updateOpacities();
        }
    }

    private void actualizarTipoResultado() {
        switch (tipoResultado) {
            case 0:
                resultadoLabel.setText(resultado);
                alphaLabel.setText("Resultado");
                break;
            case 1:
                resultadoLabel.setText(infijoFormateado);
                alphaLabel.setText("Infijo");
                break;
            case 2:
                resultadoLabel.setText(postfijo.toString());
                alphaLabel.setText("Postfijo");
                break;
        }
        alphaLabel.setOpacity(1);
        resultadoLabel.setOpacity(1);
        entradaLabel.setOpacity(0.7);
    }

    @FXML
    private void botonDerecha(ActionEvent event) {
        if (!estaEncendida) return;

        if (hayResultadoEnPantalla) {
            // Incrementa posicionResultado para desplazar el texto de resultadoLabel a la derecha
            posicionResultado = Math.min(resultadoLabel.getText().length(), posicionResultado + 1);
            actualizarVistaResultado();
        } else {
            resetTeclasEspeciales();
            if (isEntradaSelected && posicionCursor < entrada.length()) {
                if (posicionCursor + 3 < entrada.length()) { // Asegura espacio para "sin ", "cos ", "tan "
                    String nextFourChars = entrada.substring(posicionCursor, Math.min(posicionCursor + 4, entrada.length()));
                    if (nextFourChars.equals("sin ") || nextFourChars.equals("cos ") || nextFourChars.equals("tan ")) {
                        posicionCursor += 4; // Avanza 4 posiciones para "sin ", "cos ", "tan "
                    } else if (posicionCursor + 4 < entrada.length()) { // Asegura espacio para "asin ", "acos ", "atan "
                        String nextFiveChars = entrada.substring(posicionCursor, posicionCursor + 5);
                        if (nextFiveChars.equals("asin ") || nextFiveChars.equals("acos ") || nextFiveChars.equals("atan ")) {
                            posicionCursor += 5; // Avanza 5 posiciones para "asin ", "acos ", "atan "
                        } else {
                            posicionCursor++; // Avanza 1 posición si no es ninguna de las anteriores
                        }
                    } else {
                        posicionCursor++; // Avanza 1 posición si no hay suficiente espacio para una función
                    }
                } else {
                    posicionCursor++; // Avanza 1 posición si no hay suficiente espacio para una función
                }
                actualizarVista();
            }
        }
    }

    @FXML
    private void botonIzquierda(ActionEvent event) {
        if (!estaEncendida) return;

        if (hayResultadoEnPantalla) {
            // Decrementa posicionResultado para desplazar el texto de resultadoLabel a la izquierda
            posicionResultado = Math.max(0, posicionResultado - 1);
            actualizarVistaResultado();
        } else {
            resetTeclasEspeciales();
            if (isEntradaSelected && posicionCursor > 0) {
                boolean isFunctionOfFiveChars = posicionCursor >= 5 &&
                                                 (entrada.substring(posicionCursor - 5, posicionCursor).equals("asin ") ||
                                                  entrada.substring(posicionCursor - 5, posicionCursor).equals("acos ") ||
                                                  entrada.substring(posicionCursor - 5, posicionCursor).equals("atan "));
                boolean isFunctionOfFourChars = !isFunctionOfFiveChars && posicionCursor >= 4 &&
                                                (entrada.substring(posicionCursor - 4, posicionCursor).equals("sin ") ||
                                                 entrada.substring(posicionCursor - 4, posicionCursor).equals("cos ") ||
                                                 entrada.substring(posicionCursor - 4, posicionCursor).equals("tan "));

                if (isFunctionOfFiveChars) {
                    posicionCursor -= 5; // Para "asin ", "acos ", "atan "
                } else if (isFunctionOfFourChars) {
                    posicionCursor -= 4; // Para "sin ", "cos ", "tan "
                } else {
                    posicionCursor--;
                }
                actualizarVista();
            }
        }
    }
    

    private void actualizarVistaResultado() {
        if (!hayResultadoEnPantalla) return;

        StringBuilder sb = new StringBuilder();
        String textoResultado = "";
        switch (tipoResultado) {
            case 0:
                textoResultado = resultado;
                break;
            case 1:
                textoResultado = infijoFormateado;
                break;
            case 2:
                textoResultado = postfijo.toString();
                break;
        }

        // Calcula la "ventana de visualización"
        int longitudResultado = textoResultado.length();
        int ventanaInicio = Math.max(0, Math.min(posicionResultado, longitudResultado - 12));
        int ventanaFin = Math.min(longitudResultado, ventanaInicio + 12);

        for (int i = ventanaInicio; i < ventanaFin; i++) {
            if (i == posicionResultado && !isEntradaSelected) { // Agrega cursor si resultado está seleccionado
                sb.append("|");
            }
            sb.append(textoResultado.charAt(i));
        }

        if (posicionResultado == longitudResultado && !isEntradaSelected) { // Agrega cursor al final si necesario
            sb.append("|");
        }

        resultadoLabel.setText(sb.toString());
    }


    private void agregarNumero(String numero) {
        resetTeclasEspeciales();
        String antes = entrada.substring(0, posicionCursor);
        String despues = entrada.substring(posicionCursor);
        entrada = antes + numero + despues;
        posicionCursor = posicionCursor + numero.length();
        actualizarVista();
    }

    @FXML
    private void boton0(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("0");
    }
    @FXML
    private void boton1(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("1");
    }
    @FXML
    private void boton2(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("2");
    }
    @FXML
    private void boton3(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("3");
    }
    @FXML
    private void boton4(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("4");
    }
    @FXML
    private void boton5(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("5");
    }
    @FXML
    private void boton6(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("6");
    }
    @FXML
    private void boton7(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("7");
    }
    @FXML
    private void boton8(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("8");
    }
    @FXML
    private void boton9(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("9");
    }
    @FXML
    private void botonPunto(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero(".");
    }

    @FXML
    private void botonShift(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == false) {
            shiftLabel.setOpacity(1.0); // Encender shiftLabel
            shift = true;
        } else {
            shiftLabel.setOpacity(0.4); // Apagar shiftLabel
            shift = false;
        }
    }

    @FXML
    private void botonParentesisAbierto(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("aln ");
            shift =  false;
        } else {
            agregarNumero("(");
        }
    }

    @FXML
    private void botonParentesisCerrado(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero(")");
    }

    @FXML
    private void botonCosecante(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("csc ");
    }

    @FXML
    private void botonSecante(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("sec ");
    }

    @FXML
    private void botonCotangente(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("cot ");
    }

    @FXML
    private void botonPotenciaCuadrada(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("log ");
            shift =  false;
        } else {
            agregarNumero("^2");
        }
    }

    @FXML
    private void botonPotenciaCubica(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("ln ");
            shift =  false;
        } else {
            agregarNumero("^3");
        }
    }

    @FXML
    private void botonPotenciaAlaN(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("e");
            shift =  false;
        } else {
            agregarNumero("^");
        }
    }

    @FXML
    private void botonSeno(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("asin ");
            shift =  false;
        } else {
            agregarNumero("sin ");
        }
    }

    @FXML
    private void botonCoseno(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("acos ");
            shift =  false;
        } else {
            agregarNumero("cos ");
        }
    }

    @FXML
    private void botonTangente(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("atan ");
            shift =  false;
        } else {
            agregarNumero("tan ");
        }
    }

    @FXML
    private void botonRaiz(ActionEvent event) {
        if (!estaEncendida) return;
        if (shift == true){
            agregarNumero("alog ");
            shift =  false;
        } else {
            agregarNumero("√");
        }
    }

    @FXML
    private void botonMas(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("+");
    }

    @FXML
    private void botonPor(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("*");
    }

    @FXML
    private void botonBorrar(ActionEvent event) {
        if (!estaEncendida) return;
        if (posicionCursor > 0 && !entrada.isEmpty()) {
            StringBuilder sb = new StringBuilder(entrada);
            sb.deleteCharAt(posicionCursor - 1);
            entrada = sb.toString();
            posicionCursor--;
            actualizarVista();
        }
    }

    @FXML
    private void botonIgual(ActionEvent event) {
        if (!estaEncendida) return;
        
        try {
            
            infijoFormateado = retornarInfijoFormateado(entrada);
            //infijoFormateado = retornarInfijoFormateado("2+asin (1.7071-1)+3");
            System.out.println("Infijo separado por espacios: " + infijoFormateado);

            //postfijo = convertirAPostfijo(infijoFormateado);
            postfijo = convertirAPostfijo(entrada);
            System.out.println("Postfijo: " + postfijo);

            resultado = evaluarExpresionPostfija(postfijo);
            System.out.println("Resultado: " + resultado);
            
            resultadoLabel.setText(resultado);
            
            
            hayResultadoEnPantalla = true;
            actualizarTipoResultado();
            isEntradaSelected = false;
            updateOpacities();
            
        } catch (Exception e) {
        }
        
    }

    @FXML
    private void botonMenos(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("-");
    }

    @FXML
    private void botonEntre(ActionEvent event) {
        if (!estaEncendida) return;
        agregarNumero("÷");
    }

    @FXML
    private void botonPrenderApagar(ActionEvent event) {
        
        if (!estaEncendida) {
            encenderCalculadora();
            return;
        }
        
        if (shift == true){
            apagarCalculadora();
            return;
        }
        
        // Restablecer las variables
        entrada = "";
        posicionCursor = 0;
        hayResultadoEnPantalla = true;
        isEntradaSelected = true;
        actualizarTipoResultado();
        updateOpacities();

        // Actualizar la interfaz de usuario
        actualizarVista();
        resultadoLabel.setText("0");

        // Establecer opacidades
        entradaLabel.setOpacity(1.0);
        resultadoLabel.setOpacity(0.4);
    }


}
