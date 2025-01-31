package Lexico;


public class Token {

	private String codigo;
	private String atributo;
	private int linea;
	
	public Token() {
	}

	public Token(String codigo, String atributo, int linea) {
		this.codigo = codigo;
		this.atributo = atributo;
		this.linea = linea; 
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getAtributo() {
		return atributo;
	}

	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}
	
	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	@Override
	public String toString() {
		return "< " + codigo + " , " + atributo + " >";
	}
	
	
	
}
