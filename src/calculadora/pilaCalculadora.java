package calculadora;

public class pilaCalculadora {
    nodoCalculadora top = null;
    double trigonometricas;
    int size;
    String Cstr;

    public void push(String symbol) {
        nodoCalculadora nuevo = new nodoCalculadora(symbol);
        nuevo.sig = top;
        top = nuevo;
        size++;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void pop(){
        nodoCalculadora eraseNodo;
        if(!isEmpty()){
            eraseNodo = top;
            top = top.sig;
            eraseNodo.sig = null;
            size--;
        }   
    }

    public boolean operatorOrOperad(String text){
        if (text.equals("e")) {
            return true; // Considera "e" como un número
        }
        try {
            Float.parseFloat(text);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    
    public void operations(String operator) {
        double resultado = 0; // Inicializar resultado

        // Operación unaria
        if (size < 1 && (operator.equals("sin") || operator.equals("cos") || operator.equals("tan") ||
            operator.equals("asin") || operator.equals("acos") || operator.equals("atan") ||
            operator.equals("√") || operator.equals("log") || operator.equals("ln") ||
            operator.equals("alog") || operator.equals("aln"))) {
            System.out.println("Error: No hay suficientes operandos en la pila para la operación " + operator);
            return;
        }

        float B = 0;
        if (!isEmpty()) {
            String Bstr = top.symbol;
            B = (Bstr.equals("e") ? (float)Math.E : Float.parseFloat(Bstr));
            pop(); // Remover el elemento superior para las operaciones unarias y binarias
        }

        float A = 0; // A se define solo si es necesario, es decir, para operaciones binarias
        if (!operator.equals("sin") && !operator.equals("cos") && !operator.equals("tan") &&
            !operator.equals("asin") && !operator.equals("acos") && !operator.equals("atan") &&
            !operator.equals("√") && !operator.equals("log") && !operator.equals("ln") &&
            !operator.equals("alog") && !operator.equals("aln") && !isEmpty()) {
            String Astr = top.symbol;
            A = (Astr.equals("e") ? (float)Math.E : Float.parseFloat(Astr));
            pop(); // Solo pop otro si es una operación binaria
        }

        // Cálculo basado en el operador
        switch (operator) {
            case "+":
                resultado = A + B;
                push(String.valueOf(resultado));
                break;
            case "-":
                resultado = A - B;
                push(String.valueOf(resultado));
                break;
            case "*":
                resultado = A * B;
                push(String.valueOf(resultado));
                break;
            case "÷":
                resultado = A / B;
                push(String.valueOf(resultado));
                break;
            case "^":
                resultado = Math.pow(A, B);
                push(String.valueOf(resultado));
                break;
            case "√":
                resultado = Math.sqrt(B);
                push(String.valueOf(resultado));
                break;
            case "log":
                resultado = Math.log10(B);
                push(String.valueOf(resultado));
                break;
            case "ln":
                resultado = Math.log(B);
                push(String.valueOf(resultado));
                break;
            case "alog":
                resultado = Math.pow(10, B);
                push(String.valueOf(resultado));
                break;
            case "aln":
                resultado = Math.exp(B);
                push(String.valueOf(resultado));
                break;
            case "sin":
            case "cos":
            case "tan":
            case "asin":
            case "acos":
            case "atan":
            case "sec":
            case "csc":
            case "cot":
                resultado = calcularTrigonometrica(operator, B);
                push(String.valueOf(resultado));
                break;
            default:
                System.out.println("Operador no reconocido.");
        }
    }

    private double calcularTrigonometrica(String operador, float valor) {
        return switch (operador.toLowerCase()) {
            case "sin" -> Math.sin(Math.toRadians(valor));
            case "cos" -> Math.cos(Math.toRadians(valor));
            case "tan" -> Math.tan(Math.toRadians(valor));
            case "asin" -> Math.toDegrees(Math.asin(valor));
            case "acos" -> Math.toDegrees(Math.acos(valor));
            case "atan" -> Math.toDegrees(Math.atan(valor));
            case "sec" -> 1 / Math.cos(Math.toRadians(valor));
            case "csc" -> 1 / Math.sin(Math.toRadians(valor));
            case "cot" -> 1 / Math.tan(Math.toRadians(valor));
            default -> 0;
        };
    }
    
}