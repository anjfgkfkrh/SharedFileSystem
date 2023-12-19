#include <stdio.h>

extern char etext;
extern char edata;
extern char end;

int globalInit = 10;
int global;

int sum(int num1, int num2)
{
	int hap = 0;
	hap = num1 + num2;
	
	printf("STACK: hap addr = %u hap = %d \n", &hap, hap);
	printf("STACK: num1 addr = %u num1 = %d \n", &num1, num1);
	printf("STACK: num2 addr = %u num2 = %d \n", &num2, num2);
	
	return hap;
}

int main(void) {
	
	int local = 50;
	static int globalStatic = 55;
	int *mem;
	mem = (int *) malloc(32);
	
	printf("Address of etext: %p %u\n", &etext, &etext);
	printf("Address of edata: %p %u\n", &edata, &edata);
	printf("Address of end: %p %u\n", &end, &end);
	
	printf("DATA: globalInit addr = %u globalInit = %d \n", &globalInit, globalInit);
	printf("DATA: global addr = %u global = %d \n", &global, global);
	printf("DATA: gloalStatic addr = %u globalStatic = %d \n", &globalStatic, globalStatic);
	printf("STACK: local addr = %u local = %d \n", &local, local);
	
	sum(60,32);
	
	int (*kor) (int a, int b);
	kor = sum;
	
	printf("STACL: Function sum addr = %u \n", &kor);
	printf("HEAP: Dynamic mem addr = %u \n", mem);
	
	
	return 0;
}
	
	
	
	
	
	
	
	
	
