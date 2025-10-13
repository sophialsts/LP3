public class Calculadora {
    private double num1;
    private double num2;
    private char op;

    private double getNum1() {
        return num1;
    }

    private double getNum2() {
        return num2;
    }

    private char getOp() {
        return op;
    }

    private void setNum1(double num1_) {
        this.num1 = num1_;
    }

    private void setNum2(double num2_) {
        this.num2 = num2_;
    }

    private void setOp(char op_) {
        this.op = op_;
    }

    public void atribuir(double num1, double num2, char op) {
        setNum1(num1);
        setNum2(num2);
        setOp(op);
    }

    private double adicao() {
        return this.getNum1() + this.getNum2();
    }

    private double subtracao() {
        return this.getNum1() - this.getNum2();
    }

    private double multiplicacao() {
        return this.getNum1() * this.getNum2();
    }

    private double divisao() {
        if (this.getNum2() != 0) {
            return this.getNum1() / this.getNum2();
        }else{
            return Double.POSITIVE_INFINITY;
        }
    }

    public double calcular() {
        double resultado = 0;
        switch (this.getOp()) {
            case '+':
                resultado = this.adicao();
                break;
            case '-':
                 resultado = this.subtracao();
                 break;
            case '*':
                 resultado = this.multiplicacao();
                 break;
            case '/':
                 resultado = this.divisao();
                 break;
        }
        return resultado;
    }
}
