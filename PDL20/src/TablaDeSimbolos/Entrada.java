package TablaDeSimbolos;

import java.util.ArrayList;

public class Entrada {
    private int posTS;          // Posición en la tabla de símbolos (usada como desplazamiento)
    private String lexema;      // Nombre del identificador (lexema)
    private String tipo;        // Tipo del identificador (inicialmente 'None')
    private int despl;
    private int numParam;
	private String tipoParam;
	private String tipoRetorno;
	private int param;
	private int paramFunc;
	private String etiqueta;
	private boolean esGlobal;
	private ArrayList<Entrada> parametros;

    // Constructor
    public Entrada(int posTS, String lexema) {
        this.posTS = posTS;
        this.lexema = lexema;
        this.tipo = "None";     // Inicializamos el tipo como 'None'
        this.despl = 0;
        this.parametros = new ArrayList<Entrada>();
    }

    // Getters y Setters
    public int getPosTS() {
        return posTS;
    }

    public void setPosTS(int posTS) {
        this.posTS = posTS;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public int getDespl() {
		return despl;
	}
    public void setDespl(int dimension) {
		this.despl = dimension;
	}
    public String getTipoRetorno() {
		return tipoRetorno;
	}

	public void setTipoRetorno(String tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}
	public int getNumParam() {
		return numParam;
	}

	public void setNumParam(int numParam) {
		this.numParam = numParam;
	}

	public String getTipoParam() {
		return tipoParam;
	}

	public void setTipoParam(String tipoParam) {
		this.tipoParam = tipoParam;
	}

	public int getParam() {
		return param;
	}

	public void setParam(int param) {
		this.param = param;
	}

	public ArrayList<Entrada> getParametros() {
		return parametros;
	}

	public void setParametros(ArrayList<Entrada> parametros) {
		this.parametros = parametros;
	}
	 public void setEtiq(String etiq) {
	        this.etiqueta = etiq;
	    }
	 public String getEtiqueta() {
	        return this.etiqueta;
	    }
	 public int getparamFunc() {
	        return paramFunc;
	    }

	    public void setparamFunc(int param) {
	        paramFunc = param;
	    }

		public void setesGlobal(boolean esGlobal) {
			this.esGlobal=esGlobal;
			
		}
		public boolean getesGlobal() {
			return esGlobal;
		}
}
