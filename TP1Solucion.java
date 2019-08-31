package paquete;


import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import unpaz.tallerDeProgramacion.bitmapDisplay.*;
import unpaz.tallerDeProgramacion.BinaryFileTP1Help.BinaryFileReader;
import unpaz.tallerDeProgramacion.BinaryFileTP1Help.BinaryFileWriter;

//@authors Alejandro Vera, Pedro Vera para Taller de programación UNPAZ 2016

public class TP1Solucion implements ImagesOperationsListener{
	
	DataInputStream mis_datos_leidos;
	static Pixel pixeles_entrada[][];
	static Pixel pixeles_mod[][];
	static Pixel pixeles_en_gris[][];
	static int ancho;
	static int alto;
	int offset;
	final static double luminancia_red = 0.299;
	final static double luminancia_green = 0.587;
	final static double luminancia_blue = 0.114;
	String ruta_imagen;
	BinaryFileReader mi_lector;
	BinaryFileWriter mi_escritor;
	
	
	BitmapDisplay miBitmap;
	
	public TP1Solucion() {
	
		miBitmap = new BitmapDisplay(this);
	}
	

	public static void main(String[] args) {
		
		new TP1Solucion();

	}
	
	 public Pixel[][] readImage(String file_name) {//******lee el archivo
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(file_name));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		Pixel[][] pixels = new Pixel[image.getWidth()][image.getHeight()];//esto funciona no tocar!!!

		alto= image.getWidth();//***estan mal los nombres pero asi funciona
		ancho = image.getHeight();
		
		
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int rgb = image.getRGB(i ,j);
				int red = (rgb >> 16) & 0xff;
				int green = (rgb >> 8) & 0xff;
				int blue = (rgb) & 0xff;
				pixels[i][j] = new Pixel(red, green, blue);
			}
		}
		
		pixeles_entrada = pixels;
		
		//***girar a la derecha***
		
		Pixel[][] matriz_correcta = new Pixel[ancho][alto];

		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {

				matriz_correcta[j][alto -1- i] = pixeles_entrada[i][j];

			}

		}
		//***despues de girada hay que cambiar el alto por el ancho!!!
		int contenedor = alto;
		alto = ancho;
		ancho = contenedor;
		
		pixeles_entrada = matriz_correcta;
		
		return pixeles_entrada;//pixels;
		
		}

	public int calcularLuminancia(int red, int green, int blue) {//******************************************

		int luminancia = (int) (luminancia_red * red + luminancia_green * green + luminancia_blue * blue);

		return luminancia;
	}
	
	
	@Override
	public void anotherFilter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculateHistogram() {//*************No anda todavia!!!
		
		Pixel[][]matriz_histograma = new Pixel[512][512];
		Pixel pixel_blanco = new Pixel(255, 255, 255);
		Pixel pixel_negro = new Pixel(0, 0, 0);
		int[] colores_cant = new int [256];
		Pixel [][] array_grises = pixeles_en_gris;
				
		for(int i =0; i<ancho; i++){
			for(int j = 0; j<alto; j++){
				
				int color = array_grises[i][j].getR();
				colores_cant[color]+= 1;  
			}
			
		}//**
		//*llenar de pixeles blancos
		for(int i = 0; i<512; i++){
			for(int j = 0; j<512; j++){
				
				matriz_histograma[i][j]= pixel_negro;
			}
			
		}//***
		
		//a dibujar el histograma Alee!!
		int cont=0;
		for(int i =0; i<512; i++){
			
					cont = colores_cant[i];
			
					for(int n =0; n <cont; n++){
					
						matriz_histograma[i][n]= pixel_negro;
					
						
					}
			
		}//***fin de los for anidados
		
		try {
			
			miBitmap.showResult(matriz_histograma, 512, 512);
		
		} catch (Exception e) {
			
			miBitmap.showErrorMessage("Algo salió mal!!!");
			e.printStackTrace();
		}
		
	}

	@Override
	public void crop(String arg0) {//*******Perfectirijillo!!!**********************************************
		
		LinkedList<String> mi_lista = this.devuelveLaEntradaSinTokens(arg0);
		int x1, y1, x2 ,y2;
		
		if(this.sonAEnterosPositivos(mi_lista)==false){
			
			miBitmap.showErrorMessage("Ingrese enteros válidos por favor");
			return;
		}
		
		if(mi_lista.size()!=4){
			
			miBitmap.showErrorMessage("Ingresó mal los numeros");
			
		}
		
		LinkedList<Integer>datos_recorte = this.parseaAEnteros(mi_lista);
		
		x1= datos_recorte.get(0);
		y1= datos_recorte.get(1);
		x2= datos_recorte.get(2);
		y2= datos_recorte.get(3);
		
		int alto_recorte = (y2-y1)+1;
		int ancho_recorte = (x2-x1)+1;
				
		
		if(x1>= x2){
			
			miBitmap.showErrorMessage("X1 debe ser menor a X2");
		}
		if(y1>= y2){
			
			miBitmap.showErrorMessage("Y1 debe ser menor a Y2");
			
		}
		if(x1>=ancho){
			
			miBitmap.showErrorMessage("X1 deb ser menor a "+ ancho);
			
		}
		if(x2>ancho){
			
			miBitmap.showErrorMessage("X2 debe ser menor a:"+ancho);
		} 
		if(y1>= alto){
			
			miBitmap.showErrorMessage("Y1 debe ser menor a"+alto);
			
		}
		if(y2> alto){
			
			miBitmap.showErrorMessage("Y2 debe ser menor a: "+ alto);
			
		}//**hasta ahi reconoce los errores
		
		Pixel[][] foto_recortada= new Pixel[alto_recorte][ancho_recorte];
		
		for(int i = 0; i< alto_recorte; i++){
			
				for(int j = 0; j< ancho_recorte; j++){
					
			foto_recortada[i][j]= pixeles_entrada[i+x1][j+y1];
					
				
			}
			
		}//****fin de los for anidados
		
		
		try {
			miBitmap.showResult(foto_recortada, ancho_recorte, alto_recorte);
		} catch (Exception e) {
			miBitmap.showErrorMessage("Algo anda mal");
			e.printStackTrace();
		}
	
	}//*************************************************************************

	@Override
	public void equalizate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void filter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flipHorizontal() {//******RESUELTO!!!*****************************
		
		Pixel matriz[][] = pixeles_entrada;
		pixeles_mod = new Pixel[alto][ancho];
		
		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {

				pixeles_mod[i][ancho - j - 1] = matriz[i][j];

			}

		}//***
		
		try {
		
			miBitmap.showResult(pixeles_mod, ancho, alto);
	
		} catch (Exception e) {
			
			miBitmap.showErrorMessage("Algo anda mal!!!");
			
			e.printStackTrace();
		
		}
		
	}

	@Override
	public void flipVertical() {//**********RESUELTO!!!!*****************************************
		Pixel matriz[][] = pixeles_entrada;
		pixeles_mod= new Pixel[alto][ancho];
		
		
		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {

				//pixeles_mod[ancho - (i + 1)][j] = matriz[i][j];
				//pixeles_mod[i][j] = matriz[ancho - (i + 1)][j];
				pixeles_mod[alto -1-i][j] =matriz[i][j]; 
			}

		}
		miBitmap.showResult(pixeles_mod, ancho, alto);
	}

	@Override
	public void grayscale() {//*********SOLUCIONADO!!!!*****************************************************

		pixeles_en_gris = new Pixel[alto][ancho];

		for (int i = 0; i < alto; i++) {// aca calculo la luminancia de cada
											// pixel
			for (int j = 0; j < ancho; j++) {

				int luminancia = calcularLuminancia(pixeles_entrada[i][j].getR(), pixeles_entrada[i][j].getG(), pixeles_entrada[i][j].getB());

				pixeles_en_gris[i][j] = new Pixel(luminancia, luminancia, luminancia);
			}

		} // ***

		
		miBitmap.showResult(pixeles_en_gris, ancho, alto);
		
	}

	@Override
	public void loadImage(String arg0) {//**********Perfecto****************************************
		
		miBitmap.showOriginal(readImage(arg0), ancho, alto);
		
		/*try {
			FileInputStream mi_file_stream = new FileInputStream(arg0);
			this.mis_datos_leidos = new DataInputStream(mi_file_stream);

			leer_cabecera();
			leer_informacion_cabecera();
			
			pixeles_entrada = new Pixel[alto][ancho];
		
			for (int i = alto - 1; i >= 0; i--) {
				
				for (int j = 0; j < ancho; j++) {
				
					int blue = mis_datos_leidos.read();
					int green = mis_datos_leidos.read();
					int red = mis_datos_leidos.read();
					
					pixeles_entrada[i][j] = new Pixel(red, green, blue);
					
				}
				
				for (int x = 0; x < pad(); ++x) {
					mis_datos_leidos.read();

				}
			}
			miBitmap.showOriginal(pixeles_entrada, ancho,
					alto);
			
		} catch (IOException e) {

			miBitmap.showErrorMessage("Algo anda mal!!!");
		}*/
		
	}
	
	public void leer_cabecera() throws IOException {
		
		// BIT MAP FILE HEADER
				char mi_byte;
				mi_byte = (char) mis_datos_leidos.read();
				if ('B' == mi_byte) {
					mi_byte = (char) mis_datos_leidos.read();//ojo con esto!!!!
				}
				readInt();// fileSize
				readShort();// reserved1
				readShort();// reserved2
				offset = readInt();
				// HASTA ACA --14 BYTES DE FILE HEADER
		
	}
	
	public void leer_informacion_cabecera() throws IOException {
		
		readInt();// zizeinfoheader
		
		ancho = readInt();
		alto = readInt();
		
		readShort();// plano_colores
		readShort();// bytes_pixeles
		
		readInt();// Compression
		readInt();// imagen_pura
		readInt();// 1
		readInt();// 2
		readInt();// 3
		readInt();// 4
		
	}
	
	public int readInt() throws IOException {
		
		int b1 = mis_datos_leidos.read();
		int b2 = mis_datos_leidos.read();
		int b3 = mis_datos_leidos.read();
		int b4 = mis_datos_leidos.read();
		
		return ((b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0));
	
	}

	public short readShort() throws IOException {
		
		int b1 = mis_datos_leidos.read();
		int b2 = mis_datos_leidos.read();
		
		return (short) ((b2 << 8) + b1);
	
	}
	
	private int pad() {
		
		int pad = ancho % 4;

		return pad;
	}//****************************************************Chequear pad!!!

	@Override
	public void modifyBrightness(String arg0) {//***************ANDAAAAAAAA***************************
		
		
		pixeles_mod = pixeles_entrada;
		
		try {
		
			int cant_brillo= Integer.parseInt(arg0);
			
			for(int i = 0; i< alto; i++){
				for(int j =0; j< ancho; j++){
					
					if(pixeles_entrada[i][j].getR()+cant_brillo<=255)
					pixeles_mod[i][j].setR(pixeles_entrada[i][j].getR()+cant_brillo);
					if(pixeles_entrada[i][j].getG()+cant_brillo<=255)
					pixeles_mod[i][j].setG(pixeles_entrada[i][j].getG()+cant_brillo);
					if(pixeles_entrada[i][j].getB()+cant_brillo<=255)
					pixeles_mod[i][j].setB(pixeles_entrada[i][j].getB()+cant_brillo);
					
				}
				
			}//**
			
			try {
				miBitmap.showResult(pixeles_mod, ancho, alto);
			} catch (Exception e) {
				miBitmap.showErrorMessage("algo anda mal!!!");
			}
			
		
		} catch (NumberFormatException e) {
			
			miBitmap.showErrorMessage("Ingresó un valor incorrecto!!!");
			
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void modifyContrast(String arg0) {//*****************PERFECTO*************************
		
		pixeles_mod = pixeles_entrada;
		
		try {
			
			double cant_contraste = Double.parseDouble(arg0);
			
			for(int i = 0; i< alto; i++){
				for(int j =0; j< ancho; j++){
					
					if(pixeles_mod[i][j].getR()*cant_contraste<=255)
					pixeles_mod[i][j].setR((int)(pixeles_entrada[i][j].getR()*cant_contraste));
					
					if(pixeles_mod[i][j].getG()*cant_contraste<=255)
					pixeles_mod[i][j].setG((int)(pixeles_entrada[i][j].getG()*cant_contraste));
					
					if(pixeles_mod[i][j].getB()*cant_contraste<=255)
					pixeles_mod[i][j].setB((int)(pixeles_entrada[i][j].getB()*cant_contraste));
					
				}
				
			}//****
			
			
			
		} catch (NumberFormatException e) {
			
			miBitmap.showErrorMessage("Ingresó un valor incorrecto!!!");
			e.printStackTrace();
		
		}
		
		try {
			miBitmap.showResult(pixeles_mod, ancho, alto);//***muestra con contraste
		} catch (Exception e) {
			miBitmap.showErrorMessage("Algo anda mal!!!");
		}
		
	}

	@Override
	public void negative() {//*********************************Perfecto***************************************************

		pixeles_mod = new Pixel[alto][ancho];
		//para cada color 255 - color
		for(int i = 0; i< alto; i++){
			for(int j = 0; j<ancho; j++){
				
				Pixel aux = new Pixel(255 - pixeles_entrada[i][j].getR(),
			    255 - pixeles_entrada[i][j].getG(),255 - pixeles_entrada[i][j].getB());
				
				pixeles_mod[i][j]= aux;
				
			}
			
		}
		
		miBitmap.showResult(pixeles_mod, ancho, alto);
		
	}//*******************************************************************************

	@Override
	public void quantization(String arg0) {//**************SLUCIONADO!!!***************************************************
		
		pixeles_mod = pixeles_en_gris;
		
		try {
			
			int valor = Integer.parseInt(arg0);
			
			if(valor ==0){
				
				miBitmap.showErrorMessage("Ingrese un entero mayor a cero");
				return;
				
			}
			int cant_grises= valor;
			Pixel gris64 = new Pixel(64, 64, 65);
			Pixel gris191 = new Pixel(191, 191, 191);
			Pixel gris42 = new Pixel(42, 42, 42);
			Pixel gris127 = new Pixel(127, 127, 127);
			Pixel gris212 = new Pixel(212, 212, 212);
			Pixel gris32 = new Pixel(32, 32, 32);
			Pixel gris96 = new Pixel(96, 96, 96);
			Pixel gris159 = new Pixel(159, 159, 159);
			Pixel gris223 = new Pixel(223, 223, 223);
			Pixel gris16 = new Pixel(16, 16, 16);
			Pixel gris48 = new Pixel(48, 48, 48);
			Pixel gris80 = new Pixel(80, 80, 80);
			Pixel gris112 = new Pixel(112, 112, 112);
			Pixel gris143 = new Pixel(143, 143, 143);
			Pixel gris175 = new Pixel(175, 175, 175);
			Pixel gris207 = new Pixel(207, 207, 207);
			Pixel gris239 = new Pixel(239, 239, 239);
			
			for(int i = 0; i<alto; i++){
				for(int j = 0; j<ancho; j++){
					
					if(cant_grises>256){
						
						miBitmap.showErrorMessage("ingrese un entero menor a 256 y mayor a 1");
						return;
					}
					if(cant_grises<2){
						
						miBitmap.showErrorMessage("Ingrese 2 o mayor a dos");
						return;
						
					}else{
						if(cant_grises==2){//****************
							
							if(pixeles_en_gris[i][j].getB()<=127){
								
								pixeles_mod[i][j]= gris64;
							}else{
								
								pixeles_mod[i][j] = gris191;
								
							}
							
						}//*********************
						if(cant_grises==3){//***************
							if(pixeles_en_gris[i][j].getB()<=85){
								
								pixeles_mod[i][j]= gris42;
								
							}else{
								if(pixeles_en_gris[i][j].getB()<=170){
									
									pixeles_mod[i][j] = gris127;
								}else{
									
									pixeles_mod[i][j] = gris212;
									
								}
								
								
							}
							
							
						}///***********************
						
						if(cant_grises==4){//********************
							
							if(pixeles_en_gris[i][j].getB()<=64){
								
								pixeles_mod[i][j]= gris32;
								
							}else{
								if(pixeles_en_gris[i][j].getB()<=127){
									
									pixeles_mod[i][j]= gris96;
									
								}else{
									if(pixeles_en_gris[i][j].getB()<=191){
										
										pixeles_mod[i][j]= gris159;
										
									}else{
										
										pixeles_mod[i][j]= gris223;
										
									}
									
									
								}
								
								
								
							}
							
							
						}//*******************************
						if(cant_grises>4){
							if(pixeles_en_gris[i][j].getB()<=32){
								
								pixeles_mod[i][j]= gris16;
								
							}else{
								if(pixeles_en_gris[i][j].getB()<=64){
									
									pixeles_mod[i][j]= gris48;
									
								}else{
									if(pixeles_en_gris[i][j].getB()<=96){
										
										pixeles_mod[i][j]= gris80;
										
									}else{
										if(pixeles_en_gris[i][j].getB()<=127){
											
											pixeles_mod[i][j]= gris112;
											
										}else{
											if(pixeles_en_gris[i][j].getB()<=159){
												
												pixeles_mod[i][j]= gris143;
											}else{
												if(pixeles_en_gris[i][j].getB()<=191){
													
													pixeles_mod[i][j]= gris175;
													
												}else {
													if(pixeles_en_gris[i][j].getB()<=223){
														
														pixeles_mod[i][j]= gris207;
														
													}else{
														pixeles_mod[i][j] = gris239;
															
													}
												}
												
											}
											
										}
										
										
									}
									
									
								}
								
							}
							
							
						}
								
					}//**************************************
		
				}
				
			}//*fin for anidados
			
			miBitmap.showResult(pixeles_mod, ancho, alto);
			
			
		} catch (NumberFormatException e) {
			
			miBitmap.showErrorMessage("Ingresó un num no válido!!!");
			
			e.printStackTrace();
		}
		
		
	}//*****************************************************************

	@Override
	public void reduce(String arg0) {//*****
		
		LinkedList<String> entrada_sin_tokens = devuelveLaEntradaSinTokens(arg0);
		LinkedList<Integer>entrada_enteros = parseaAEnteros(entrada_sin_tokens);
		
		if(!sonAEnterosPositivos(entrada_sin_tokens)){
			
			miBitmap.showErrorMessage("No ingresó valores correctos...");
			return;
		}
		if(entrada_sin_tokens.size()!=2){
			
			miBitmap.showErrorMessage("ingrese solo dos valores por favor!!!");
			return;
		}
		
		for(Integer i: entrada_enteros){
			
			if(i==0){
				
				miBitmap.showErrorMessage("Ingrese valores mayores a cero");
				return;
				
			}
			
		}
		
		int valor_x = entrada_enteros.get(0);
		int valor_y = entrada_enteros.get(1);
		
		int ancho_reducido = (int)ancho/valor_y;
		int alto_reducido = (int) alto/valor_x;
		int mod_ancho = ancho % valor_y;
		int mod_alto = alto % valor_x;
		
		Pixel contenedor;
		pixeles_mod = new Pixel[alto_reducido][ancho_reducido];
		
		Pixel matriz[][]= pixeles_entrada;
			
	}

	@Override
	public void rotate180() {//********FUNCIONA!!!*****************************
		
		pixeles_mod = new Pixel[alto][ancho];
		
		for(int i=0; i< alto; i++){
			for(int j = 0; j<ancho; j++){
				
				pixeles_mod[alto - 1 - i][ancho -1 - j]= pixeles_entrada[i][j];
				
			}
			
			
		}//**
		
		miBitmap.showResult(pixeles_mod, ancho, alto);
		
	}//*********************************************************************

	@Override
	public void rotate270() {//*************RESUELTOOO!!*****************

		pixeles_mod = new Pixel[ancho][alto];

		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {

				pixeles_mod[j][alto -i -1]= pixeles_entrada[i][j];//90grados izq
			}

		} // **

		miBitmap.showResult(pixeles_mod, alto, ancho);

	}//*****************************************************************

	@Override
	public void rotate90() {//**************SOLUCIONADO!!!*********************
		
		pixeles_mod = new Pixel[ancho][alto];
		
		for(int i = 0; i< alto; i++){
			for(int j = 0; j< ancho; j++){
				
				pixeles_mod[ancho-j-1][i]= pixeles_entrada[i][j];
				
			}
			
		}
		
		miBitmap.showResult(pixeles_mod, alto, ancho);//*******MIRAR
	
	}//*****************************************************************

	@Override
	public void saveResult(String arg0) {//************************Resolver issues
		
		
		
	}
	
	public LinkedList<String> devuelveLaEntradaSinTokens(String entrada_teclado){
		
		LinkedList<String> resultado = new LinkedList<>();

		StringTokenizer st = new StringTokenizer(entrada_teclado, "(,);");

		while (st.hasMoreTokens()) {

			resultado.addLast(st.nextToken());

		}
		
		return resultado;
	} 

	public boolean sonAEnterosPositivos(LinkedList<String> lista){
		
		for(String s: lista){
			
			try {
				
				Integer.parseInt(s);
				
				if(Integer.parseInt(s)<0){
					
					return false;
					
				}
				
			} catch (NumberFormatException e) {
			
				return false;
			}	
		}
		
		return true;
	}
	
	public LinkedList<Integer> parseaAEnteros(LinkedList<String> lista){
		
		LinkedList<Integer> mis_enteros = new LinkedList<>();
		
		if(sonAEnterosPositivos(lista)){
			
		for(String s:lista){
			
			mis_enteros.addLast(Integer.parseInt(s));
			
			}	
			
		}

		return mis_enteros;
	}
}
