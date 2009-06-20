package org.dyndns.dainichi.redec;

import processing.core.PImage;

/*************************************************************************
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Complex object, you cannot change it. The "final" keyword
 *  when declaring re and im enforces this rule, making it a
 *  compile-time error to change the .re or .im fields after
 *  they've been initialized.
 *
 *  % java Complex
 *  a            = 5.0 + 6.0i
 *  b            = -3.0 + 4.0i
 *  Re(a)        = 5.0
 *  Im(a)        = 6.0
 *  b + a        = 2.0 + 10.0i
 *  a - b        = 8.0 + 2.0i
 *  a * b        = -39.0 + 2.0i
 *  b * a        = -39.0 + 2.0i
 *  a / b        = 0.36 - 1.52i
 *  (a / b) * b  = 5.0 + 6.0i
 *  conj(a)      = 5.0 - 6.0i
 *  |a|          = 7.810249675906654
 *  tan(a)       = -6.685231390246571E-6 + 1.0000103108981198i
 *
 *************************************************************************/

public class Complex {
    private final float re;   // the real part
    private final float im;   // the imaginary part
    public Complex(double a){ this(a,0);}
    public Complex(float a){this(a,0);}
    public Complex(double a, double b)
    {
    	this((float)a,(float)b);
    }
    // create a new object with the given real and imaginary parts
    public Complex(float real, float imag) {
        re = real;
        im = imag;
    }
   

    // return a string representation of the invoking Complex object
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    // return abs/modulus/magnitude and angle/phase/argument
    public float abs()   { return (float) Math.hypot(re, im); }  // Math.sqrt(re*re + im*im)
    public float phase() { return (float) Math.atan2(im, re); }  // between -pi and pi

    // return a new Complex object whose value is (this + b)
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        float real = a.re + b.re;
        float imag = a.im + b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public Complex minus(Complex b) {
        Complex a = this;
        float real = a.re - b.re;
        float imag = a.im - b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this * b)
    public Complex times(Complex b) {
        Complex a = this;
        float real = a.re * b.re - a.im * b.im;
        float imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    // scalar multiplication
    // return a new object whose value is (this * alpha)
    public Complex times(float alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() {  return new Complex(re, -im); }

    // return a new Complex object whose value is the reciprocal of this
    public Complex reciprocal() {
        float scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public float re() { return re; }
    public float im() { return im; }

    // return a / b
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    public Complex exp() {
        return new Complex((float)(Math.exp(re) * Math.cos(im)), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }
    


    // a static version of plus
    public static Complex plus(Complex a, Complex b) {
        float real = a.re + b.re;
        float imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }

    // sample client for testing
    public static void main(String[] args) {
        Complex a = new Complex(5.0, 6.0);
        Complex b = new Complex(-3.0, 4.0);

        System.out.println("a            = " + a);
        System.out.println("b            = " + b);
        System.out.println("Re(a)        = " + a.re());
        System.out.println("Im(a)        = " + a.im());
        System.out.println("b + a        = " + b.plus(a));
        System.out.println("a - b        = " + a.minus(b));
        System.out.println("a * b        = " + a.times(b));
        System.out.println("b * a        = " + b.times(a));
        System.out.println("a / b        = " + a.divides(b));
        System.out.println("(a / b) * b  = " + a.divides(b).times(b));
        System.out.println("conj(a)      = " + a.conjugate());
        System.out.println("|a|          = " + a.abs());
        System.out.println("tan(a)       = " + a.tan());
    }/*
    public Complex[][] toComplex(PImage in, int mode)
	{
		Complex[][] cp = new Complex[3][in.height * in.width];
		in.loadPixels();
		for (int i = 0; i < 256 * 256; i++)
		{
			if (mode == RGB)
			{
				cp[0][i] = new Complex(hue(bg3.pixels[i]));
				cp[1][i] = new Complex(saturation(bg3.pixels[i]));
				cp[2][i] = new Complex(brightness(bg3.pixels[i]));
			} else
			{
				cp[0][i] = new Complex(red(bg3.pixels[i]));
				cp[1][i] = new Complex(green(bg3.pixels[i]));
				cp[2][i] = new Complex(blue(bg3.pixels[i]));
			}
		}
		return cp;
	}

	public PImage fromComplex(ReDec parent,Complex[][] in, int mode)
	{
		parent.pushStyle();
		parent.colorMode(mode);
		PImage img = new PImage(256, 256);
		img.loadPixels();
		for (int i = 0; i < 256 * 256; i++)
		{
			img.pixels[i] = parent.color(in[0][i].re(), in[1][i].re(), in[2][i].re(), 255f);
		}
		img.updatePixels();
		parent.popStyle();
		return img;
	}

	public Complex[] range(Complex[] input, int mode, int index)
	{
		Complex[] out = new Complex[256];
		for (int i = 0; i < 256; i++)
		{
			if (mode == 1)
			{
				out[i] = input[index + i * 256];
			} else
				out[i] = input[index * 256 + i];

		}
		return out;
	}

	public void fromrange(Complex[] input, int mode, int index, Complex[] out)
	{
		for (int i = 0; i < 256; i++)
		{
			if (mode == 1)
				out[index + i * 256] = input[i];
			else
				out[index * 256 + i] = input[i];
		}
	}*/
}
