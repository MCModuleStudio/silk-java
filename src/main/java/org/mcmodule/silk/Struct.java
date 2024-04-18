package org.mcmodule.silk;

import sun.misc.Unsafe;

public abstract class Struct implements Cloneable {

	private static final Unsafe UNSAFE;
	private long memory;

	public Struct() {
		this.memory = allocateMemory(getStructSize());
	}
	
	private long allocateMemory(int structSize) {
		long memory = UNSAFE.allocateMemory(structSize);
		if (memory != 0L) {
			UNSAFE.setMemory(memory, structSize, (byte) 0);
			return memory;
		}
		throw new OutOfMemoryError();
	}

	protected abstract int getStructSize();
	
	protected final int getInteger(int offset) {
		long memory = this.memory;
		return memory == 0L ? 0 : UNSAFE.getInt(memory + offset);
	}
	
	protected final void setInteger(int offset, int val) {
		long memory = this.memory;
		if (memory != 0L) {
			UNSAFE.putInt(memory + offset, val);
		}
	}
	
	long getMemory() {
		return this.memory;
	}
	
	@Override
	protected final void finalize() throws Throwable {
		super.finalize();
		if (this.memory != 0L) {
			UNSAFE.freeMemory(this.memory);
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			Struct struct = (Struct) UNSAFE.allocateInstance(this.getClass());
			int structSize = getStructSize();
			struct.memory = allocateMemory(structSize);
			UNSAFE.copyMemory(this.memory, struct.memory, structSize);
			return struct;
		} catch (InstantiationException e) {
			throw new CloneNotSupportedException();
		}
	}
	
	static {
		UNSAFE = Native.getUnsafe();
	}
}
