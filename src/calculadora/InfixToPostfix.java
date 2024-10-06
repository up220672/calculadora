package calculadora;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class InfixToPostfix {

    public static List<String> conversor(String operacion) {
        String operacionEspaciada = preprocesarOperacion(operacion);
        
        pilaCalculadora pilaDeOperadores = new pilaCalculadora();
        Map<String, Integer> prioridades = new HashMap<>();
        
        // Establecer prioridades para los operadores, funciones y constantes
        prioridades.put("(", 0);
        prioridades.put("+", 1);
        prioridades.put("-", 1);
        prioridades.put("*", 2);
        prioridades.put("÷", 2);
        prioridades.put("sin", 4);
        prioridades.put("cos", 4);
        prioridades.put("tan", 4);
        prioridades.put("asin", 4);
        prioridades.put("atan", 4);
        prioridades.put("acos", 4);
        prioridades.put("√", 3);
        prioridades.put("^", 3);
        prioridades.put("log", 4);
        prioridades.put("ln", 4);
        prioridades.put("alog", 4);
        prioridades.put("aln", 4);
        // No se necesita prioridad para 'e' ya que se maneja como número

        List<String> listaPostfix = new ArrayList<>();
        String[] operacionSplit = operacionEspaciada.trim().split("\\s+");
        List<String> listaInfix = new ArrayList<>(Arrays.asList(operacionSplit));
        listaInfix.add(0, "(");
        listaInfix.add(listaInfix.size(), ")");

        for (String elemento : listaInfix) {
            if (isNumber(elemento) || elemento.equals("e")) { // Manejar 'e' como número
                listaPostfix.add(elemento);
            } else if (elemento.equals("(")) {
                pilaDeOperadores.push(elemento);
            } else if (elemento.equals(")")) {
                while (!pilaDeOperadores.top.symbol.equals("(")) {
                    listaPostfix.add(pilaDeOperadores.top.symbol);
                    pilaDeOperadores.pop();
                }
                pilaDeOperadores.pop();
            } else {
                while (!pilaDeOperadores.isEmpty() &&
                       prioridades.getOrDefault(elemento, 5) <= prioridades.getOrDefault(pilaDeOperadores.top.symbol, 5)) {
                    listaPostfix.add(pilaDeOperadores.top.symbol);
                    pilaDeOperadores.pop();
                }
                pilaDeOperadores.push(elemento);
            }
        }
        return listaPostfix;
    }

    private static String preprocesarOperacion(String operacion) {
        StringBuilder conEspacios = new StringBuilder();
        boolean puedeSerNegativo = true;
        StringBuilder funcionActual = new StringBuilder(); // Acumula letras para formar nombres de funciones

        for (int i = 0; i < operacion.length(); i++) {
            char c = operacion.charAt(i);

            if (Character.isLetter(c)) {
                funcionActual.append(c); // Acumula caracteres para nombres de funciones
                continue;
            }

            if (funcionActual.length() > 0) {
                // Si hay una función acumulada, agrega espacios alrededor
                conEspacios.append(" ").append(funcionActual.toString()).append(" ");
                funcionActual.setLength(0); // Limpia el acumulador para el próximo nombre de función
            }

            if (c == '-') {
                if (puedeSerNegativo) {
                    conEspacios.append(c);
                } else {
                    conEspacios.append(" - ");
                }
                puedeSerNegativo = true;
            } else {
                if (c == ' ' || Character.isDigit(c) || c == '.') {
                    conEspacios.append(c);
                } else {
                    conEspacios.append(" ").append(c).append(" ");
                    puedeSerNegativo = true;
                }
            }

            if (Character.isDigit(c)) {
                puedeSerNegativo = false;
            }
        }

        // Añadir cualquier nombre de función restante al final de la operación
        if (funcionActual.length() > 0) {
            conEspacios.append(" ").append(funcionActual.toString()).append(" ");
        }

        // Normalizar espacios y aplicar tratamientos adicionales de limpieza
        String resultado = conEspacios.toString().replaceAll("\\s+", " ").trim();
        resultado = resultado.replaceAll("([+\\-*/√÷^()])", " $1 ");
        resultado = resultado.replaceAll("\\s{2,}", " ").trim();

        return resultado;
    }



    public static boolean isNumber(String number) {
        if (number.equals("e")) return true; // Tratar 'e' como número
        try {
            Float.parseFloat(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static List<String> convertirAPostfijo(String operacion) {
        return conversor(operacion);
    }
    
    public static String evaluarExpresionPostfija(List<String> expresionPostfija) {
        String[] Result = expresionPostfija.toArray(new String[0]);
        return testPilaCalculadora.mainValue(Result);
    }

    public static String retornarInfijoFormateado(String operacion) {
        return preprocesarOperacion(operacion);
    }
}
