/*
 * Project5.c
 *
 * Created: 2025-10-29 오전 9:14:39
 * Author : COMPUTER
 */

#define F_CPU 14745600UL
#include <avr/io.h>
#include <util/delay.h>

// LCD 핀 정의
#define LCD_WDATA	PORTC
#define LCD_WINST	PORTC
#define LCD_CTRL	PORTB
#define LCD_RS		0
#define LCD_RW		1
#define LCD_EN		2

// 함수 선언
void LCD_Data(char ch);
void LCD_Comm(char ch);
void LCD_CHAR(char c);
void LCD_STR(char *str);
void LCD_pos(char col, char row);
void LCD_Clear(void);
void LCD_Init(void);

void Init_USART(void);
unsigned char USART0_rx(void);
int USART_Available(void);

void keypad_Init(void);
char keypad_getkey(void);

char input_buffer[16];
int buf_index = 0;

// ---------------------------- LCD 함수 ----------------------------
void LCD_Data(char ch) {
	LCD_CTRL |= (1<<LCD_RS); 
	LCD_CTRL &= ~(1<<LCD_RW); 
	LCD_CTRL |= (1<<LCD_EN); 
	_delay_us(50); 
	LCD_WDATA = ch; 
	_delay_us(50); 
	LCD_CTRL &= ~(1<<LCD_EN); 
	}
	
void LCD_Comm(char ch) {
	LCD_CTRL &= ~(1<<LCD_RS); 
	LCD_CTRL &= ~(1<<LCD_RW); 
	LCD_CTRL |= (1<<LCD_EN);
	_delay_us(50); 
	LCD_WINST = ch; 
	_delay_us(50); 
	LCD_CTRL &= ~(1<<LCD_EN);
	}
	 
void LCD_CHAR(char c){
	LCD_Data(c); 
	_delay_ms(1);
	}
	
void LCD_STR(char *str){
	while(*str) LCD_CHAR(*str++);
	}
	
void LCD_pos(char col, char row){
	LCD_Comm(0x80 | (row + col*0x40));
	}
	
void LCD_Clear(void){
	LCD_Comm(0x01);
	_delay_ms(2);
	}
	 
void LCD_Init(void){
	LCD_Comm(0x38);
	 _delay_ms(2); 
	 LCD_Comm(0x0E); 
	 _delay_ms(2); 
	 LCD_Comm(0x06); 
	 _delay_ms(2); 
	 LCD_Clear();
	 }

// ---------------------------- USART 함수 ----------------------------
void Init_USART(){
	UCSR0B=(1<<RXEN0)|(1<<TXEN0);
	UCSR0C=(1<<UCSZ01)|(1<<UCSZ00);
	UBRR0L=7;
	}
	
unsigned char USART0_rx(){
	while(!(UCSR0A&(1<<RXC0)));
	return UDR0;
	}
	
int USART_Available(){
	return (UCSR0A&(1<<RXC0));
	}

// ---------------------------- 키패드 함수 ----------------------------
void keypad_Init(){ DDRD = 0xF0; }
char keypad_getkey()
{
    PORTD = 0x10; _delay_us(5);
    if ((PIND & 0x01) == 0x01) return '1';
    if ((PIND & 0x02) == 0x02) return '4';
    if ((PIND & 0x04) == 0x04) return '7';
    if ((PIND & 0x08) == 0x08) return '*';

    PORTD = 0x20; _delay_us(5);
    if ((PIND & 0x01) == 0x01) return '2';
    if ((PIND & 0x02) == 0x02) return '5';
    if ((PIND & 0x04) == 0x04) return '8';
    if ((PIND & 0x08) == 0x08) return '0';

    PORTD = 0x40; _delay_us(5);
    if ((PIND & 0x01) == 0x01) return '3';
    if ((PIND & 0x02) == 0x02) return '6';
    if ((PIND & 0x04) == 0x04) return '9';
    if ((PIND & 0x08) == 0x08) return '#';

    return 0;
}

// ---------------------------- 메인 함수 ----------------------------
int main(void)
{
    DDRC = 0xFF;
    DDRB = 0x0F;
    LCD_Init();
    keypad_Init();
    Init_USART();

    LCD_Clear();
    LCD_pos(0,0);
    LCD_STR("Waiting...");

    while(1)
    {
        // 시리얼로 숫자 들어오면 LCD에 출력
        if(USART_Available())
        {
            char ch = USART0_rx();
            if(ch >= '0' && ch <= '9')
            {
                input_buffer[buf_index++] = ch;
                input_buffer[buf_index] = 0;

                LCD_Clear();
                LCD_pos(0,0);
                LCD_STR("Enter number... ");
                LCD_pos(1,0);
                LCD_STR(input_buffer);		// 받은 숫자들 표시
            }
        }

        // 키패드에서 '0' 누르면 초기화 + Retry 표시
        char key = keypad_getkey();
        if(key == '0')					// 0 누르면 버퍼 초기화
        {
            buf_index = 0;
            input_buffer[0] = 0;	

            LCD_Clear();
            LCD_pos(0,0);
            LCD_STR("Retry");
            _delay_ms(1000);

            LCD_Clear();
            LCD_pos(0,0);
            LCD_STR("Enter number...");
        }
        _delay_ms(50);
    }
}
