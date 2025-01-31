package TablaDeSimbolos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class tablaDeSimbolos {
	private int id;
	private int pos;
	private HashMap <String, Entrada> tabla;
	private HashMap<Integer, String> tablaAux;
	private Entrada entrada;
	private int numP;
	private boolean esGlobal=false;

	public tablaDeSimbolos (int id) {
		tabla = new HashMap<>();
		tablaAux = new HashMap<>();
		pos = 1;
		this.id=id;
	}
	
	// Aadir un identificador si no está en la tabla
	public boolean add(String id) {
		if (!tabla.containsKey(id)) {
			
			entrada = new Entrada(pos, id);
			tabla.put(id, entrada);
			pos++;
			
			return true;
		}
		return false;
	}
	
	public String getNombreIdent(int pos) {
        Entrada ent = tabla.get(tablaAux.get(pos));
        return ent.getEtiqueta();
    }
	
	public int posTS(String id) {
		if (tabla.containsKey(id)) {
			return tabla.get(id).getPosTS();
		}
		return -1;
	}
	public String getId(int posTS) {
	    for (HashMap.Entry<String, Entrada> entry : tabla.entrySet()) {
	        Entrada entrada = entry.getValue();
	        if (entrada.getPosTS() == posTS ) {
	            return entry.getKey(); 
	        }
	    }
	    return null; 
	}
	public void setNumP(int n) {
		numP=n;
	}
	public void esGlobal() {
		esGlobal=true;
		
	}

	public void addTipoyDespl(String num, String tipo, int despl) {
		int numero = Integer.parseInt(num);
		String id = getId(numero);
		if (id != null) {
			Entrada entradaNueva = tabla.get(id);
			entradaNueva.setTipo(tipo);
			entradaNueva.setDespl(despl);
			if(tipo.equals("funcion")) {
				entradaNueva.setparamFunc(numP);
				numP=0;
			}
			entradaNueva.setesGlobal(esGlobal);
			//System.out.println("Numero "+ num);
			//System.out.println("Es global??: "+ esGlobal);
			esGlobal=false;
			tabla.replace(id, entradaNueva);
		}
	}
	
	// Busca el tipo del identificador
	public String buscaTipo(String num) {
	    int numero = Integer.parseInt(num);
	    String tipo = null, id;
	    id = getId(numero);  // Obtiene el identificador asociado al n�mero
	    if (id != null) {
	        tipo = tabla.get(id).getTipo();  // Obtiene el tipo del identificador
	        if (tipo.equals("funcion")) {  // Si es una funci�n, obtener el tipo de retorno
	            tipo = getTipoRetorno(num);  // Obtener el tipo de retorno de la funci�n
	        }
	    }
	    if (tipo == null)
	        tipo = "None";  // Si no se encuentra, devuelve "None"
	    return tipo;  
	}
	
	public Boolean esFunc(String num) {
	    int numero = Integer.parseInt(num);
	    String tipo = null, id;
	    id = getId(numero);  // Obtiene el identificador asociado al n�mero
	    if (id != null) {
	        tipo = tabla.get(id).getTipo();  // Obtiene el tipo del identificador
	        
	    }
	    return tipo.equals("funcion");
	}

	// Devuelve el tipo de retorno de la funci�n
	public String getTipoRetorno(String num) {
	    int numero = Integer.parseInt(num);
	    String id = getId(numero);  // Obtiene el ID asociado al n�mero
	    if (id != null) {
	        Entrada entrada = tabla.get(id);  // Obtiene la entrada de la tabla de s�mbolos
	        return entrada.getTipoRetorno();  // Devuelve el tipo de retorno de la funci�n
	    }
	    return "None";  // Si no se encuentra, devuelve "None"
	}

	public void addTipoRet(String num, String tipo) {
		int numero = Integer.parseInt(num);
		String id = getId(numero);
		if (id != null) {
			Entrada entradaNueva = tabla.get(id);
			entradaNueva.setTipoRetorno(tipo);
			tabla.replace(id, entradaNueva);
		}
	}
	public void setParam (String num) {
		int numero = Integer.parseInt(num);
		String id = getId(numero);
		if (id != null) {
			Entrada entradaNueva = tabla.get(id);
			entradaNueva.setParam(1);
			tabla.replace(id, entradaNueva);
		}
	}
	public void setNumParam(String num, int numParams) {
		int numero = Integer.parseInt(num);
		String id = getId(numero);
		if (id != null) {
			Entrada entradaNueva = tabla.get(id);
			entradaNueva.setNumParam(numParams);
			tabla.replace(id, entradaNueva);
		}
	}
	public void setParamFunc(String numFunc, ArrayList<String> num) {
	    ArrayList<Entrada> listaParametros = new ArrayList<>();  // Lista para guardar las entradas de los par�metros
	    StringBuilder tiposParametrosConcatenados = new StringBuilder();  // Guardar� los tipos concatenados para referencia
	    int numero, numParam = 0;
	    String id;

	    // Recorre los IDs de los par�metros
	    for (String posTS : num) {
	        numero = Integer.parseInt(posTS);
	        id = getId(numero);  // Obtener el lexema del par�metro
	        if (id != null) {
	            Entrada entradaParametro = tabla.get(id);  // Obtener la entrada del par�metro
	            listaParametros.add(entradaParametro);  // Agregar entrada a la lista
	            tiposParametrosConcatenados.append(entradaParametro.getTipo()).append(", ");  // Concatenar el tipo
	            numParam++;
	        }
	    }

	    // Remover la �ltima coma y espacio si existen
	    if (tiposParametrosConcatenados.length() > 0) {
	        tiposParametrosConcatenados.setLength(tiposParametrosConcatenados.length() - 2);
	    }

	    // Actualizar la funci�n en la tabla de s�mbolos
	    numero = Integer.parseInt(numFunc);
	    id = getId(numero);
	    if (id != null) {
	        Entrada entradaFuncion = tabla.get(id);  // Obtener la entrada de la funci�n
	        entradaFuncion.setParametros(listaParametros);  // Guardar la lista de entradas como par�metros
	        entradaFuncion.setNumParam(numParam);  // Guardar el n�mero de par�metros
	        entradaFuncion.setTipoParam(tiposParametrosConcatenados.toString());  // Guardar los tipos concatenados
	        tabla.replace(id, entradaFuncion);  // Reemplazar en la tabla
	    }
	}
	// Retorna una lista con los tipos de los par�metros de la funci�n en orden
	public ArrayList<String> getParamFunc(String idFunc) {
	    if (!tabla.containsKey(idFunc)) {
	        return null;  // Si la funci�n no est� en la TS, devuelve null
	    }
	    Entrada entrada = tabla.get(idFunc);
	    if (!entrada.getTipo().equals("funcion")) {
	        return null;  // Si el ID no corresponde a una funci�n, devuelve null
	    }

	    ArrayList<String> tiposParametros = new ArrayList<>();
	    for (Entrada param : entrada.getParametros()) {
	        tiposParametros.add(param.getTipo());  // Guardar el tipo de cada par�metro
	    }
	    return tiposParametros;  // Retornar la lista con los tipos de par�metros
	}

	
	public ArrayList<String> getTiposParametros(String idFunc) {
	    if (!tabla.containsKey(idFunc)) {
	    	//System.out.println("no existe func:   "+ Integer.parseInt(idFunc));
	        return null;
	        
	    }
	    Entrada entrada = tabla.get(idFunc);
	    if (entrada.getTipo().equals("funcion")) {
	        ArrayList<String> tiposParametros = new ArrayList<>();
	        for (Entrada param : entrada.getParametros()) {
	            tiposParametros.add(param.getTipo());
	            //System.out.println("existe parametro: "+param);
	        }
	        return tiposParametros;
	    }
	    return null;  // No es una funci�n
	}
	// Devuelve los nombres de los par�metros de la funci�n dada por su idFunc
	public ArrayList<String> getNombresParametros(String idFunc) {
	    if (!tabla.containsKey(idFunc)) {
	        return null;  // Si la funci�n no est� en la tabla, devuelve null
	    }
	    Entrada entrada = tabla.get(idFunc);
	    if (!entrada.getTipo().equals("funcion")) {
	        return null;  // Si no es una funci�n, devuelve null
	    }

	    ArrayList<String> nombresParametros = new ArrayList<>();
	    for (Entrada param : entrada.getParametros()) {
	        nombresParametros.add(param.getLexema());  // A�adir el nombre del par�metro
	    }
	    return nombresParametros;  // Retornar la lista con los nombres de los par�metros
	}

	

	public String toString() {
	    StringBuilder TS = new StringBuilder("TABLA #0 :\n");
	    HashMap<Integer, StringBuilder> tablasFunciones = new HashMap<>();  // Map para tablas de funciones
	    int tablaIndex = 1;  // �ndice de las tablas de funciones
	    ArrayList<Integer> omitidos = new ArrayList<>();  // Lista de posiciones a omitir en la TABLA #0

	    // Convertir el HashMap en una lista ordenada por posTS
	    List<Entrada> listaOrdenada = new ArrayList<>(tabla.values());
	    listaOrdenada.sort(Comparator.comparingInt(Entrada::getPosTS));  // Orden ascendente por posTS

	    // **Primera fase: Recorremos para identificar qu� posiciones se deben omitir y crear las tablas de funciones**
	    for (Entrada entrada : listaOrdenada) {
	        if (entrada.getTipo().equals("funcion")) {  // Si es funci�n
	        	int numParametros = entrada.getNumParam();
	        	int i=0;
	            int posicionesASaltar = entrada.getparamFunc();  // Variables locales a excluir
	            //System.out.println("Posiciones a saltar: " + posicionesASaltar);
	            StringBuilder tablaFuncion = new StringBuilder("TABLA #" + tablaIndex + " (Variables locales y parametros de la funcion '" + entrada.getLexema() + "'):\n");

	            int posActual = entrada.getPosTS() + 1;  // Primera posici�n despu�s de la funci�n
	            int posicionesSaltadas = 0;

	            // Recorremos solo en orden ascendente por las posiciones siguientes
	            for (Entrada subEntrada : listaOrdenada) {
	                if (subEntrada.getPosTS() >= posActual && posicionesSaltadas < posicionesASaltar) {
	                	if(i<numParametros) {
	                		String lexema = subEntrada.getLexema();
	                		lexema = lexema.substring(2);
	                		tablaFuncion.append(" * Lexema : '").append(lexema).append("'\n")
                            .append("   Atributos :\n")
                            .append("   + tipo : '").append(subEntrada.getTipo()).append("'\n")
                            .append("   + despl : ").append(subEntrada.getDespl()).append("\n")
                            .append("---------------------------------\n");
                    omitidos.add(subEntrada.getPosTS());  // A�adir a omitidos
                    posicionesSaltadas++;
                    i++;
	                	}else {
	                		//System.out.println("Analizando lexema: " + subEntrada.getLexema() + ", Posici�n: " + subEntrada.getPosTS() + ", es global?: " + subEntrada.getesGlobal());
		                    if (!subEntrada.getesGlobal()) {  // Si no es global, se agrega a la tabla de la funci�n
		                        tablaFuncion.append(" * Lexema : '").append(subEntrada.getLexema()).append("'\n")
		                                .append("   Atributos :\n")
		                                .append("   + tipo : '").append(subEntrada.getTipo()).append("'\n")
		                                .append("   + despl : ").append(subEntrada.getDespl()).append("\n")
		                                .append("---------------------------------\n");
		                        omitidos.add(subEntrada.getPosTS());  // A�adir a omitidos
		                        posicionesSaltadas++;
		                    } 
	                	}
	                }
	            }
	            //System.out.println(omitidos);
	            
	            tablasFunciones.put(tablaIndex, tablaFuncion);  // Guardar tabla de la funci�n
	           
	            tablaIndex++;
	        }
	    }

	    //System.out.println("omitidos: " + omitidos);

	    // **Segunda fase: Recorrer e imprimir la TABLA #0 sin los omitidos**
	    for (Entrada entrada : listaOrdenada) {
	        if (!omitidos.contains(entrada.getPosTS())) {  // Solo imprimir si no est� en omitidos
	            if (!entrada.getTipo().equals("funcion")) {
	                TS.append(" * Lexema : '").append(entrada.getLexema()).append("'\n")
	                        .append("   Atributos :\n")
	                        .append("   + tipo : '").append(entrada.getTipo()).append("'\n")
	                        .append("   + despl : ").append(entrada.getDespl()).append("\n")
	                        .append("---------------------------------\n");
	            } else {
	                TS.append(" * Lexema : '").append(entrada.getLexema()).append("'\n")
	                        .append("   Atributos :\n")
	                        .append("   + tipo : '").append(entrada.getTipo()).append("'\n")
	                        .append("     + numParam : '").append(entrada.getNumParam()).append("'\n");

	                ArrayList<Entrada> parametros = entrada.getParametros();
	                for (int i = 0; i < parametros.size(); i++) {
	                    TS.append("       + TipoParam").append(i + 1).append(" : '").append(parametros.get(i).getTipo()).append("'\n");
	                }

	                TS.append("       + TipoRetorno : '").append(entrada.getTipoRetorno()).append("'\n")
	                        .append("     + EtiqFuncion : '").append(entrada.getLexema()).append("'\n")
	                        .append("---------------------------------\n");
	            }
	        }
	    }

	    // **Tercera fase: Imprimir las tablas de funciones al final**
	    for (int i = 1; i <= tablasFunciones.size(); i++) {
	        TS.append("\n").append(tablasFunciones.get(i));
	    }

	    return TS.toString();
	}
}

