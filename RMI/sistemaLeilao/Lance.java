package RMI.sistemaLeilao;

public class Lance {
    
    private final float valor;
    private final long horario;
    private final Cliente cliente;

    public Lance(float v, long h, Cliente c) {
        this.valor = v;
        this.horario = h;
        this.cliente = c;
    }

    public float getValor() {
        return valor;
    }

    public long getHorario() {
        return horario;
    }

    public Cliente getCliente() {
        return cliente;
    }

}
