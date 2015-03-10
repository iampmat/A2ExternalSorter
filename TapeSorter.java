package Sort;

/**
 * Represents a machine with limited memory that can sort tape drives.
 */
public class TapeSorter {

	private int memorySize;
	private int tapeSize;
	public int[] memory;

	public TapeSorter(int memorySize, int tapeSize) {
		this.memorySize = memorySize;
		this.tapeSize = tapeSize;
		this.memory = new int[memorySize];
	}

	public int tapeSize(TapeDrive tape) {
		int i = 0;
		int a = tape.read();
		int b = tape.read();
		i++;
		while (a != b) {
			b = tape.read();
			i++;
		}
		tape.reset();
		return i;

	}

	public void quicksort(int size) {
		int max = size - 1;
		int min = 0;
		quicksort(min, max);

	}

	private void quicksort(int left, int right) {
		int pivot = memory[left + (right - left) / 2];
		int max = right;
		int min = left;
		while (min < max) {

			while (memory[min] < pivot) {
				min++;
			}
			while (memory[max] > pivot) {
				max--;
			}
			if (min <= max) {
				int tmp = memory[max];
				memory[max] = memory[min];
				memory[min] = tmp;
				min++;
				max--;

			}

		}

		if (left < max) {

			quicksort(left, max);
		}
		if (min < right) {
			quicksort(min, right);
		}

	}

	
	public void initialPass(TapeDrive in, TapeDrive out1, TapeDrive out2) {
		int i = 0;
		int a = 0;
		int b = 0;

		while (i < tapeSize) {
			for (int k = 0; k < memorySize; k++) {
				if (b < tapeSize) {
					memory[k] = in.read();
					b++;
				}
			}
			if (b < tapeSize) {
				quicksort(memorySize);
			}
			if (b >= tapeSize) {
				quicksort(b - i);
			}

			if (a == 0) {

				for (int j = 0; j < memorySize; j++) {
					if (i < tapeSize) {

						out1.write(memory[j]);

						i++;
					} else {
						break;
					}
				}
				a = 1;
			} else if (a == 1) {
				for (int k = 0; k < memorySize; k++) {
					if (i < tapeSize) {
						out2.write(memory[k]);
						i++;
					} else {
						break;
					}
				}
				a = 0;
			}

		}

		out1.reset();
		out2.reset();
		in.reset();
	}

	
	public void mergeChunks(TapeDrive in1, TapeDrive in2, TapeDrive out,
			int size1, int size2) {

		int length = size1 + size2;
		int a = 0;
		int num = in1.read();
		int num1 = in2.read();
		while (a < length) {

			if (num <= num1 && size1 != 0) {

				out.write(num);
				size1--;
				if (size1 != 0) {
					num = in1.read();
				}
				a++;
			} else if (num1 < num && size2 != 0) {
				out.write(num1);
				size2--;
				if (size2 != 0) {
					num1 = in2.read();
				}
				a++;
			}
			if (size1 == 0) {
				out.write(num1);

				a++;
				if (a >= length) {
					break;
				}

				num1 = in2.read();

			}
			if (size2 == 0) {
				out.write(num);
				a++;
				if (a >= length) {
					break;
				}
				num = in1.read();

			}

		}

	}

	public void doRun(TapeDrive in1, TapeDrive in2, TapeDrive out1,
			TapeDrive out2, int runNumber) {
		int a = 1;
		int b = 0;

		int chunk;
		if (runNumber > 0) {
			for (int i = 1; i <= runNumber; i++) {
				a = a * 2;
			}
		} else if (runNumber == 0) {
			a = 1;

		}
		chunk = memorySize * a;
		
		int fullchunk = (int) Math.floor((tapeSize) / (chunk * 2));

		int leftover = (tapeSize) - (2 * chunk * fullchunk);

		if (fullchunk == 0) {

			mergeChunks(in1, in2, out1, chunk, leftover - chunk);
		}

		while (fullchunk > 0) {
			b = 0;
			mergeChunks(in1, in2, out1, chunk, chunk);
			fullchunk--;
			if (fullchunk != 0) {
				mergeChunks(in1, in2, out2, chunk, chunk);
				fullchunk--;
				b = 1;
			}
			if (fullchunk == 0 && leftover != 0) {
				if (b == 0) {
					if (leftover >= chunk) {

						mergeChunks(in1, in2, out2, chunk, leftover - chunk);
					} else if (leftover < chunk)

						mergeChunks(in1, in2, out2, leftover, 0);
				}
				if (b == 1) {
					if (leftover < chunk) {

						mergeChunks(in1, in2, out1, leftover, 0);
					} else if (leftover >= chunk) {

						mergeChunks(in1, in2, out1, chunk, leftover - chunk);
					}
				}
			}

		}

		in1.reset();
		in2.reset();
		out1.reset();
		out2.reset();

	}

	public TapeDrive copy(TapeDrive t1, TapeDrive t2) {
		t1.reset();
		t2.reset();
		int size1 = tapeSize(t1);
		for (int i = 0; i < size1; i++) {
			t1.write(t2.read());
		}
		t1.reset();
		t2.reset();
		return t1;
	}

	public TapeDrive zeroTape(TapeDrive t1) {
		int size = tapeSize(t1);
		for (int i = 0; i < size; i++) {
			t1.write(0);
		}
		t1.reset();
		return t1;
	}


	public void sort(TapeDrive t1, TapeDrive t2, TapeDrive t3, TapeDrive t4) {

		double size = tapeSize(t1);
	
		int runs = (int) Math.ceil(Math.log(size/memorySize));
		
		
		initialPass(t1, t2, t3);
		zeroTape(t1);

	
		for (int i = 0; i <runs; i++) {

			if (i == 0) {
				doRun(t2, t3, t1, t4, 0);
			} else if (i % 2 != 0) {
				zeroTape(t2);
				zeroTape(t3);
				doRun(t1, t4, t2, t3, i);
				
				if (i + 1 >= runs) {
					copy(t1, t2);
				}
			} else if (i % 2 == 0) {
				zeroTape(t1);
				zeroTape(t4);
				doRun(t2, t3, t1, t4, i);

			}

		}

	}

	public static void main(String[] args) {
		// Example of how to test
		TapeSorter tapeSorter = new TapeSorter(21, 80);

		TapeDrive t1 = TapeDrive.generateRandomTape(80);
		TapeDrive t2 = new TapeDrive(80);
		TapeDrive t3 = new TapeDrive(80);
		TapeDrive t4 = new TapeDrive(80);

		tapeSorter.sort(t1, t2, t3, t4);

		int last = Integer.MIN_VALUE;
		boolean sorted = true;
		t1.reset();
		for (int i = 0; i < 80; i++) {
			int val = t1.read();
			sorted &= last <= val;
			last = val;

		}
		t2.reset();
		t1.reset();

		
		for (int i = 0; i < 80; i++) {
			System.out.println(t1.read());

		}
		
		if (sorted)
			System.out.println("Sorted!");
		else
			System.out.println("Not sorted!");
	}

}