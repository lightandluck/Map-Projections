/**
 * MIT License
 * 
 * Copyright (c) 2017 Justin Kunimune
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package utils;


/**
 * Because Java apparently doesn't already have a mutable Double
 * 
 * @author jkunimune
 */
public class MutableDouble {
	
	private double value;
	
	public MutableDouble() {
		this(0.);
	}
	
	public MutableDouble(double value) {
		this.value = value;
	}
	
	public double get() {
		return this.value;
	}
	
	public boolean isFinite() {
		return Double.isFinite(this.value);
	}
	
	public boolean isInfinite() {
		return Double.isInfinite(this.value);
	}
	
	public boolean isNaN() {
		return Double.isNaN(this.value);
	}
	
	public boolean isZero() {
		return this.equals(0);
	}
	
	public boolean equals(double value) {
		return this.value == value;
	}
	
	public void set(double value) {
		this.value = value;
	}
	
	public void increment() {
		this.increment(1);
	}
	
	public void increment(double value) {
		this.value += value;
	}
	
	public void decrement() {
		this.decrement(1);
	}
	
	public void decrement(double value) {
		this.value -= value;
	}
	
	public String toString() {
		return "MutableDouble("+value+")";
	}
	
}
