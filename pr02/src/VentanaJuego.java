

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.*;

/** Clase principal de minijuego de coche para Práctica 02 - Prog III
 * Ventana del minijuego.
 * @author Andoni Eguíluz
 * Facultad de Ingeniería - Universidad de Deusto (2014)
 */
public class VentanaJuego extends JFrame {
	private static final long serialVersionUID = 1L;  // Para serialización
	JPanel pPrincipal;         // Panel del juego (layout nulo)
	MundoJuego miMundo;        // Mundo del juego
	CocheJuego miCoche;        // Coche del juego
	MiRunnable miHilo = null;  // Hilo del bucle principal de juego	
	public Boolean[] pulsaciones = new Boolean[4];
	private int puntos=0;
	private int perdidas=0;
	private JLabel lMensaje;
	/** Constructor de la ventana de juego. Crea y devuelve la ventana inicializada
	 * sin coches dentro
	 */
	public VentanaJuego() {
		// Liberación de la ventana por defecto al cerrar
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		// Creación contenedores y componentes
		pPrincipal = new JPanel();
		JPanel pBotonera = new JPanel();
		JButton bAcelerar = new JButton( "Acelera" );
		JButton bFrenar = new JButton( "Frena" );
		JButton bGiraIzq = new JButton( "Gira Izq." );
		JButton bGiraDer = new JButton( "Gira Der." );
		lMensaje= new JLabel("Puntos 0 - Estrellas perdidas 0");
		// Formato y layouts
		pPrincipal.setLayout( null );
		pPrincipal.setBackground( Color.white );
		// Añadido de componentes a contenedores
		add( pPrincipal, BorderLayout.CENTER );
		//pBotonera.add( bAcelerar );
		//pBotonera.add( bFrenar );
		//pBotonera.add( bGiraIzq );
		//pBotonera.add( bGiraDer );
		pBotonera.add( lMensaje );
		add( pBotonera, BorderLayout.SOUTH );
		// Formato de ventana
		setSize( 1000, 750 );
		setResizable( false );
		// Escuchadores de botones
		bAcelerar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.acelera( +10, 1 );
				// System.out.println( "Nueva velocidad de coche: " + miCoche.getVelocidad() );
			}
		});
		bFrenar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.acelera( -10, 1 );
				// System.out.println( "Nueva velocidad de coche: " + miCoche.getVelocidad() );
			}
		});
		bGiraIzq.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.gira( +10 );
				// System.out.println( "Nueva dirección de coche: " + miCoche.getDireccionActual() );
			}
		});
		bGiraDer.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.gira( -10 );
				// System.out.println( "Nueva dirección de coche: " + miCoche.getDireccionActual() );
			}
		});
		
		// Añadido para que también se gestione por teclado con el KeyListener
		
		
		Arrays.fill(pulsaciones, Boolean.FALSE);
		
		pPrincipal.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: {
					pulsaciones[0]= true;
					break;
				}
				case KeyEvent.VK_DOWN: {
					pulsaciones[1]= true;
					break;
				}
				case KeyEvent.VK_LEFT: {
					pulsaciones[2]= true;
					break;
				}
				case KeyEvent.VK_RIGHT: {
					pulsaciones[3]= true;
					break;
				}
			}
//				switch (e.getKeyCode()) {
//					case KeyEvent.VK_UP: {
//						miCoche.acelera( +5, 1 );
//						break;
//					}
//					case KeyEvent.VK_DOWN: {
//						miCoche.acelera( -5, 1 );
//						break;
//					}
//					case KeyEvent.VK_LEFT: {
//						miCoche.gira( +10 );
//						break;
//					}
//					case KeyEvent.VK_RIGHT: {
//						miCoche.gira( -10 );
//						break;
//					}
//				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: {
					pulsaciones[0]= false;
					break;
				}
				case KeyEvent.VK_DOWN: {
					pulsaciones[1]= false;
					break;
				}
				case KeyEvent.VK_LEFT: {
					pulsaciones[2]= false;
					break;
				}
				case KeyEvent.VK_RIGHT: {
					pulsaciones[3]= false;
					break;
				}
				}
			}
		});
		pPrincipal.setFocusable(true);
		pPrincipal.requestFocus();
		pPrincipal.addFocusListener( new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				pPrincipal.requestFocus();
			}
		});
		// Cierre del hilo al cierre de la ventana
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (miHilo!=null) miHilo.acaba();
			}
		});
	}
	
	/** Programa principal de la ventana de juego
	 * @param args
	 */
	public static void main(String[] args) {
		// Crea y visibiliza la ventana con el coche
		try {
			final VentanaJuego miVentana = new VentanaJuego();
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override
				public void run() {
					miVentana.setVisible( true );
				}
			});
			miVentana.miMundo = new MundoJuego( miVentana.pPrincipal );
			miVentana.miMundo.creaCoche( 150, 100 );
			miVentana.miCoche = miVentana.miMundo.getCoche();
			miVentana.miCoche.setPiloto( "Fernando Alonso" );
			miVentana.miMundo.creaEstrella();
			// Crea el hilo de movimiento del coche y lo lanza
			miVentana.miHilo = miVentana.new MiRunnable();  // Sintaxis de new para clase interna
			Thread nuevoHilo = new Thread( miVentana.miHilo );
			nuevoHilo.start();
		} catch (Exception e) {
			System.exit(1);  // Error anormal
		}
	}
	
	/** Clase interna para implementación de bucle principal del juego como un hilo
	 * @author Andoni Eguíluz
	 * Facultad de Ingeniería - Universidad de Deusto (2014)
	 */
	class MiRunnable implements Runnable {
		boolean sigo = true;
		@Override
		public void run() {
			// Bucle principal forever hasta que se pare el juego...
			while (sigo) {
				// Mover coche
				
				if (pulsaciones[0]){
					miMundo.aplicarFuerza(miCoche.fuerzaAceleracionAdelante(), miCoche);
					//miCoche.acelera( miCoche.fuerzaAceleracionAdelante(), 1 );
				}
				if (pulsaciones[1]){
					//miCoche.acelera( miCoche.fuerzaAceleracionAtras(), 1 );
					miMundo.aplicarFuerza(miCoche.fuerzaAceleracionAtras(), miCoche);
				}
				if (pulsaciones[2]){
					miCoche.gira( +10 );
				}
				if (pulsaciones[3]){
					miCoche.gira( -10 );
				}
				miCoche.mueve( 0.040 );
				// Chequear choques
				// (se comprueba tanto X como Y porque podría a la vez chocar en las dos direcciones (esquinas)
				if (miMundo.hayChoqueHorizontal(miCoche)) // Espejo horizontal si choca en X
					miMundo.rebotaHorizontal(miCoche);
				if (miMundo.hayChoqueVertical(miCoche)) // Espejo vertical si choca en Y
					miMundo.rebotaVertical(miCoche);
				
				
				if (Math.abs(System.currentTimeMillis()-miMundo.getUltimaCreacion())>1200){
					miMundo.creaEstrella();
				}
				quitaYRotaEstrellas(6000);
				//dibujarEstrellas();
				
				if (choquesConEstrellas()==1){
					puntos+=5;
					lMensaje.setText("Puntos "+puntos+ " - Estrellas perdidas " +  perdidas);
				}
				
				if (perdidas>=10){
					lMensaje.setText("SE ACABO EL JUEGO - Has sacado " +puntos + " PUNTOS");
					sigo= false;
				}
				// Dormir el hilo 40 milisegundos
				try {
					Thread.sleep( 40 );
				} catch (Exception e) {
				}
			}
		}
		
		/** Ordena al hilo detenerse en cuanto sea posible
		 */
		public void acaba() {
			sigo = false;
		}
	};
//	private void dibujarEstrellas() {
//		Estrella est;
//		// TODO Auto-generated method stub
//		for (int i =0;i<miMundo.getEstrellas().size();i++){
//			est= miMundo.getEstrellas().get(i);
//			//pPrincipal.remove(est.getGrafico());
//			est.getGrafico().setGiro(10);
//			pPrincipal.add(est.getGrafico());
//	
//		}
//		pPrincipal.repaint();
//	}
	
	/** Quita todas las estrellas que lleven en pantalla demasiado tiempo   * y rota 10 grados las que sigan estando   * @param maxTiempo  Tiempo máximo para que se mantengan las estrellas (msegs)   * @return  Número de estrellas quitadas */  
	public int quitaYRotaEstrellas( long maxTiempo ){
		try{
		Estrella est;
		//for (int i =0;i<miMundo.getEstrellas().size();i++){
		for (int i =miMundo.getEstrellas().size()-1;i>=0;i--){
		//for (Iterator<Estrella> it = miMundo.getEstrellas().iterator();it.hasNext();){
			//est= it.next();
			est= miMundo.getEstrellas().get(i);
			if (Math.abs(System.currentTimeMillis()- est.getTiempo())>maxTiempo){
				pPrincipal.remove(est.getGrafico());
				miMundo.getEstrellas().remove(est);
				perdidas++;
				lMensaje.setText("Puntos "+puntos+ " - Estrellas perdidas " +  perdidas);
			}else{
				est.getGrafico().setGiro(10);
				pPrincipal.add(est.getGrafico());
			}
		}	
		pPrincipal.repaint();
		}
		catch(Exception e){
			
		}
		return 0;
	}
	

	
	
	public int choquesConEstrellas(){
		try{
		Estrella est;
//		for (Iterator<Estrella> it = miMundo.getEstrellas().iterator();it.hasNext();){
//			est= it.next();
//			if (miMundo.hayChoque(miCoche, est)){
//				pPrincipal.remove(est.getGrafico());
//				miMundo.getEstrellas().remove(est);
//				return 1;
//			}
//		}
		for (int i =miMundo.getEstrellas().size()-1;i>=0;i--){
			est= miMundo.getEstrellas().get(i);
			if (miMundo.hayChoque(miCoche, est)){
				pPrincipal.remove(est.getGrafico());
				miMundo.getEstrellas().remove(est);
				return 1;
			}
		}}
		catch(Exception e){
			
		}
		return 0;
	}
}
