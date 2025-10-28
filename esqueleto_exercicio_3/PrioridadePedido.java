enum PrioridadePedido {
    ALTA(1),
    MEDIA(2),
    BAIXA(3);
    
    private final int valor;
    
    PrioridadePedido(int valor) {
        this.valor = valor; 
    }
    
    public int getValor() {
        return valor;
    }
    
    public static PrioridadePedido fromValor(int valor) {
        for (PrioridadePedido p : values()) {
            if (p.valor == valor) {
                return p;
            }
        }
        throw new IllegalArgumentException("Valor de prioridade inválido: " + valor);
    }
}