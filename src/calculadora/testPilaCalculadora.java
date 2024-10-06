package calculadora;

public class testPilaCalculadora {

    public static String mainValue(String[] Result) {
        pilaCalculadora stacktest = new pilaCalculadora();
        String[] test1 = Result;

        for (int i = 0; i < test1.length; i++) {
            if(stacktest.operatorOrOperad(test1[i])){
                stacktest.push(test1[i]);
            }else{
                stacktest.operations(test1[i]);
            }
        }

        return stacktest.top.symbol;
    }
}